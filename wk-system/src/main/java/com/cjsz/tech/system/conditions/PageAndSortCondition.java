package com.cjsz.tech.system.conditions;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Author:Jason
 * Date:2016/11/28
 */
public class PageAndSortCondition extends PageConditionBean {
    private String sort_field;
    private String sort_order;


    public String getSort_field() {
        return sort_field;
    }

    public void setSort_field(String sort_field) {
        this.sort_field = sort_field;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }
}
