package com.cjsz.tech.book.beans;


import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 图书列表
 * Created by Administrator on 2016/10/25.
 */
public class AppBooksBean {

    private Long book_id;       //图书ID

    private Long book_cat_id;       //图书分类ID

    private Long book_cat_pid;

    private Integer book_type;      //图书类型（1：网文，2：出版）

    private String book_cat_name;       //图书分类名称

    private String book_catname_a;

    private String book_catname_b;

    private String book_name;       //书名

    private String book_author;     //作者

    private String book_cover;     //图书封面

    private String book_cover_small;     //图书封面压缩图

    private String book_isbn;     //ISBN号码

    private Long order_weight;     //排序

    private String book_remark;     //图书描述

    private String book_publisher;  //出版社

    private Double price;      //图书原价

    private Double discount_price;  //图书折扣价

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date end_time;      //截止日期


    public Long getBook_cat_pid() {
        return book_cat_pid;
    }

    public void setBook_cat_pid(Long book_cat_pid) {
        this.book_cat_pid = book_cat_pid;
    }

    public String getBook_catname_a() {
        return book_catname_a;
    }

    public void setBook_catname_a(String book_catname_a) {
        this.book_catname_a = book_catname_a;
    }

    public String getBook_catname_b() {
        return book_catname_b;
    }

    public void setBook_catname_b(String book_catname_b) {
        this.book_catname_b = book_catname_b;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public Long getBook_cat_id() {
        return book_cat_id;
    }

    public void setBook_cat_id(Long book_cat_id) {
        this.book_cat_id = book_cat_id;
    }

    public String getBook_cat_name() {
        if (StringUtils.isEmpty(book_cat_name)){
            if (StringUtils.isNotEmpty(book_catname_b)){
                return book_catname_b;
            }else{
                return book_catname_a;
            }
        }
        return book_cat_name;
    }

    public void setBook_cat_name(String book_cat_name) {
        this.book_cat_name = book_cat_name;
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

    public String getBook_cover_small() {
        return book_cover_small;
    }

    public void setBook_cover_small(String book_cover_small) {
        this.book_cover_small = book_cover_small;
    }

    public String getBook_isbn() {
        return book_isbn;
    }

    public void setBook_isbn(String book_isbn) {
        this.book_isbn = book_isbn;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public String getBook_remark() {
        return book_remark;
    }

    public void setBook_remark(String book_remark) {
        this.book_remark = book_remark;
    }

    public String getBook_publisher() {
        return book_publisher;
    }

    public void setBook_publisher(String book_publisher) {
        this.book_publisher = book_publisher;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(Double discount_price) {
        this.discount_price = discount_price;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public Integer getBook_type() {
        return book_type;
    }

    public void setBook_type(Integer book_type) {
        this.book_type = book_type;
    }
}
