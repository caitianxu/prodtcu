package com.cjsz.tech.api.beans;

import com.cjsz.tech.core.page.PageList;

/**
 * Created by Administrator on 2017/10/19 0019.
 */
public class BookListJSONBean {

    private Long code;
    private PageJSONBean data;
    private  String msg;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public PageJSONBean getData() {
        return data;
    }

    public void setData(PageJSONBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
