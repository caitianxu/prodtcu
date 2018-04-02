package com.cjsz.tech.cms.beans;

import java.io.Serializable;

/**
 * 资讯模板删除时后台接收参数的实体类
 * Created by Administrator on 2016/11/21 0021.
 */
public class DelArticleTempBean implements Serializable {

    private Integer[]  article_temp_ids;        //模板Id数组

    public Integer[] getArticle_temp_ids() {
        return article_temp_ids;
    }

    public void setArticle_temp_ids(Integer[] article_temp_ids) {
        this.article_temp_ids = article_temp_ids;
    }
}
