package com.cjsz.tech.member.beans;

import java.io.Serializable;

/**
 * Author:Jason
 * Date:2017/7/3
 */
public class UserInfo implements Serializable {
    private  String client;
    private String userId;
    private String userName;

    @Override
    public String toString() {
        return "UserInfo{" +
                "client='" + client + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
