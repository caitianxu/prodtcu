package com.cjsz.tech.meb.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户阅读记录
 * Created by LuoLi on 2017/4/13 0013.
 */
@Entity
@Table(name = "member_read_record")
public class MemberReadRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long record_id;//记录Id

    private Long member_id;//会员Id

    private Long book_id;//图书Id

    private String book_name;

    private String book_cover;

    private String chapter_name;

    private Integer book_type;//图书类型（1：网文，2：出版）

    private Long last_chapter_id;//最后章节Id

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date begin_time;//开始时间

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date end_time;//截止时间

    private Long total_time;//总时长min

    private Long total_chapter;//总章节数

    private String plan;

    private String device_type_code;//设备类型编码(终端)

    private Integer is_delete;

    @Transient
    private String nick_name;

    @Transient
    private String schedule;

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getBook_cover() {
        return book_cover;
    }

    public void setBook_cover(String book_cover) {
        this.book_cover = book_cover;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public MemberReadRecord(){}

    public MemberReadRecord(Long member_id, Long book_id,Integer book_type, Long last_chapter_id){
        this.member_id = member_id;
        this.book_id = book_id;
        this.book_type = book_type;
        this.last_chapter_id = last_chapter_id;
    }

    public Long getRecord_id() {
        return record_id;
    }

    public void setRecord_id(Long record_id) {
        this.record_id = record_id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public Long getLast_chapter_id() {
        return last_chapter_id;
    }

    public void setLast_chapter_id(Long last_chapter_id) {
        this.last_chapter_id = last_chapter_id;
    }

    public Date getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(Date begin_time) {
        this.begin_time = begin_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public Long getTotal_time() {
        return total_time;
    }

    public void setTotal_time(Long total_time) {
        this.total_time = total_time;
    }

    public Long getTotal_chapter() {
        return total_chapter;
    }

    public void setTotal_chapter(Long total_chapter) {
        this.total_chapter = total_chapter;
    }

    public String getDevice_type_code() {
        return device_type_code;
    }

    public void setDevice_type_code(String device_type_code) {
        this.device_type_code = device_type_code;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public Integer getBook_type() {
        return book_type;
    }

    public void setBook_type(Integer book_type) {
        this.book_type = book_type;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }
}
