package com.cjsz.tech.system.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 帮助中心
 * Created by Administrator on 2017/8/7 .
 */
@Entity
@Table(name = "help_center")
public class HelpCenter implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;   //标题

    private String content;    //内容

    private String reply_content;   //回复内容

    private Long create_user;    //创建用户id

    private Long reply_user;     //回复用户id

    @JSONField(format = "yyyy-MM-dd HH:mm:ss ")
    private Date create_time;       //创建时间

    @JSONField(format = "yyyy-MM-dd Hh:mm:ss ")
    private Date reply_time;       //更新时间

    @Transient
    private String create_name;

    @Transient
    private String reply_name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply_content() {
        return reply_content;
    }

    public void setReply_content(String reply_content) {
        this.reply_content = reply_content;
    }

    public Long getCreate_user() {
        return create_user;
    }

    public void setCreate_user(Long create_user) {
        this.create_user = create_user;
    }

    public Long getReply_user() {
        return reply_user;
    }

    public void setReply_user(Long reply_user) {
        this.reply_user = reply_user;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getReply_time() {
        return reply_time;
    }

    public void setReply_time(Date reply_time) {
        this.reply_time = reply_time;
    }

    public String getCreate_name() {
        return create_name;
    }

    public void setCreate_name(String create_name) {
        this.create_name = create_name;
    }

    public String getReply_name() {
        return reply_name;
    }

    public void setReply_name(String reply_name) {
        this.reply_name = reply_name;
    }
}
