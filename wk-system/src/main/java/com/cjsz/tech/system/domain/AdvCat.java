package com.cjsz.tech.system.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 广告分类
 * Created by Administrator on 2017/1/22 0022.
 */
@Entity
@Table(name = "adv_cat")
public class AdvCat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adv_cat_id;       //广告分类Id

    private String project_code;       //项目code

    private Long org_id;       //机构Id

    private String adv_cat_code;        //广告位code

    private String adv_cat_name;       //广告分类名称

    private String adv_cat_remark;     //分类描述

    private Long order_weight;       //排序

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;       //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;       //修改时间

    private Integer is_delete;      //是否删除

    @Transient
    private String project_name;        //项目名称

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public Long getAdv_cat_id() {
        return adv_cat_id;
    }

    public void setAdv_cat_id(Long adv_cat_id) {
        this.adv_cat_id = adv_cat_id;
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getAdv_cat_code() {
        return adv_cat_code;
    }

    public void setAdv_cat_code(String adv_cat_code) {
        this.adv_cat_code = adv_cat_code;
    }

    public String getAdv_cat_name() {
        return adv_cat_name;
    }

    public void setAdv_cat_name(String adv_cat_name) {
        this.adv_cat_name = adv_cat_name;
    }

    public String getAdv_cat_remark() {
        return adv_cat_remark;
    }

    public void setAdv_cat_remark(String adv_cat_remark) {
        this.adv_cat_remark = adv_cat_remark;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
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

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }
}
