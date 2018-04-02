package com.cjsz.tech.book.service;

import com.cjsz.tech.book.domain.BookTagRel;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21 0021.
 */
public interface BookTagRelService {
    /**
     * 图书Id、标签Id查询关系数据
     * @param tag_id
     * @param book_id
     * @return
     */
    public BookTagRel selectByTagIdAndBookId(Long tag_id, Long book_id);

    /**
     * 给标签添加图书
     * @param rel
     */
    public void saveBookTagRel(BookTagRel rel);

    /**
     * 移除标签下图书
     * @param bookTagRel
     */
    public void deleteBookTagRel(BookTagRel bookTagRel);

    /**
     * 查询标签下是否分配图书
     * @param tag_id
     * @return
     */
    List<BookTagRel> selectById(Long tag_id);
}
