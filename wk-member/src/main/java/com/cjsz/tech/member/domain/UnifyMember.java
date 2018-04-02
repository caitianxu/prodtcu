package com.cjsz.tech.member.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Author:Jason
 * Date:2017/6/26
 */
@Entity
@Table(name = "tb_member")
public class UnifyMember implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String account; //账号
    private Long org_id;    //机构ID
    private String phone;   //手机号
    private String email;   //邮箱
    private String nick_name;   //昵称
    private Integer sex;//性别(1：男，2：女)
    private String sign;        //签名
    private String token;
    private String token_type; //1:pc端;2:移动端；3：微信
    private String icon;    //头像
    private Long member_id;   //用户ID
    private Integer read_size;  //阅读字号
    private String read_skin;   //阅读背景
    private Long dept_id;       //组织id

    @Transient
    private String org_name;    //机构名称
    @Transient
    private Long balance;   //余额
    @Transient
    private Integer review_num;
    @Transient
    private String dept_name;       //组织名称

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public UnifyMember() {
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public UnifyMember(String account, Long org_id) {
        this.account = account;
        this.org_id = org_id;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "UnifyMember{" +
                "token='" + token + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", org_id=" + org_id +
                ", org_name=" + org_id +
                ", account='" + account + '\'' +
                ", member_id='" + member_id + '\'' +
                ", sex='" + sex + '\'' +
                ", icon='" + icon + '\'' +
                ", token_type='" + token_type + '\'' +
                ", id=" + id +
                '}';
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Integer getReview_num() {
        return review_num;
    }

    public void setReview_num(Integer review_num) {
        this.review_num = review_num;
    }

    public Integer getRead_size() {
        return read_size;
    }

    public void setRead_size(Integer read_size) {
        this.read_size = read_size;
    }

    public String getRead_skin() {
        return read_skin;
    }

    public void setRead_skin(String read_skin) {
        this.read_skin = read_skin;
    }

    public Long getDept_id() {
        return dept_id;
    }

    public void setDept_id(Long dept_id) {
        this.dept_id = dept_id;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }
}
