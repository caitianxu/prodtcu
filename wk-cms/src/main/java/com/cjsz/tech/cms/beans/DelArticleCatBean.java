package com.cjsz.tech.cms.beans;

import java.io.Serializable;

/**
 * 资讯分类删除时后台接收参数的实体类
 * Created by Administrator on 2016/11/21 0021.
 */
public class DelArticleCatBean implements Serializable {

    private Long[]  article_cat_ids;    //资讯分类Id数组
    private String  mark;               //删除信息，用于二次确认

    public Long[] getArticle_cat_ids() {
        return article_cat_ids;
    }

    public void setArticle_cat_ids(Long[] article_cat_ids) {
        this.article_cat_ids = article_cat_ids;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
