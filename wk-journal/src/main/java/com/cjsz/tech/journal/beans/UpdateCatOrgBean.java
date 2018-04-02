package com.cjsz.tech.journal.beans;

/**
 * Created by LuoLi on 2017/4/24 0024.
 */
public class UpdateCatOrgBean {

    private Long journal_cat_id;//分类Id

    private Long newspaper_cat_id;//分类Id

    private Long org_id;//机构Id

    private Long[] org_ids;

    public Long getJournal_cat_id() {
        return journal_cat_id;
    }

    public void setJournal_cat_id(Long journal_cat_id) {
        this.journal_cat_id = journal_cat_id;
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
