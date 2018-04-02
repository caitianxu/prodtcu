package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.BookReviewBean;
import com.cjsz.tech.book.domain.BookIndexRecord;
import com.cjsz.tech.book.domain.BookReview;
import com.cjsz.tech.book.mapper.BookReviewMapper;
import com.cjsz.tech.book.service.BookIndexRecordService;
import com.cjsz.tech.book.service.BookReviewService;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 图书评论
 * Created by Administrator on 2017/3/16 0016.
 */
@Service
public class BookReviewServiceImpl implements BookReviewService {

    @Autowired
    private BookReviewMapper bookReviewMapper;
    @Autowired
    private BookIndexRecordService bookIndexRecordService;

    @Override
    public Object sitePageQuery(Sort sort, PageConditionBean bean, Long org_id, Long book_id) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<BookReview> reviewList = getReviewTree(org_id, book_id);
        PageList pageList = new PageList(reviewList, null);
        return pageList;
    }

    @Override
    public Object getReviewsList(Sort sort, PageConditionBean bean, Long org_id, Long book_id) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<BookReviewBean> reviewList = bookReviewMapper.getReviewsList(org_id, book_id);
        PageList pageList = new PageList(reviewList, null);
        return pageList;
    }

    @Override
    public Object getOwnReviewQuery(Sort sort, PageConditionBean bean, Long member_id) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<BookReview> reviewList = getOwnReviewTree(member_id);
        PageList pageList = new PageList(reviewList, null);
        return pageList;
    }

    @Override
    public BookReviewBean selectReviewById(Long review_id) {
        return bookReviewMapper.selectReviewById(review_id);
    }

    @Override
    public BookReview selectById(Long review_id) {
        return bookReviewMapper.selectById(review_id);
    }

    @Override
    public BookReview getInfoById(Long review_id) {
        return bookReviewMapper.getInfoById(review_id);
    }

    @Override
    @Transactional
    public void saveBookReview(BookReview review, String token_type) {
        review.setDevice_type_code(token_type);
        review.setIs_delete(2);//是否删除（1：是  2：否）
        review.setPraise_count(0);
        bookReviewMapper.insert(review);
        review.setFull_path("0|" + review.getReview_id() + "|");
        bookReviewMapper.updateByPrimaryKey(review);

        //图书评论，图书指数记录
        //record_type(1:点击详情;2:收藏;3:分享;4:评论;5:阅读);web:000004
        BookIndexRecord bookIndexRecord = new BookIndexRecord(review.getOrg_id(), review.getBook_id(),2, review.getMember_id(), 4, new Date(), token_type);
        bookIndexRecordService.addRecord(bookIndexRecord);
    }

    @Override
    @Transactional
    public void saveBookReviewResponse(BookReview review, String token_type) {
        bookReviewMapper.addReviewNums(review.getPid());
        review.setDevice_type_code(token_type);
        review.setIs_delete(2);//是否删除（1：是  2：否）
        review.setPraise_count(0);
        bookReviewMapper.insert(review);
        review.setFull_path(review.getFull_path() + review.getReview_id() + "|");
        bookReviewMapper.updateByPrimaryKey(review);

        //图书评论，图书指数记录
        //record_type(1:点击详情;2:收藏;3:分享;4:评论;5:阅读);web:000004
        BookIndexRecord bookIndexRecord = new BookIndexRecord(review.getOrg_id(), review.getBook_id(), 2,review.getMember_id(), 4, new Date(), token_type);
        bookIndexRecordService.addRecord(bookIndexRecord);
    }

    @Override
    public List<BookReview> getReviewTree(Long org_id, Long book_id) {
        List<BookReview> firstReviews = bookReviewMapper.selectLevel1ByOrgIdAndBookId(org_id, book_id);
        if(firstReviews.size() == 0){
            return new ArrayList<BookReview>();
        }
        List<BookReview> childReviews = bookReviewMapper.selectSubLevelByOrgIdAndBookId(org_id, book_id);
        for(BookReview bookReview : firstReviews){
            List<BookReview> children = new ArrayList<BookReview>();
            children = getReviewChildren(childReviews, bookReview);
            bookReview.setChildren(children);
        }
        return firstReviews;
    }
    public List<BookReview> getReviewChildren(List<BookReview> bookReviews, BookReview bookReview){
        List<BookReview> children = new ArrayList<BookReview>();
        for(int i = 0; i<bookReviews.size(); i++){
            if(bookReviews.get(i).getPid().equals(bookReview.getReview_id())){
                bookReviews.get(i).setChildren(getChildren(bookReviews, bookReviews.get(i)));
                children.add(bookReviews.get(i));
                bookReviews.remove(bookReviews.get(i));
                i--;
            }
        }
        return children;
    }

    @Override
    public void deleteReview(Long review_id) {
        bookReviewMapper.deleteReview(review_id);
        //查找该评论下是否存在子评论
        List<Integer> reviews = bookReviewMapper.selectByIds(review_id);
        if (reviews != null && reviews.size() >0) {
            String reIds = StringUtils.join(reviews, ",");
            bookReviewMapper.deleteReviews(reIds);
        }
    }


    @Override
    public void deleteReviews(String review_id) {
        bookReviewMapper.deleteReviews(review_id);

    }

    @Override
    public BookReview selectNotMyReviewsByIds(Long member_id, Long review_id) {
        return bookReviewMapper.selectNotMyReviewsById(member_id, review_id);
    }

    @Override
    public List<BookReview> selectNotMyReviewsByIds(Long member_id, String review_ids) {
        return bookReviewMapper.selectNotMyReviewsByIds(member_id, review_ids);
    }

    @Override
    public void deleteReviewChild(Long review_id){
        bookReviewMapper.deleteReview(review_id);
        BookReview bookReview = bookReviewMapper.selectChildById(review_id);
        BookReview bookReview1 = bookReviewMapper.selectChildById(bookReview.getPid());
        //更改主评论回复数量
        Long nums = bookReview1.getReview_nums();
        nums -= 1;
        bookReview1.setReview_nums(nums);
        bookReviewMapper.updateByPrimaryKey(bookReview1);
    }
    @Override
    public Integer getReviewCountByMemberId(Long member_id) {
        return bookReviewMapper.getReviewCountByMemberId(member_id);
    }

    @Override
    public List<BookReview> getReviewsByFullPath(String fullPath) {
        return bookReviewMapper.findReviewsByFullPath(fullPath);
    }

    @Override
    public PageList getReviewsByPid(Sort sort, PageConditionBean bean,Long reviewId) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<BookReviewBean> reviewList = bookReviewMapper.getReviewsByPid(reviewId);
        PageList pageList = new PageList(reviewList, null);
        return pageList;

    }

    public List<BookReview> getOwnReviewTree(Long member_id) {
        List<BookReview> firstReviews = bookReviewMapper.selectFirstByMemberId(member_id);
        if(firstReviews.size() == 0){
            return new ArrayList<BookReview>();
        }
        List<Long> pidList = new ArrayList<Long>();
        for(BookReview view : firstReviews){
            pidList.add(view.getReview_id());
        }
        List<BookReview> childReviews = bookReviewMapper.selectByPid(StringUtils.join(pidList,","));
        for(BookReview bookReview : firstReviews){
            List<BookReview> children = new ArrayList<BookReview>();
            children = getChildren(childReviews, bookReview);
            bookReview.setChildren(children);
        }
        return firstReviews;
    }
    public List<BookReview> getChildren(List<BookReview> bookReviews, BookReview bookReview){
        List<BookReview> children = new ArrayList<BookReview>();
        for(int i = 0; i<bookReviews.size(); i++){
            if(bookReviews.get(i).getPid().equals(bookReview.getReview_id()) && (!bookReview.getMember_id().equals(bookReviews.get(i).getMember_id()))){
                bookReviews.get(i).setChildren(getChildren(bookReviews, bookReviews.get(i)));
                children.add(bookReviews.get(i));
                bookReviews.remove(bookReviews.get(i));
                i--;
            }
        }
        return children;
    }

    @Override
    public Object getMemberReview(Long member_id,Integer pageNum, Integer pageSize,Sort sort){
        PageHelper.startPage(pageNum,pageSize);
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<BookReviewBean> reviewList = bookReviewMapper.getMemberReview(member_id);
        PageList pageList = new PageList(reviewList, null);
        return pageList;

    }

    @Override
    public Object getMemberReviewv2(Long member_id,Integer pageNum, Integer pageSize,Sort sort){
        PageHelper.startPage(pageNum,pageSize);
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<BookReviewBean> reviewList = bookReviewMapper.getMemberReviewv2(member_id);
        PageList pageList = new PageList(reviewList, null);
        return pageList;

    }

    @Override
    public List<BookReviewBean> getBookReviewList(Long book_id,Long org_id){
        return bookReviewMapper.getBookReviewList(book_id,org_id);
    }

    @Override
    public Object getReviewList(Sort sort, PageConditionBean bean, Long org_id, Long book_id,Integer book_type,Long member_id) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<BookReviewBean> reviewList = bookReviewMapper.getReviewList(org_id, book_id,book_type,member_id);
        PageList pageList = new PageList(reviewList, null);
        return pageList;
    }

    @Override
    public Integer selectCountMyReviewsById(Long member_id, String review_ids) {
        return bookReviewMapper.selectCountMyReviewsById(member_id,review_ids);
    }

    @Override
    public void deleteByReviews(String review_ids) {
        bookReviewMapper.deleteByReviews(review_ids);
    }
}
