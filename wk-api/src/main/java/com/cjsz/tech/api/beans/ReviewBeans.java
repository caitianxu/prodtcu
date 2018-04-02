package com.cjsz.tech.api.beans;

import com.cjsz.tech.book.beans.BookReviewBean;
import com.cjsz.tech.book.domain.BookReview;
import com.cjsz.tech.core.page.PageList;

/**
 * Created by Administrator on 2017/7/14 0014.
 */
public class ReviewBeans {

    private BookReviewBean bookReview;

    private Object object;

    public BookReviewBean getBookReview() {
        return bookReview;
    }

    public void setBookReview(BookReviewBean bookReview) {
        this.bookReview = bookReview;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
