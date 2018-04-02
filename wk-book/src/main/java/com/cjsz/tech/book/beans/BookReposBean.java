package com.cjsz.tech.book.beans;

import com.cjsz.tech.book.domain.BookRepo;

import java.util.List;

/**
 * 图书仓库图书集合
 * Created by Administrator on 2016/12/19 0019.
 */
public class BookReposBean {

    private List<Long> ids;     //图书Ids

    private Boolean bool;   //true：上架；false：下架

    private List<BookRepo> bookRepos;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public List<BookRepo> getBookRepos() {
        return bookRepos;
    }

    public void setBookRepos(List<BookRepo> bookRepos) {
        this.bookRepos = bookRepos;
    }
}
