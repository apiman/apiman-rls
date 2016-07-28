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

/**
 * @author eric.wittmann@gmail.com
 */
public class LimitLinksBean {
    
    private String self;
    private String increment;
    private String delete;
    
    /**
     * Constructor.
     */
    public LimitLinksBean() {
    }

    /**
     * @return the self
     */
    public String getSelf() {
        return self;
    }

    /**
     * @param self the self to set
     */
    public void setSelf(String self) {
        this.self = self;
    }

    /**
     * @return the increment
     */
    public String getIncrement() {
        return increment;
    }

    /**
     * @param increment the increment to set
     */
    public void setIncrement(String increment) {
        this.increment = increment;
    }

    /**
     * @return the delete
     */
    public String getDelete() {
        return delete;
    }

    /**
     * @param delete the delete to set
     */
    public void setDelete(String delete) {
        this.delete = delete;
    }

}
