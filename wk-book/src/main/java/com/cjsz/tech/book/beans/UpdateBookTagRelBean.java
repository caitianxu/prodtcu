package com.cjsz.tech.book.beans;

/**
 * 标签添加、移除图书
 * Created by Administrator on 2016/12/19 0019.
 */
public class UpdateBookTagRelBean {

    private Long tag_id;        //图书标签Id

    private Long book_id;       //图书Id

    private Boolean bool;

    public Long getTag_id() {
        return tag_id;
    }

    public void setTag_id(Long tag_id) {
        this.tag_id = tag_id;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public Boolean isBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }
}
