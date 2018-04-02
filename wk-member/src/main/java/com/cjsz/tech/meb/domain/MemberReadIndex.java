package com.cjsz.tech.meb.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户阅读指数
 * Created by LuoLi on 2017/4/13 0013.
 */
@Entity
@Table(name = "member_read_index")
public class MemberReadIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long member_id;//会员Id

    private Long total_time;//总时长

    private Long total_chapter;//总章节数

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;//日期

    private Long read_index;//阅读指数 = 总时长 * 总章节数

    @Transient
    private String nick_name;//用户昵称

    @Transient
    private Integer is_sys_icon;//是否使用系统头像(1:是;2:否)

    @Transient
    private Long grade_id;//会员等级Id

    @Transient
    private String icon;//用户图像

    @Transient
    private String grade_title;//等级名称

    public MemberReadIndex(){}

    public MemberReadIndex(Long member_id, Long total_time, Long total_chapter, Date create_time, Long read_index){
        this.member_id = member_id;
        this.total_time = total_time;
        this.total_chapter = total_chapter;
        this.create_time = create_time;
        this.read_index = read_index;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
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

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Long getRead_index() {
        return read_index;
    }

    public void setRead_index(Long read_index) {
        this.read_index = read_index;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public Integer getIs_sys_icon() {
        return is_sys_icon;
    }

    public void setIs_sys_icon(Integer is_sys_icon) {
        this.is_sys_icon = is_sys_icon;
    }

    public Long getGrade_id() {
        return grade_id;
    }

    public void setGrade_id(Long grade_id) {
        this.grade_id = grade_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGrade_title() {
        return grade_title;
    }

    public void setGrade_title(String grade_title) {
        this.grade_title = grade_title;
    }
}
