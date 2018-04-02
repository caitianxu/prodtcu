package com.cjsz.tech.api.beans;

import java.util.List;

/**
 * Created by Administrator on 2017/10/19 0019.
 */
public class BookChapterListBean {
    private Long code;
    private ChapterJSONBean data;
    private String msg;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public ChapterJSONBean getData() {
        return data;
    }

    public void setData(ChapterJSONBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
