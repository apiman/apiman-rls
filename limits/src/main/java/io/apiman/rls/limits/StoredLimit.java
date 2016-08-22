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

import io.apiman.rls.beans.rest.LimitBean;

/**
 * A single limit managed by the Limits class.  This is basically a wrapper around
 * the {@link LimitBean} object so that additional 
 * 
 * @author eric.wittmann@gmail.com
 */
public class StoredLimit {

    private LimitBean details;
//    private transient final Object mutex = new Object();

    /**
     * Constructor.
     */
    public StoredLimit() {
    }

    /**
     * @return the details
     */
    public LimitBean getDetails() {
        return details;
    }

    /**
     * @param details the details to set
     */
    public void setDetails(LimitBean details) {
        this.details = details;
    }

//    /**
//     * @return the mutex
//     */
//    public Object getMutex() {
//        return mutex;
//    }

}
