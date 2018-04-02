package com.cjsz.tech.journal.beans;

import java.io.Serializable;

/**
 * 期刊分类删除时后台接收参数的实体类
 * Created by Administrator on 2016/11/21 0021.
 */
public class DelPeriodicalCatBean implements Serializable {

    private Long[]  periodical_cat_ids;    //期刊分类Id数组
    private String  mark;               //删除信息，用于二次确认

    public Long[] getPeriodical_cat_ids() {
        return periodical_cat_ids;
    }

    public void setPeriodical_cat_ids(Long[] periodical_cat_ids) {
        this.periodical_cat_ids = periodical_cat_ids;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
