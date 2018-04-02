package com.cjsz.tech.book.service;

import com.cjsz.tech.beans.ProxyBookChapter;
import com.cjsz.tech.book.domain.BookChapter;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface BookChapterService {

    public Object pageQuery(Sort sort, Integer page, Integer rows, String searchText);

    public Long saveChapter(final BookChapter chapter);

    public BookChapter findById(Long id);

    public BookChapter findMyChapterById(Long bkid, Long chapterid);

    public List<BookChapter> findList();

    public void deleteById(Long id);

    public void deleteByBookId(Long bookId);

    public void save(BookChapter saveobj);

    public void update(BookChapter updateobj);

    public List<BookChapter> findPdfChapterByBook_id(Long book_id);

    public void deleteChapterByBookAndType(Long book_id, String fmt);

    public void updatePath(Long book_id, Long chapterid, String curpath);

    public List<ProxyBookChapter> findEpubChapterForTwo(String bkid);

    //目录结构化——findEpubChapterForTwo
    public List<ProxyBookChapter> getTreeChapters(String bkid);

    public List<ProxyBookChapter> getChildrenChapters(List<ProxyBookChapter> chapters, Integer pid);

    public List<BookChapter> findEpubChapters(String bookId);

    public List<BookChapter> findPdfChapters(String bookId);

    //目录结构化——findEpubChapters
    public List<BookChapter> getTreeEpubChapters(String bkid);

    //目录结构化——findPdfChapters
    public List<BookChapter> getTreePdfChapters(String bkid);

    public List<BookChapter> getChildrenChaptersOne(List<BookChapter> chapters, Long pid);

    //获取Epub章节目录（url不重复）
    public List<BookChapter> findUniqueUrlEpubChapters(String bkidstr);

    List<Long> findChapterIdsByBKId(Long bkid);


    Integer selectCountChapter(Long book_id);
}