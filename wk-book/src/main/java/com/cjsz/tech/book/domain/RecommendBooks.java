package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 图书推荐图书列表
 */
@Entity
@Table(name = "recommend_books")
public class RecommendBooks implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private Long recommend_cat_id;
    private Integer book_type;
    private Long book_id;
    private String book_cover;
    private String book_name;
    private String book_author;
    private String book_remark;
    private Long order_weight;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBook_remark() {
        return book_remark;
    }

    public void setBook_remark(String book_remark) {
        this.book_remark = book_remark;
    }

    public Integer getBook_type() {
        return book_type;
    }

    public void setBook_type(Integer book_type) {
        this.book_type = book_type;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public String getBook_cover() {
        return book_cover;
    }

    public void setBook_cover(String book_cover) {
        this.book_cover = book_cover;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Long getRecommend_cat_id() {
        return recommend_cat_id;
    }

    public void setRecommend_cat_id(Long recommend_cat_id) {
        this.recommend_cat_id = recommend_cat_id;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }
}
