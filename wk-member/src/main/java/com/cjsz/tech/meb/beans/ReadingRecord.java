package com.cjsz.tech.meb.beans;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * 阅读记录
 * Created by daixiaofeng on 2017/7/13.
 */
public class ReadingRecord implements Serializable {

    private String book_cover_small;//图书封面压缩图

    private String name;//章节名称

    private String book_name;//图书名称

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date end_time;//截止时间

    private String schedule;//阅读进度

    private Long book_id;//图书ID

    private Long chapter_id;

    private Integer book_type;

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public String getBook_cover_small() {
        return book_cover_small;
    }

    public void setBook_cover_small(String book_cover_small) {
        this.book_cover_small = book_cover_small;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Integer getBook_type() {
        return book_type;
    }

    public void setBook_type(Integer book_type) {
        this.book_type = book_type;
    }

    public Long getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(Long chapter_id) {
        this.chapter_id = chapter_id;
    }
}
