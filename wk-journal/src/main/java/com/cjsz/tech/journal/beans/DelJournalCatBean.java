package com.cjsz.tech.journal.beans;

import java.io.Serializable;

/**
 * 期刊分类删除时后台接收参数的实体类
 * Created by Administrator on 2016/11/21 0021.
 */
public class DelJournalCatBean implements Serializable {

    private Long[]  journal_cat_ids;    //期刊分类Id数组
    private String  mark;               //删除信息，用于二次确认

    public Long[] getJournal_cat_ids() {
        return journal_cat_ids;
    }

    public void setJournal_cat_ids(Long[] journal_cat_ids) {
        this.journal_cat_ids = journal_cat_ids;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
