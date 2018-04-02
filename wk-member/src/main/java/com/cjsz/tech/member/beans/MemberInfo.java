package com.cjsz.tech.member.beans;

import java.io.Serializable;

/**
 * Author:Jason
 * Date:2017/6/26
 */
public class MemberInfo implements Serializable {
    private  String account;
    private  String pwd;
    private  String token_type;
    private  String clinet_type;
    private  Long dept_id;
    private  Long org_id;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getClinet_type() {
        return clinet_type;
    }

    public void setClinet_type(String clinet_type) {
        this.clinet_type = clinet_type;
    }

    public Long getDept_id() {
        return dept_id;
    }

    public void setDept_id(Long dept_id) {
        this.dept_id = dept_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }
}
