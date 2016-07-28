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

import java.util.ArrayList;
import java.util.List;

/**
 * @author eric.wittmann@gmail.com
 */
public class LimitListBean {
    
    private int page;
    private int pageSize;
    private int total;
    private List<LimitBean> limits = new ArrayList<LimitBean>();
    private LimitListLinksBean links = new LimitListLinksBean();

    /**
     * Constructor.
     */
    public LimitListBean() {
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the limits
     */
    public List<LimitBean> getLimits() {
        return limits;
    }

    /**
     * @param limits the limits to set
     */
    public void setLimits(List<LimitBean> limits) {
        this.limits = limits;
    }

    /**
     * @return the links
     */
    public LimitListLinksBean getLinks() {
        return links;
    }

    /**
     * @param links the links to set
     */
    public void setLinks(LimitListLinksBean links) {
        this.links = links;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

}
