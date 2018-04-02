package com.cjsz.tech.book.beans;

import java.util.List;

/**
 * Created by Administrator on 2017/10/21 0021.
 */
public class BookRecommendListBean {

    private String code;
    private List<BookRecommend> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<BookRecommend> getData() {
        return data;
    }

    public void setData(List<BookRecommend> data) {
        this.data = data;
    }
}
