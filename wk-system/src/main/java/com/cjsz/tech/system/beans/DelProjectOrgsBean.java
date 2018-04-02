package com.cjsz.tech.system.beans;

import java.io.Serializable;

/**
 * Created by LuoLi on 2017/3/23 0023.
 */
public class DelProjectOrgsBean implements Serializable {

    private Long[] ids;//项目下的机构拓展Ids

    private String  mark;

    public Long[] getIds() {
        return ids;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
