package com.cjsz.tech.api.beans;

import com.cjsz.tech.book.beans.CJZWWPublicBookBean;

/**
 * Created by Administrator on 2017/11/21 0021.
 */
public class BookRewardBean {
    private Long code;
    private String[] data;
    private String msg;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
