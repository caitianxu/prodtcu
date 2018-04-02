package com.cjsz.tech.api.beans;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 书架
 * Created by shiaihua on 16/12/21.
 */
@Entity
@Table(name = "book_shelf")
public class ShelfBook implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shelf_id;

    private Long bk_id;

    private Integer book_type;

    private String bk_name;

    private String bk_cover;

    private String bk_cover_small;

    private String bk_url;

    private String bk_author;

    private Integer order_weight;

    private Long user_id;
//    private Long org_id;
    private String user_name;
    private Integer is_delete;      //是否删除（1：是  2：否）

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;

    private String schedule;       //进度

    @Transient
    private String book_publisher;//出版社

    public Long getShelf_id() {
        return shelf_id;
    }

    public void setShelf_id(Long shelf_id) {
        this.shelf_id = shelf_id;
    }

    public Long getBk_id() {
        return bk_id;
    }

    public void setBk_id(Long bk_id) {
        this.bk_id = bk_id;
    }

    public String getBk_name() {
        return bk_name;
    }

    public void setBk_name(String bk_name) {
        this.bk_name = bk_name;
    }

    public String getBk_cover() {
        return bk_cover;
    }

    public void setBk_cover(String bk_cover) {
        this.bk_cover = bk_cover;
    }

    public String getBk_cover_small() {
        return bk_cover_small;
    }

    public void setBk_cover_small(String bk_cover_small) {
        this.bk_cover_small = bk_cover_small;
    }

    public String getBk_url() {
        return bk_url;
    }

    public void setBk_url(String bk_url) {
        this.bk_url = bk_url;
    }

    public String getBk_author() {
        return bk_author;
    }

    public void setBk_author(String bk_author) {
        this.bk_author = bk_author;
    }

    public Integer getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Integer order_weight) {
        this.order_weight = order_weight;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Integer getBook_type() {
        return book_type;
    }

    public void setBook_type(Integer book_type) {
        this.book_type = book_type;
    }

  /*public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }*/

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getBook_publisher() {
        return book_publisher;
    }

    public void setBook_publisher(String book_publisher) {
        this.book_publisher = book_publisher;
    }
}
