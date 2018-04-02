package com.cjsz.tech.member.beans;

import java.io.Serializable;

/**
 * Author:Jason
 * Date:2017/7/3
 */
public class LoginResultInfo implements Serializable {
    private String userName;
    private Integer nickSex ; //性别
    private String nickName; //昵称
    private String userEmail;
    private String userMobi;
    private String token;
    private String userId;
    private Long org_id;
    private String org_name;
    private String token_type;
    private Long balance;

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobi() {
        return userMobi;
    }

    public void setUserMobi(String userMobi) {
        this.userMobi = userMobi;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getNickSex() {
        return nickSex;
    }

    public void setNickSex(Integer nickSex) {
        this.nickSex = nickSex;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LoginResultInfo{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}
