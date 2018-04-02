package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 图书评论点赞记录
 * Created by Administrator on 2017/3/16 0016.
 */
@Entity
@Table(name = "book_review_praise")
public class BookReviewPraise implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long praise_id;//点赞记录id

    private Long review_id;//图书评论Id

    private Long member_id;//会员Id

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;//回复时间

    private Integer is_delete;//是否删除（1：已删除，2：未删除）

    public Long getPraise_id() {
        return praise_id;
    }

    public void setPraise_id(Long praise_id) {
        this.praise_id = praise_id;
    }

    public Long getReview_id() {
        return review_id;
    }

    public void setReview_id(Long review_id) {
        this.review_id = review_id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
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
}
