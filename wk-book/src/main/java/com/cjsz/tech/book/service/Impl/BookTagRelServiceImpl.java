package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.book.domain.BookTagRel;
import com.cjsz.tech.book.mapper.BookTagRelMapper;
import com.cjsz.tech.book.service.BookTagRelService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21 0021.
 */
@Service
public class BookTagRelServiceImpl implements BookTagRelService {

    @Autowired
    private BookTagRelMapper bookTagRelMapper;

    //图书Id、标签Id查询关系数据
    @Override
    public BookTagRel selectByTagIdAndBookId(Long tag_id, Long book_id) {
        return bookTagRelMapper.selectByTagIdAndBookId(tag_id, book_id);
    }

    //给标签添加图书
    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "图书标签关系")
    public void saveBookTagRel(BookTagRel rel) {
        bookTagRelMapper.insert(rel);
    }

    //移除标签下图书
    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "图书标签关系")
    public void deleteBookTagRel(BookTagRel bookTagRel) {
        bookTagRelMapper.deleteBookTagRel(bookTagRel.getBook_id(), bookTagRel.getTag_id());
    }

    @Override
    public List<BookTagRel> selectById(Long tag_id) {
        return bookTagRelMapper.selectById(tag_id);
    }
}
