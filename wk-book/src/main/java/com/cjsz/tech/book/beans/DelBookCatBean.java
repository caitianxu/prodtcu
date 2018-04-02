package com.cjsz.tech.book.beans;

import java.io.Serializable;

/**
 * 图书分类删除时后台接收参数的实体类
 * Created by Administrator on 2016/11/21 0021.
 */
public class DelBookCatBean implements Serializable {

    private Long pkg_id;            //数据包Id
    private Long  book_cat_id;    //图书分类Id
    private Long[]  book_cat_ids;    //图书分类Id数组
    private String  mark;               //删除信息，用于二次确认

    public Long getPkg_id() {
        return pkg_id;
    }

    public void setPkg_id(Long pkg_id) {
        this.pkg_id = pkg_id;
    }

    public Long getBook_cat_id() {
        return book_cat_id;
    }

    public void setBook_cat_id(Long book_cat_id) {
        this.book_cat_id = book_cat_id;
    }

    public Long[] getBook_cat_ids() {
        return book_cat_ids;
    }

    public void setBook_cat_ids(Long[] book_cat_ids) {
        this.book_cat_ids = book_cat_ids;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
