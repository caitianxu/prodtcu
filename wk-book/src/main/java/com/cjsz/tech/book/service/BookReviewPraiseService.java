package com.cjsz.tech.book.service;

import com.cjsz.tech.book.domain.BookReviewPraise;

/**
 * 图书评论点赞
 * Created by Administrator on 2017/3/16 0016.
 */
public interface BookReviewPraiseService {


    BookReviewPraise getPraiseById(Long review_id, Long member_id);

    void savePraise(Long review_id, Long member_id, Integer praise_count);

    void updatePraise(BookReviewPraise praise, Integer praise_count);
}
