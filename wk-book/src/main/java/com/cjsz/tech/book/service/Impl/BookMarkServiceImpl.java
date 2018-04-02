package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.book.beans.BookMarkBean;
import com.cjsz.tech.book.domain.BookMark;
import com.cjsz.tech.book.mapper.BookMarkMapper;
import com.cjsz.tech.book.service.BookMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by LuoLi on 2017/4/10.
 */
@Service
public class BookMarkServiceImpl implements BookMarkService {

    @Autowired
    private BookMarkMapper bookMarkMapper;

    @Override
    public List<BookMark> getListByMemberId(Long memberId, Long book_id) {
        return bookMarkMapper.getListByMemberId(memberId, book_id);
    }

    @Override
    public void saveBookMark(BookMark bookMark) {
        bookMark.setCreate_time(new Date());
        bookMarkMapper.insert(bookMark);
    }

    @Override
    public void deleteBookMarkByIds(String markIds) {
        bookMarkMapper.deleteBookMarkByIds(markIds);
    }

    @Override
    public BookMark selectByChapId(Long chapter_id,Long member_id,Long book_id){
        return bookMarkMapper.getBookMark(chapter_id,member_id,book_id);
    }

    @Override
    public List<BookMarkBean> getMarkList(Long member_id,Long book_id,Integer book_type){
        return bookMarkMapper.getMarkList(member_id,book_id,book_type);
    }

    @Override
    public Long getBookId(Long chapter_id){
        return bookMarkMapper.getBookId(chapter_id);
    }
}
