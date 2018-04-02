package com.cjsz.tech.journal.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by henry on 2017/8/8.
 */
@Entity
@Table(name = "un_periodical")
public class UnPeriodical {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid;
    private Long user_id;
    private String s_title;
    private String s_path;
    private Integer s_num;
    private Integer un_success;
    private Integer un_error;
    private Integer s_status;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date end_time;

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getS_title() {
        return s_title;
    }

    public void setS_title(String s_title) {
        this.s_title = s_title;
    }

    public String getS_path() {
        return s_path;
    }

    public void setS_path(String s_path) {
        this.s_path = s_path;
    }

    public Integer getS_num() {
        return s_num;
    }

    public void setS_num(Integer s_num) {
        this.s_num = s_num;
    }

    public Integer getUn_success() {
        return un_success;
    }

    public void setUn_success(Integer un_success) {
        this.un_success = un_success;
    }

    public Integer getUn_error() {
        return un_error;
    }

    public void setUn_error(Integer un_error) {
        this.un_error = un_error;
    }

    public Integer getS_status() {
        return s_status;
    }

    public void setS_status(Integer s_status) {
        this.s_status = s_status;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }
}
