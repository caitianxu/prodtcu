package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 网文图书同步
 * Created by Administrator on 2016/12/21 0021.
 */
@Entity
@Table(name = "cjzww_books")
public class CJZWWBooks implements Serializable {

    @Id
    private Long book_id;       //图书id
    private String book_name;   //书名
    private String book_author; //作者
    private String book_cover;  //封面url
    private String book_remark;     //简介
    private String book_cat_name;
    private Long word_size;      //字数
    private Integer is_finish;      //是否完结

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date update_time;       //最后更新时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;       //创建时间

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
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

    public String getBook_cover() {
        return book_cover;
    }

    public void setBook_cover(String book_cover) {
        this.book_cover = book_cover;
    }

    public String getBook_remark() {
        return book_remark;
    }

    public void setBook_remark(String book_remark) {
        this.book_remark = book_remark;
    }

    public Long getWord_size() {
        return word_size;
    }

    public void setWord_size(Long word_size) {
        this.word_size = word_size;
    }

    public Integer getIs_finish() {
        return is_finish;
    }

    public void setIs_finish(Integer is_finish) {
        this.is_finish = is_finish;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getBook_cat_name() {
        return book_cat_name;
    }

    public void setBook_cat_name(String book_cat_name) {
        this.book_cat_name = book_cat_name;
    }
}
