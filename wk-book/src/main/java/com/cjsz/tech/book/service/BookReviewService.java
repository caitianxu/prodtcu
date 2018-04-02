package com.cjsz.tech.book.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.BookReviewBean;
import com.cjsz.tech.book.domain.BookReview;
import com.cjsz.tech.core.page.PageList;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * 图书评论
 * Created by Administrator on 2017/3/16 0016.
 */
public interface BookReviewService {

    /**
     * 图书评论分页列表（tree）
     * @param sort
     * @param bean
     * @param org_id
     * @param book_id
     * @return
     */
    Object sitePageQuery(Sort sort, PageConditionBean bean, Long org_id, Long book_id);

    //v2 获取图书分页列表
    Object getReviewsList(Sort sort, PageConditionBean bean, Long org_id, Long book_id);

    Object getOwnReviewQuery(Sort sort, PageConditionBean bean, Long member_id);

    //v2
    BookReviewBean selectReviewById(Long review_id);

    BookReview selectById(Long review_id);

    BookReview getInfoById(Long review_id);

    //添加评论
    void saveBookReview(BookReview review, String token_type);

    void saveBookReviewResponse(BookReview review, String token_type);

    List<BookReview> getReviewTree(Long org_id, Long book_id);

    //删除评论（同时删除所有子评论）v2
    void deleteReview(Long review_id);

    //删除评论（同时删除所有子评论）
    void deleteReviews(String review_id);

    //删除子评论同时更改主评论回复数量
    void deleteReviewChild(Long review_id);

    BookReview selectNotMyReviewsByIds(Long member_id, Long review_id);

    List<BookReview> selectNotMyReviewsByIds(Long member_id, String review_ids);

    //获取会员评论总数
    Integer getReviewCountByMemberId(Long member_id);

    List<BookReview> getReviewsByFullPath(String fullPath);

    PageList getReviewsByPid(Sort sort, PageConditionBean bean, Long reviewId);

    //获取会员所有书评
    Object getMemberReview(Long member_id,Integer pageNum,Integer pageSize,Sort sort);

    //获取会员所有书评
    Object getMemberReviewv2(Long member_id,Integer pageNum,Integer pageSize,Sort sort);

    //微信端获取最新的前三条评论
    List<BookReviewBean> getBookReviewList(Long book_id,Long org_id);

    Object getReviewList(Sort sort, PageConditionBean bean, Long org_id, Long book_id,Integer book_type,Long member_id);

    //查询会员评论数量
    Integer selectCountMyReviewsById(Long member_id, String review_ids);

    //删除主评论
    void deleteByReviews(String review_ids);
}
