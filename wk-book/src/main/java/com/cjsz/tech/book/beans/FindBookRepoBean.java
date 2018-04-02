package com.cjsz.tech.book.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * 图书仓库图书查询条件
 * Created by Administrator on 2016/12/19 0019.
 */
public class FindBookRepoBean extends PageConditionBean {

    private Long tag_id;        //图书标签Id

    private Boolean bool;       //true:标签下已有的;false:标签下没有的

    private Integer book_status;    //1:上架;2:下架

    public Long getTag_id() {
        return tag_id;
    }

    public void setTag_id(Long tag_id) {
        this.tag_id = tag_id;
    }

    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public Boolean isBool() {
        return bool;
    }

    public Integer getBook_status() {
        return book_status;
    }

    public void setBook_status(Integer book_status) {
        this.book_status = book_status;
    }
}
