package com.cjsz.tech.book.service;

import com.cjsz.tech.book.beans.BookMarkBean;
import com.cjsz.tech.book.domain.BookMark;

import java.util.List;

/**
 * Created by LuoLi on 2017/4/10.
 */
public interface BookMarkService {

    List<BookMark> getListByMemberId(Long memberId, Long book_id);

    void saveBookMark(BookMark bookMark);

    void deleteBookMarkByIds(String markIds);

    //查询是否有同章节书签
    BookMark selectByChapId(Long chapter_id,Long member_id,Long book_id);

    //获取图书书签列表
    List<BookMarkBean> getMarkList(Long member_id,Long book_id,Integer book_type);

    //根据章节ID获取图书ID
    Long getBookId(Long chapter_id);
}
