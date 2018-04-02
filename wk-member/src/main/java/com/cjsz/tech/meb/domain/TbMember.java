package com.cjsz.tech.meb.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 新会员（统一长江中文网）
 * Created by daixiaofeng on 2017/7/7.
 */

@Entity
@Table(name = "tb_member")
public class TbMember implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;  //会员帐号,手机获取设备编号动态创建【如果有】

    private Long org_id;  //机构ID

    private String phone;  //手机

    private String email;  //邮箱

    private String nick_name; //昵称

    private Long sex; //性别(1：男，2：女)

    private String token; //登录key

    private Long token_type; //token类型(1:pc 2:mobile）

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Long getSex() {
        return sex;
    }

    public void setSex(Long sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getToken_type() {
        return token_type;
    }

    public void setToken_type(Long token_type) {
        this.token_type = token_type;
    }
}
