package com.cjsz.tech.book.beans;

import java.io.Serializable;

/**
 * 搜索结果
 * Created by shiaihua on 16/9/19.
 */
public class SearchResult implements Serializable {

    private String page_url;

    /**
     * 单词所在页面
     */
    private Integer page;
    /**
     * 单词所在的话
     */
    private String words;

    /**
     * 章节TITLE
     */
    private String title;

    private String chapterid;

    private String bookId;

    private String book_name;

    private String chapter_name;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPage_url() {
        return page_url;
    }

    public void setPage_url(String page_url) {
        this.page_url = page_url;
    }

    public String getChapterid() {
        return chapterid;
    }

    public void setChapterid(String chapterid) {
        this.chapterid = chapterid;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

}
