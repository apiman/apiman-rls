/*
 * Copyright 2016 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.rls.limits;

import io.apiman.rls.beans.LimitPeriod;
import io.apiman.rls.beans.rest.LimitBean;
import io.apiman.rls.beans.rest.LimitLinksBean;
import io.apiman.rls.beans.rest.LimitListBean;
import io.apiman.rls.beans.rest.NewLimitBean;
import io.apiman.rls.limits.exceptions.LimitExceededException;
import io.apiman.rls.limits.exceptions.LimitNotFoundException;
import io.apiman.rls.limits.exceptions.LimitPeriodConflictException;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the full list of limits, allowing them to be created, deleted, and incremented.  This
 * is intended to be a singleton.
 * 
 * @author eric.wittmann@gmail.com
 */
public class Limits {
    
    private static final Limits sInstance = new Limits();
    public static final Limits getInstance() {
        return sInstance;
    }
    
    private ConcurrentHashMap<String, StoredLimit> limits = new ConcurrentHashMap<>(10000);

    /**
     * Constructor.
     */
    protected Limits() {
    }
    
    /**
     * Creates a new limit (by ID) and returns it.  If the limit already exists,
     * an attempt will be made to increment it by the amount of "value" in the
     * newLimit object.
     * 
     * The caller must pass in a timestamp indicating when this operation was
     * intended to take place.  This is important for operational accuracy (the
     * timestamp indicates the moment the system received the request for the 
     * limit to be createdOrUpdated).
     * 
     * If the limit already exists, it is possible that incrementing it by the 
     * requested amount will result in the limit being exceeded.  In this case
     * a {@link LimitExceededException} is thrown.
     * 
     * If the limit already exists, and the configured "period" of the limit 
     * conflicts with the period requested in "newLimit", then a conflict is
     * detected and a {@link LimitPeriodConflictException} is thrown.
     *  
     * @param when
     * @param newLimit
     * @throws LimitExceededException
     * @throws LimitPeriodConflictException
     */
    public LimitBean createLimit(ZonedDateTime when, NewLimitBean newLimit)
            throws LimitExceededException, LimitPeriodConflictException {
        StoredLimit storedLimit = new StoredLimit();
        LimitBean limit = new LimitBean();
        limit.setId(newLimit.getId());
        limit.setMaxValue(newLimit.getMaxValue());
        limit.setRemainingValue(newLimit.getMaxValue());
        limit.setPeriod(newLimit.getPeriod());
        limit.setCreatedOn(when);
        limit.setModifiedOn(when);
        limit.setResetOn(nextResetTime(when, limit.getPeriod()));
        limit.setLinks(new LimitLinksBean());
        storedLimit.setDetails(limit);
        
        StoredLimit prevStoredLimit = limits.putIfAbsent(newLimit.getId(), storedLimit);
        if (prevStoredLimit != null) {
            storedLimit = prevStoredLimit;
            limit = storedLimit.getDetails();
        }
        
        // Check for period mismatch
        if (limit.getPeriod() != newLimit.getPeriod()) {
            throw new LimitPeriodConflictException();
        }
        
        // Now increment the limit by the indicated amount
        if (newLimit.getValue() > 0) {
            limit = doIncrementLimit(when, storedLimit, newLimit.getValue());
        }
        
        return limit;
    }

    /**
     * Deletes a single limit by its unique ID.
     * @param when
     * @param limitId
     * @throws LimitNotFoundException
     */
    public void deleteLimit(ZonedDateTime when, String limitId) throws LimitNotFoundException {
        if (!limits.containsKey(limitId)) {
            throw new LimitNotFoundException();
        }
        limits.remove(limitId);
    }

