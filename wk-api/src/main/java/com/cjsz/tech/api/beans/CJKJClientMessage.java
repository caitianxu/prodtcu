package com.cjsz.tech.api.beans;


import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 科技公司官网客户留言
 */
@Entity
@Table(name = "cjkj_client_message")
public class CJKJClientMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String client_name;
    private String client_company;
    private String contact;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getClient_company() {
        return client_company;
    }

    public void setClient_company(String client_company) {
        this.client_company = client_company;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
