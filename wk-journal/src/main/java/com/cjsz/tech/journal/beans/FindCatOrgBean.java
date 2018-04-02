package com.cjsz.tech.journal.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by LuoLi on 2017/4/25 0025.
 */
public class FindCatOrgBean extends PageConditionBean {

    private Long journal_cat_id;

    private Long newspaper_cat_id;

    private Long project_id;

    private String project_code;

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

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }
}
