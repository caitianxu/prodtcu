package com.cjsz.tech.book.beans;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;



public class BookReviewBean {


    private Long review_id;//图书评论Id

    private Long pid;//回复的评论Id

    private String full_path;//层路径

    private Long member_id;//会员Id

    private Long book_id;//图书Id

    private Integer book_type;  //图书类型

    private Object review_content;//回复内容

    private Long review_nums;//回复数量

    private Long org_id;//机构Id

    private String device_type_code;//客户端(大屏，ios客户端，Android客户端，web)

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;//回复时间

    private Integer is_delete;//是否删除（1：是  2：否）

    private String book_name;   //图书名称

    private String book_cover_small;   //图书封面

    private String icon;    //会员头像

    private String nick_name;   //昵称

    private Long praise_count;

    private Long praise_id;

    public Long getPraise_count() {
        return praise_count;
    }

    public void setPraise_count(Long praise_count) {
        this.praise_count = praise_count;
    }

    public Long getPraise_id() {
        return praise_id;
    }

    public void setPraise_id(Long praise_id) {
        this.praise_id = praise_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public Long getReview_id() {
        return review_id;
    }

    public void setReview_id(Long review_id) {
        this.review_id = review_id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getFull_path() {
        return full_path;
    }

    public void setFull_path(String full_path) {
        this.full_path = full_path;
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

    public Integer getBook_type() {
        return book_type;
    }

    public void setBook_type(Integer book_type) {
        this.book_type = book_type;
    }

    public Object getReview_content() {
        return review_content;
    }

    public void setReview_content(Object review_content) {
        this.review_content = review_content;
    }

    public Long getReview_nums() {
        return review_nums;
    }

    public void setReview_nums(Long review_nums) {
        this.review_nums = review_nums;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getDevice_type_code() {
        return device_type_code;
    }

    public void setDevice_type_code(String device_type_code) {
        this.device_type_code = device_type_code;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_cover_small() {
        return book_cover_small;
    }

    public void setBook_cover_small(String book_cover_small) {
        this.book_cover_small = book_cover_small;
    }
}
