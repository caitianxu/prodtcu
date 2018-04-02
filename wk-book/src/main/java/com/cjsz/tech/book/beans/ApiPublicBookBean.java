package com.cjsz.tech.book.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Administrator on 2017/9/29 0029.
 */
public class ApiPublicBookBean extends PageConditionBean{

    private String type;
    private Long book_cat_id;
    private String order;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getBook_cat_id() {
        return book_cat_id;
    }

    public void setBook_cat_id(Long book_cat_id) {
        this.book_cat_id = book_cat_id;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
