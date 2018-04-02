package com.cjsz.tech.api.beans;

/**
 * Created by Administrator on 2017/10/20 0020.
 */
public class ChapterContentJSONBean {

    private Long code;
    private ChapterContentBean data;
    private String  msg;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public ChapterContentBean getData() {
        return data;
    }

    public void setData(ChapterContentBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
