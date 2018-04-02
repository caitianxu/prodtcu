package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.book.domain.BookReviewPraise;
import com.cjsz.tech.book.mapper.BookReviewMapper;
import com.cjsz.tech.book.mapper.BookReviewPraiseMapper;
import com.cjsz.tech.book.service.BookReviewPraiseService;
import com.cjsz.tech.system.beans.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * 图书评论点赞
 * Created by Administrator on 2017/3/16 0016.
 */
@Service
public class BookReviewPraiseServiceImpl implements BookReviewPraiseService {

    @Autowired
    BookReviewPraiseMapper bookReviewPraiseMapper;

    @Autowired
    BookReviewMapper bookReviewMapper;

    @Override
    public BookReviewPraise getPraiseById(Long review_id, Long member_id) {
        return bookReviewPraiseMapper.getPraiseById(review_id, member_id);
    }

    @Override
    public void savePraise(Long review_id, Long member_id, Integer praise_count) {
        BookReviewPraise praise = new BookReviewPraise();
        praise.setReview_id(review_id);
        praise.setMember_id(member_id);
        praise.setCreate_time(new Date());
        praise.setIs_delete(Constants.NOT_DELETE);
        bookReviewPraiseMapper.insert(praise);
        if(null == praise_count){
            praise_count = 0;
        }
        praise_count = praise_count + 1;
        bookReviewMapper.updatePraiseCount(praise_count, review_id);
    }

    @Override
    public void updatePraise(BookReviewPraise praise, Integer praise_count) {
        if (null == praise_count){
            praise_count = 0;
        }
        Integer is_delete = Constants.NOT_DELETE;
        if(Objects.equals(praise.getIs_delete(), Constants.NOT_DELETE)){
            is_delete = Constants.IS_DELETE;
            if(praise_count > 0){
                praise_count = praise_count - 1;
            }
        }else{
            praise_count = praise_count +  1;
        }
        bookReviewPraiseMapper.updatePraiseById(is_delete, praise.getPraise_id());
        bookReviewMapper.updatePraiseCount(praise_count, praise.getReview_id());
    }
}
