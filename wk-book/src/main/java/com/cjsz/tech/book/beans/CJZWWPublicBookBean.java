package com.cjsz.tech.book.beans;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by Administrator on 2017/10/10 0010.
 */
public class CJZWWPublicBookBean {

    private Long book_id;       //图书ID

    private Integer book_type;      //图书类型（1：网文，2：出版）

    private Long book_cat_id;       //图书1级分类ID

    private Long book_cat_pid;      //图书2级分类id

    private String book_cat_name;

    private String book_catname_a;       //图书1级分类名称

    private String book_catname_b;

    private String book_name;       //书名

    private String book_author;     //作者

    private String book_cover;     //图书封面

    private String book_cover_small;     //图书封面压缩图

    private Long shelf_id;          //书架Id

    private String book_remark;     //图书描述

    private Double discount_price;  //折后价

    private Double price;

    private Integer is_finish;  //是否完结

    private Long word_size; //字数

    private String last_chapter; //最新章节

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date end_time;

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

    public Integer getBook_type() {
        return book_type;
    }

    public void setBook_type(Integer book_type) {
        this.book_type = book_type;
    }

    public String getLast_chapter() {
        return last_chapter;
    }

    public void setLast_chapter(String last_chapter) {
        this.last_chapter = last_chapter;
    }

    public Integer getIs_finish() {
        return is_finish;
    }

    public void setIs_finish(Integer is_finish) {
        this.is_finish = is_finish;
    }

    public Long getWord_size() {
        return word_size;
    }

    public void setWord_size(Long word_size) {
        this.word_size = word_size;
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

    public Long getShelf_id() {
        return shelf_id;
    }

    public void setShelf_id(Long shelf_id) {
        this.shelf_id = shelf_id;
    }

    public String getBook_remark() {
        return book_remark;
    }

    public void setBook_remark(String book_remark) {
        this.book_remark = book_remark;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
}
