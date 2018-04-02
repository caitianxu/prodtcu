package com.cjsz.tech.cms.beans;

/**
 * Created by LuoLi on 2017/4/24 0024.
 */
public class UpdateCatOrgBean {

    private Long article_cat_id;//分类Id

    private Long org_id;//机构Id

    private Long[] org_ids;

    public Long getArticle_cat_id() {
        return article_cat_id;
    }

    public void setArticle_cat_id(Long article_cat_id) {
        this.article_cat_id = article_cat_id;
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
