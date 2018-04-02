package com.cjsz.tech.member.beans;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Author:Jason
 * Date:2017/6/26
 */
public class MemberBean implements Serializable {
    private Long id;
    private String account; //账号
    private Long org_id;    //机构ID
    private String phone;   //手机号
    private String email;   //邮箱
    private String nick_name;   //昵称
    private Integer sex;//性别(1：男，2：女)
    private String token;
    private String token_type; //1:pc端;2:移动端；3：微信
    private String icon;    //头像
    private Long member_id;   //用户ID
    private Integer todayTime;  //今日阅读时长（min）
    private Integer time;      //阅读总时长（hour）
    private Integer bookNums;   //阅读总图书数量
    private Integer sum;      //会员总书评数量
    private Integer rank;     //会员阅读排行

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

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public Integer getTodayTime() {
        return todayTime;
    }

    public void setTodayTime(Integer todayTime) {
        this.todayTime = todayTime;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getBookNums() {
        return bookNums;
    }

    public void setBookNums(Integer bookNums) {
        this.bookNums = bookNums;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
