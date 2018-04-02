package com.cjsz.tech.cms.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by LuoLi on 2017/4/25 0025.
 */
public class FindCatOrgBean extends PageConditionBean {

    private Long article_cat_id;

    private Long project_id;

    private String project_code;

    public Long getArticle_cat_id() {
        return article_cat_id;
    }

    public void setArticle_cat_id(Long article_cat_id) {
        this.article_cat_id = article_cat_id;
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
