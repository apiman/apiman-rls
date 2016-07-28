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
public class RlsInfoLinksBean {

    private String create;
    private String list;
    
    /**
     * Constructor.
     */
    public RlsInfoLinksBean() {
    }

    /**
     * @return the create
     */
    public String getCreate() {
        return create;
    }

    /**
     * @param create the create to set
     */
    public void setCreate(String create) {
        this.create = create;
    }

    /**
     * @return the list
     */
    public String getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(String list) {
        this.list = list;
    }
    
}
