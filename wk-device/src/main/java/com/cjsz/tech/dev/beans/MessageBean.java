package com.cjsz.tech.dev.beans;

import com.cjsz.tech.beans.PageConditionBean;

public class MessageBean extends PageConditionBean {
    private Long dept_id;   //部分id
    private Long org_id;    //机构id

    public Long getDept_id() {
        return dept_id;
    }

    public void setDept_id(Long dept_id) {
        this.dept_id = dept_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }
}
