package com.cjsz.tech.member.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/12 0012.
 */
@Entity
@Table(name = "tb_member_sign")
public class TbMemberSign implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long member_id;

    private Integer sign_month_count;

    private String sign_gift;

    @JSONField(format = "yyyy-MM-dd")
    private Date sign_time;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;

    @Transient
    private String nick_name;

    @Transient
    private String account;

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

    public String getSign_gift() {
        return sign_gift;
    }

    public void setSign_gift(String sign_gift) {
        this.sign_gift = sign_gift;
    }

    public Date getSign_time() {
        return sign_time;
    }

    public void setSign_time(Date sign_time) {
        this.sign_time = sign_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public Integer getSign_month_count() {
        return sign_month_count;
    }

    public void setSign_month_count(Integer sign_month_count) {
        this.sign_month_count = sign_month_count;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
