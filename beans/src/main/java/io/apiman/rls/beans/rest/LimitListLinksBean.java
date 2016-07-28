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
public class LimitListLinksBean {
    
    private String self;
    private String prevPage;
    private String nextPage;
    
    /**
     * Constructor.
     */
    public LimitListLinksBean() {
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
     * @return the prevPage
     */
    public String getPrevPage() {
        return prevPage;
    }

    /**
     * @param prevPage the prevPage to set
     */
    public void setPrevPage(String prevPage) {
        this.prevPage = prevPage;
    }

    /**
     * @return the nextPage
     */
    public String getNextPage() {
        return nextPage;
    }

    /**
     * @param nextPage the nextPage to set
     */
    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

}
