package com.cjsz.tech.system.beans;

import javax.persistence.Table;
import java.util.Date;

/**
 * 进店人数APP信息
 * Created by shiaihua on 16/7/1.
 */
@Table(name = "org_intostore_app")
public class EnterStoreApp {

    private Long org_id;

    private String app_id;

    private String app_secret;

    private String access_token;

    private Date access_time;

    private Integer status;


    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getApp_secret() {
        return app_secret;
    }

    public void setApp_secret(String app_secret) {
        this.app_secret = app_secret;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Date getAccess_time() {
        return access_time;
    }

    public void setAccess_time(Date access_time) {
        this.access_time = access_time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