    /**
     * Returns a list of limits.
     * @param page
     * @param pageSize
     */
    public LimitListBean listLimits(int page, int pageSize) {
        LimitListBean list = new LimitListBean();
        list.setPage(page);
        list.setPageSize(pageSize);
        list.setTotal(limits.size());

        int startItem = (page - 1) * pageSize;
        int endItem = startItem + pageSize;

        Collection<StoredLimit> values = limits.values();
        Iterator<StoredLimit> iterator = values.iterator();
        int idx = 0;
        while (iterator.hasNext() && idx < endItem) {
            if (idx >= startItem) {
                list.getLimits().add(iterator.next().getDetails());
            }
            idx++;
        }
        
        return list;
    }
    
    /**
     * Increments a single limit by a given amount.
     * @param limitId
     * @param incrementBy
     * @throws LimitNotFoundException
     * @throws LimitExceededException
     */
    public LimitBean incrementLimit(ZonedDateTime when, String limitId, long incrementBy)
            throws LimitNotFoundException, LimitExceededException {
        StoredLimit storedLimit = limits.get(limitId);
        if (storedLimit == null) {
            throw new LimitNotFoundException();
        }
        
        return doIncrementLimit(when, storedLimit, incrementBy);
    }

    /**
     * Returns the information about a single limit.
     * @param limitId
     * @throws LimitNotFoundException
     */
    public LimitBean getLimit(String limitId) throws LimitNotFoundException {
        StoredLimit storedLimit = limits.get(limitId);
        if (storedLimit == null) {
            throw new LimitNotFoundException();
        }
        return storedLimit.getDetails().clone();
    }

    /**
     * Increments a single limit.  If the limit has been exceeded, this will throw
     * an appropriate exception.
     * @param when
     * @param storedLimit
     * @param value
     * @throws LimitExceededException
     */
    protected final static LimitBean doIncrementLimit(ZonedDateTime when, StoredLimit storedLimit, long value) throws LimitExceededException {
        synchronized (storedLimit.getMutex()) {
            LimitBean limit = storedLimit.getDetails();
            
            // Do we need to reset the rate limit?
            if (when.toInstant().toEpochMilli() >= limit.getResetOn().toInstant().toEpochMilli()) {
                limit.setResetOn(nextResetTime(when, limit.getPeriod()));
                limit.setValue(0);
                limit.setRemainingValue(limit.getMaxValue());
            }
            
            // Check to see if we *can* increment the limit
            if (limit.getRemainingValue() < value) {
                throw new LimitExceededException(limit.getResetOn());
            }
            
            // Now do the increment.
            long newValue = limit.getValue() + value;
            limit.setValue(newValue);
            limit.setRemainingValue(limit.getMaxValue() - newValue);
            
            // Set the last-modified-on date
            limit.setModifiedOn(when);
            
            // return a copy of the mutated limit, allowing the caller to update the links
            // and ensure that another thread won't modify it before it can do something with it
            return limit.clone();
        }
    }

    /**
     * Figure out the next reset time from the given date/time.
     * @param when
     * @param period
     */
    protected final static ZonedDateTime nextResetTime(ZonedDateTime when, LimitPeriod period) {
        ZonedDateTime resetOn = when.withNano(0);
        // second, minute, hour, day, month, year
        switch (period) {
        case second:
            resetOn = resetOn.plusSeconds(1);
            break;
        case minute:
            resetOn = resetOn
                .withSecond(0)
                .plusMinutes(1);
            break;
        case hour:
            resetOn = resetOn
                .withSecond(0)
                .withMinute(0)
                .plusHours(1);
            break;
        case day:
            resetOn = resetOn
                .withSecond(0)
                .withMinute(0)
                .withHour(0)
                .plusDays(1);
            break;
        case month:
            resetOn = resetOn
                .withSecond(0)
                .withMinute(0)
                .withHour(0)
                .withDayOfMonth(1)
                .plusMonths(1);
            break;
        case year:
            resetOn = resetOn
                .withSecond(0)
                .withMinute(0)
                .withHour(0)
                .withDayOfYear(1)
                .plusYears(1);
            break;
        default:
            break;
        }
        return resetOn;
    }

}
