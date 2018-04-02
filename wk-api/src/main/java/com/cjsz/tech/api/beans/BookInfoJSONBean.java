package com.cjsz.tech.api.beans;

import com.cjsz.tech.book.beans.CJZWWPublicBookBean;
import com.cjsz.tech.book.beans.PublicBookBean;

/**
 * Created by Administrator on 2017/10/18 0018.
 */
public class BookInfoJSONBean {

    private Long code;
    private CJZWWPublicBookBean data;
    private String msg;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public CJZWWPublicBookBean getData() {
        return data;
    }

    public void setData(CJZWWPublicBookBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
