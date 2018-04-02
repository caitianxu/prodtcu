package com.cjsz.tech.member.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/10/10 0010.
 */
@Entity
@Table(name = "book_order")
public class BookOrder implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long member_id;
    private Long book_id;
    private String pay_remark;  //订单描述
    private Long pay_cost;    //交易金额
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;
    private Integer is_delete;

    @Transient
    private String book_name;
    @Transient
    private String nick_name;
    @Transient
    private Long total;

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public String getPay_remark() {
        return pay_remark;
    }

    public void setPay_remark(String pay_remark) {
        this.pay_remark = pay_remark;
    }

    public Long getPay_cost() {
        return pay_cost;
    }

    public void setPay_cost(Long pay_cost) {
        this.pay_cost = pay_cost;
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

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
