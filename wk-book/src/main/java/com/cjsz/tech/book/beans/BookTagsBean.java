package com.cjsz.tech.book.beans;

import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.BookTag;

import java.util.List;

/**
 * 图书标签集合
 * Created by Administrator on 2016/12/19 0019.
 */
public class BookTagsBean {

    private List<BookTag> bookTags;

    private List<Long> ids;

    public List<BookTag> getBookTags() {
        return bookTags;
    }

    public void setBookTags(List<BookTag> bookTags) {
        this.bookTags = bookTags;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
