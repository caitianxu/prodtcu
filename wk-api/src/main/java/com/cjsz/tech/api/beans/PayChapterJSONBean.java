package com.cjsz.tech.api.beans;

/**
 * Created by Administrator on 2017/10/20 0020.
 */
public class PayChapterJSONBean {
    private Long code;
    private PayChapterBean data;
    private String msg;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public PayChapterBean getData() {
        return data;
    }

    public void setData(PayChapterBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
