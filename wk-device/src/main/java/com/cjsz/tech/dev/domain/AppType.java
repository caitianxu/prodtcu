package com.cjsz.tech.dev.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@Entity
@Table(name = "app_type")
public class AppType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long app_type_id;
    private String app_type_name;
    private String app_name;
    private String package_name;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date update_time;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;
    private Integer is_delete;
    @Transient
    private Integer org_count;

    public Long getApp_type_id() {
        return app_type_id;
    }

    public void setApp_type_id(Long app_type_id) {
        this.app_type_id = app_type_id;
    }

    public String getApp_type_name() {
        return app_type_name;
    }

    public void setApp_type_name(String app_type_name) {
        this.app_type_name = app_type_name;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Integer getOrg_count() {
        return org_count;
    }

    public void setOrg_count(Integer org_count) {
        this.org_count = org_count;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }
}
