package com.cjsz.tech.journal.beans;

/**
 * Created by LuoLi on 2017/4/24 0024.
 */
public class UpdateNewCatOrgBean {

    private Long periodical_cat_id;//分类Id

    private Long newspaper_cat_id;//分类Id

    private Long org_id;//机构Id

    private Long[] org_ids;

    public Long getPeriodical_cat_id() {
        return periodical_cat_id;
    }

    public void setPeriodical_cat_id(Long periodical_cat_id) {
        this.periodical_cat_id = periodical_cat_id;
    }

    public Long getNewspaper_cat_id() {
        return newspaper_cat_id;
    }

    public void setNewspaper_cat_id(Long newspaper_cat_id) {
        this.newspaper_cat_id = newspaper_cat_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long[] getOrg_ids() {
        return org_ids;
    }

    public void setOrg_ids(Long[] org_ids) {
        this.org_ids = org_ids;
    }
}
