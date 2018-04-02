package com.cjsz.tech.api.beans;

import java.util.List;

/**
 * Created by Administrator on 2017/10/20 0020.
 */
public class ChapterJSONBean {
    private Integer chapter_count;
    private Integer all_count;
    private List<ChapterListJSONBean> chapters;

    public Integer getChapter_count() {
        return chapter_count;
    }

    public void setChapter_count(Integer chapter_count) {
        this.chapter_count = chapter_count;
    }

    public Integer getAll_count() {
        return all_count;
    }

    public void setAll_count(Integer all_count) {
        this.all_count = all_count;
    }

    public List<ChapterListJSONBean> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterListJSONBean> chapters) {
        this.chapters = chapters;
    }
}
