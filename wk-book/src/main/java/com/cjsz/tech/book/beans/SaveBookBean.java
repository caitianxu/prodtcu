package com.cjsz.tech.book.beans;

import java.util.List;

/**
 * 数据包添加图书
 * Created by Administrator on 2016/12/23 0023.
 */
public class SaveBookBean {

    private Long pkg_id;

    private Long book_cat_id;

    private List<Long> tag_ids;

    private Long book_id;

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

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

    public List<Long> getTag_ids() {
        return tag_ids;
    }

    public void setTag_ids(List<Long> tag_ids) {
        this.tag_ids = tag_ids;
    }
}
