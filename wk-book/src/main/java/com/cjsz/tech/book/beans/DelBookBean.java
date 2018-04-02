package com.cjsz.tech.book.beans;

import java.io.Serializable;

/**
 * 期刊删除时后台接收参数的实体类
 * Created by Administrator on 2016/11/21 0021.
 */
public class DelBookBean implements Serializable {

    private Long[]  ids;    //图书Ids

    private Integer enabled;

    private Long id;

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Long[] getIds() {
        return ids;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
