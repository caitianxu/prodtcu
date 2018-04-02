package com.cjsz.tech.book.beans;

import java.util.List;

/**
 * Created by Administrator on 2017/9/5 0005.
 */
public class CJZWWBookBean {

    private List<BookDetail> bk_list;
    private String count;

    public List<BookDetail> getBk_list() {
        return bk_list;
    }

    public void setBk_list(List<BookDetail> bk_list) {
        this.bk_list = bk_list;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }




}
