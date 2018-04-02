package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 图书折扣
 * Created by Administrator on 2017/9/8 0008.
 */
@Entity
@Table(name = "book_discount")
public class BookDiscount  implements Serializable{

    @Id
    private Long id;

    private Long book_id;

    private Integer channel_type;//图书类型（1：网文，2：出版）

    private Double discount_price;//折后价格

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date start_time;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date end_time;

    private Integer enabled;//是否启用（1：是，2：否）

    private Long order_weight;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date update_time;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;

    @Transient
    private String book_name;

    @Transient
    private String book_cover;

    @Transient
    private String book_author;

    @Transient
    private Double price;       //原价

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_cover() {
        return book_cover;
    }

    public void setBook_cover(String book_cover) {
        this.book_cover = book_cover;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public Double getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(Double discount_price) {
        this.discount_price = discount_price;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    public Integer getChannel_type() {
        return channel_type;
    }

    public void setChannel_type(Integer channel_type) {
        this.channel_type = channel_type;
    }

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
