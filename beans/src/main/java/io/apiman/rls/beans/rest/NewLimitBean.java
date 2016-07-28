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

import java.time.ZoneId;

/**
 * @author eric.wittmann@gmail.com
 */
public class NewLimitBean {
    
    private String id;
    private long value;
    private long maxValue;
    private LimitPeriod period;
    private ZoneId tz;
    
    /**
     * Constructor.
     */
    public NewLimitBean() {
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
     * @return the tz
     */
    public ZoneId getTz() {
        return tz;
    }

    /**
     * @param tz the tz to set
     */
    public void setTz(ZoneId tz) {
        this.tz = tz;
    }

}
