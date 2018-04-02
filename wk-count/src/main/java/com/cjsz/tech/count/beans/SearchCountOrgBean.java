package com.cjsz.tech.count.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Administrator on 2017/8/5 0005.
 */
public class SearchCountOrgBean extends PageConditionBean {

    private Long org_id ;

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }
}
