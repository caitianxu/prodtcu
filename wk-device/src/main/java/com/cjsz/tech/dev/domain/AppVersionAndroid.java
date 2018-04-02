package com.cjsz.tech.dev.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 版本实体类
 *
 * Created by Li Yi on 2016/12/20.
 */
@Entity
@Table(name = "app_version_android")
public class AppVersionAndroid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long version_id;//版本ID

    private String package_name;//包名

    private String version_code;//版本号

    private String version_name;//版本名称

    private Long app_type_id;//app类型id

    private String publish_url;//APK发布地址

    private String package_url;//APK上传到服务器地址

    private String remark;//版本更新内容描述

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;//创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;//更新时间

    private Integer enabled;//是否使用（1：是；2：否）

    private Integer is_delete;//是否删除

    @Transient
    private String app_type_name;//app类型名称（大众版，企业版）

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public Long getVersion_id() {
        return version_id;
    }

    public void setVersion_id(Long version_id) {
        this.version_id = version_id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getVersion_code() {
        return version_code;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getPublish_url() {
        return publish_url;
    }

    public void setPublish_url(String publish_url) {
        this.publish_url = publish_url;
    }

    public String getPackage_url() {
        return package_url;
    }

    public void setPackage_url(String package_url) {
        this.package_url = package_url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getApp_type_name() {
        return app_type_name;
    }

    public void setApp_type_name(String app_type_name) {
        this.app_type_name = app_type_name;
    }

    public Long getApp_type_id() {
        return app_type_id;
    }

    public void setApp_type_id(Long app_type_id) {
        this.app_type_id = app_type_id;
    }
}
