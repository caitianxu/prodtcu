package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 图书仓库
 * Created by Administrator on 2016/12/19 0019.
 */
@Entity
@Table(name = "book_repo")
public class BookRepo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long book_id;           //图书ID

    private String book_name;       //书名

    private String book_author;     //作者

    private String book_cover;      //图书封面

    private String book_cover_small;      //图书封面压缩的

    private String book_isbn;       //ISBN号码

    private String book_url;        //图书路径

    private String book_publisher;  //出版社

    private String book_remark;     //图书简介

    @JSONField(format = "yyyy-MM-dd")
    private Date publish_time;    //出版时间

    private Double price;       //价格

    private Integer book_status;    //1:上架;2:下架

    @JSONField(format = "yyyy-MM-dd")
    private Date end_time;          //版权截止日期

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;       //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;       //更新时间

    private Integer parse_status;   //解析状态（1:未解析;2:解析中;3:已解析）

    private String file_name;       //图书上传的文件名

    @Transient
    private String tag_names;       //图书标签

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

    public String getBook_url() {
        return book_url;
    }

    public void setBook_url(String book_url) {
        this.book_url = book_url;
    }

    public String getBook_publisher() {
        return book_publisher;
    }

    public void setBook_publisher(String book_publisher) {
        this.book_publisher = book_publisher;
    }

    public String getBook_remark() {
        return book_remark;
    }

    public void setBook_remark(String book_remark) {
        this.book_remark = book_remark;
    }

    public Date getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(Date publish_time) {
        this.publish_time = publish_time;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getBook_status() {
        return book_status;
    }

    public void setBook_status(Integer book_status) {
        this.book_status = book_status;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
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

    public Integer getParse_status() {
        return parse_status;
    }

    public void setParse_status(Integer parse_status) {
        this.parse_status = parse_status;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getTag_names() {
        return tag_names;
    }

    public void setTag_names(String tag_names) {
        this.tag_names = tag_names;
    }
}
