package com.cjsz.tech.api.beans;

import com.cjsz.tech.book.domain.BookCat;

import java.util.List;

/**
 * Created by Administrator on 2017/10/21 0021.
 */
public class BookCatJSONBean {
    private Long code;
    private List<BookCat> data;
    private String msg;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public List<BookCat> getData() {
        return data;
    }

    public void setData(List<BookCat> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
