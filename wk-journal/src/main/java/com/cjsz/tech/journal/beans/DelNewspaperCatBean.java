package com.cjsz.tech.journal.beans;

import java.io.Serializable;

/**
 * 分类删除时后台接收参数的实体类
 * Created by Administrator on 2016/11/21 0021.
 */
public class DelNewspaperCatBean implements Serializable {

    private Long[]  newspaper_cat_ids;    //分类Id数组
    private String  mark;               //删除信息，用于二次确认

    public Long[] getNewspaper_cat_ids() {
        return newspaper_cat_ids;
    }

    public void setNewspaper_cat_ids(Long[] newspaper_cat_ids) {
        this.newspaper_cat_ids = newspaper_cat_ids;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
