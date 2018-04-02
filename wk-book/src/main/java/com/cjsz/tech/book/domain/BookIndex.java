package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 图书指数
 * Created by LuoLi on 2017/4/15 0015.
 */
@Entity
@Table(name = "book_index")
public class BookIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//

    private Long org_id;//机构Id

    private Long book_id;//图书Id

    private Integer book_type;

    private Long click_count;//点击量

    private Long collect_count;//收藏量

    private Long share_count;//分享量

    private Long review_count;//评论量

    private Long read_count;//阅读量

    private Long unite_index;//综合指数

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;//创建时间

    @Transient
    private String org_name;

    @Transient
    private String book_name;

    public BookIndex(){}

    public BookIndex(Long org_id, Long book_id,Integer book_type, Long click_count, Long collect_count, Long share_count, Long review_count, Long read_count, Long unite_index, Date create_time){
        this.org_id = org_id;
        this.book_id = book_id;
        this.book_type = book_type;
        this.click_count = click_count;
        this.collect_count = collect_count;
        this.share_count = share_count;
        this.review_count = review_count;
        this.read_count = read_count;
        this.unite_index = unite_index;
        this.create_time = create_time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getClick_count() {
        return click_count;
    }

    public void setClick_count(Long click_count) {
        this.click_count = click_count;
    }

    public Long getCollect_count() {
        return collect_count;
    }

    public void setCollect_count(Long collect_count) {
        this.collect_count = collect_count;
    }

    public Long getShare_count() {
        return share_count;
    }

    public void setShare_count(Long share_count) {
        this.share_count = share_count;
    }

    public Long getReview_count() {
        return review_count;
    }

    public void setReview_count(Long review_count) {
        this.review_count = review_count;
    }

    public Long getRead_count() {
        return read_count;
    }

    public void setRead_count(Long read_count) {
        this.read_count = read_count;
    }

    public Long getUnite_index() {
        return unite_index;
    }

    public void setUnite_index(Long unite_index) {
        this.unite_index = unite_index;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
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
}
