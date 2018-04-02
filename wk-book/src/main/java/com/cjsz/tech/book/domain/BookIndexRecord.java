package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 图书指数记录
 * Created by LuoLi on 2017/4/15 0015.
 */
@Entity
@Table(name = "book_index_record")
public class BookIndexRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long record_id;//记录Id

    private Long org_id;//机构Id

    private Long book_id;//图书Id

    private Integer book_type;//图书类型（1：网文，2：出版）

    private Long member_id;//会员Id

    private Integer record_type;//1:点击详情;2:收藏;3:分享;4:评论;5:阅读

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;//创建时间

    private String device_type_code;//设备类型编码(终端)

    @Transient
    private String book_name;

    @Transient
    private String nick_name;

    @Transient
    private String org_name;

    public BookIndexRecord(){}

    public BookIndexRecord(Long org_id, Long book_id,Integer book_type, Long member_id, Integer record_type, Date create_time, String device_type_code){
        this.org_id = org_id;
        this.book_id = book_id;
        this.book_type = book_type;
        this.member_id = member_id;
        this.record_type = record_type;
        this.create_time = create_time;
        this.device_type_code = device_type_code;
    }

    public Long getRecord_id() {
        return record_id;
    }

    public void setRecord_id(Long record_id) {
        this.record_id = record_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public Integer getRecord_type() {
        return record_type;
    }

    public void setRecord_type(Integer record_type) {
        this.record_type = record_type;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getDevice_type_code() {
        return device_type_code;
    }

    public void setDevice_type_code(String device_type_code) {
        this.device_type_code = device_type_code;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public Integer getBook_type() {
        return book_type;
    }

    public void setBook_type(Integer book_type) {
        this.book_type = book_type;
    }
}
