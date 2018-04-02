package com.cjsz.tech.journal.beans;

import java.io.Serializable;

/**
 * 报纸删除时后台接收参数的实体类
 * Created by Administrator on 2016/11/21 0021.
 */
public class DelNewspaperBean implements Serializable {

    private Long[]  newspaper_ids;    //报纸Ids

    public Long[] getNewspaper_ids() {
        return newspaper_ids;
    }

    public void setNewspaper_ids(Long[] newspaper_ids) {
        this.newspaper_ids = newspaper_ids;
    }
}
