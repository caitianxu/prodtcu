package com.cjsz.tech.system.conditions;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class AppNavCondition extends PageConditionBean {

    private Long org_id;


    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }
}
