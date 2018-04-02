package com.cjsz.tech.cms.beans;

import java.io.Serializable;

/**
 * 资讯删除时后台接收参数的实体类
 * Created by Administrator on 2016/11/21 0021.
 */
public class DelArticleBean implements Serializable {

    private Long[]  article_ids;    //资讯Ids

    private Long article_cat_id;

    public Long getArticle_cat_id() {
        return article_cat_id;
    }

    public void setArticle_cat_id(Long article_cat_id) {
        this.article_cat_id = article_cat_id;
    }

    public Long[] getArticle_ids() {
        return article_ids;
    }

    public void setArticle_ids(Long[] article_ids) {
        this.article_ids = article_ids;
    }
}
