package com.cjsz.tech.system.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Administrator on 2017/9/25 0025.
 */
public class SubjectOrgBean extends PageConditionBean {
    private Long org_id;

    private Long[] ids;

    private Long subject_id;

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long[] getIds() {
        return ids;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }

    public Long getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Long subject_id) {
        this.subject_id = subject_id;
    }
}
