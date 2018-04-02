package com.cjsz.tech.journal.beans;

import java.io.Serializable;

/**
 * 期刊删除时后台接收参数的实体类
 * Created by Administrator on 2016/11/21 0021.
 */
public class DelJournalBean implements Serializable {

    private Long[]  journal_ids;    //期刊Ids

    public Long[] getJournal_ids() {
        return journal_ids;
    }

    public void setJournal_ids(Long[] journal_ids) {
        this.journal_ids = journal_ids;
    }
}
