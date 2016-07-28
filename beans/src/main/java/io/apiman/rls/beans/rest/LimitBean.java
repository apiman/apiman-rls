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

package io.apiman.rls.beans.rest;

import io.apiman.rls.beans.LimitPeriod;

import java.time.ZonedDateTime;

/**
 * @author eric.wittmann@gmail.com
 */
public class LimitBean implements Cloneable {
    
    private String id;
    private long value;
    private long maxValue;
    private LimitPeriod period;
    private long remainingValue;
    private ZonedDateTime resetOn;
    private ZonedDateTime createdOn;
    private ZonedDateTime modifiedOn;
    private LimitLinksBean links;

    /**
     * Constructor.
     */
    public LimitBean() {
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the value
     */
    public long getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(long value) {
        this.value = value;
    }

    /**
     * @return the maxValue
     */
    public long getMaxValue() {
        return maxValue;
    }

    /**
     * @param maxValue the maxValue to set
     */
    public void setMaxValue(long maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * @return the period
     */
    public LimitPeriod getPeriod() {
        return period;
    }

    /**
     * @param period the period to set
     */
    public void setPeriod(LimitPeriod period) {
        this.period = period;
    }

    /**
     * @return the remainingValue
     */
    public long getRemainingValue() {
        return remainingValue;
    }

    /**
     * @param remainingValue the remainingValue to set
     */
    public void setRemainingValue(long remainingValue) {
        this.remainingValue = remainingValue;
    }

    /**
     * @return the resetOn
     */
    public ZonedDateTime getResetOn() {
        return resetOn;
    }

    /**
     * @param resetOn the resetOn to set
     */
    public void setResetOn(ZonedDateTime resetOn) {
        this.resetOn = resetOn;
    }

    /**
     * @return the createdOn
     */
    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    /**
     * @param createdOn the createdOn to set
     */
    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * @return the modifiedOn
     */
    public ZonedDateTime getModifiedOn() {
        return modifiedOn;
    }

    /**
     * @param modifiedOn the modifiedOn to set
     */
    public void setModifiedOn(ZonedDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    /**
     * @return the links
     */
    public LimitLinksBean getLinks() {
        return links;
    }

    /**
     * @param links the links to set
     */
    public void setLinks(LimitLinksBean links) {
        this.links = links;
    }
    
    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public final LimitBean clone() {
        LimitBean clone = new LimitBean();
        clone.setCreatedOn(getCreatedOn());
        clone.setId(getId());
        clone.setLinks(new LimitLinksBean());
        clone.setMaxValue(getMaxValue());
        clone.setModifiedOn(getModifiedOn());
        clone.setPeriod(getPeriod());
        clone.setRemainingValue(getRemainingValue());
        clone.setResetOn(getResetOn());
        clone.setValue(getValue());
        return clone;
    }

}
