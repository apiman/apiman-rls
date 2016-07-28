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
public class RlsInfoBean {
    
    private String name;
    private String version;
    private String description;
    private RlsInfoLinksBean links = new RlsInfoLinksBean();
    
    /**
     * Constructor.
     */
    @SuppressWarnings("nls")
    public RlsInfoBean() {
        this.name = "apiman-rls-api";
        this.version = "1.0";
        this.description = "A REST API to the apiman Rate Limiting [Micro-]Service.  This API provides a way for external clients to quickly and accurately query and increment named rate limits.";
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the links
     */
    public RlsInfoLinksBean getLinks() {
        return links;
    }

    /**
     * @param links the links to set
     */
    public void setLinks(RlsInfoLinksBean links) {
        this.links = links;
    }

}
