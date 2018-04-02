package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 图书推荐分类
 */
@Entity
@Table(name = "recommend_cat")
public class RecommendCat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long recommend_cat_id;
    private Long recommend_code;
    private String recommend_type_name;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date update_time;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;
    private Integer is_delete;

    public Long getRecommend_cat_id() {
        return recommend_cat_id;
    }

    public void setRecommend_cat_id(Long recommend_cat_id) {
        this.recommend_cat_id = recommend_cat_id;
    }

    public Long getRecommend_code() {
        return recommend_code;
    }

    public void setRecommend_code(Long recommend_code) {
        this.recommend_code = recommend_code;
    }

    public String getRecommend_type_name() {
        return recommend_type_name;
    }

    public void setRecommend_type_name(String recommend_type_name) {
        this.recommend_type_name = recommend_type_name;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
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
