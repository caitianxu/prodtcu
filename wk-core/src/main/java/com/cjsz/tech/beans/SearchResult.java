package com.cjsz.tech.beans;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * 搜索结果
 * Created by shiaihua on 16/9/19.
 */
@Entity
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
}
