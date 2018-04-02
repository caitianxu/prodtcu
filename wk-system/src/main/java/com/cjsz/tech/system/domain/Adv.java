package com.cjsz.tech.system.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 广告详情
 * Created by Administrator on 2017/1/22 0022.
 */
@Entity
@Table(name = "adv")
public class Adv implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adv_id;       //广告Id

    private Long adv_cat_id;       //广告分类Id

    private Long org_id;       //机构Id

    private String adv_name;       //广告名称

    private String adv_img;       //广告图片

    private String adv_img_small;       //广告图片压缩

    private String adv_url;       //广告链接

    private Long order_weight;       //排序

    private Integer enabled;       //1：启用；2：停用

    private Object remark;       //描述

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;       //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;       //更新时间

    private Integer is_delete;      //是否删除

    @Transient
    private  String org_name;       //机构名称

    @Transient
    private Integer is_show;    //是否显示

    @Transient
    private Integer org_count;  //机构数量

    @Transient
    private Long org_order_weight;  //机构排序字段

    @Transient
    private Integer org_is_delete;  //分配关系是否删除

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public Long getAdv_id() {
        return adv_id;
    }

    public void setAdv_id(Long adv_id) {
        this.adv_id = adv_id;
    }

    public Long getAdv_cat_id() {
        return adv_cat_id;
    }

    public void setAdv_cat_id(Long adv_cat_id) {
        this.adv_cat_id = adv_cat_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getAdv_name() {
        return adv_name;
    }

    public void setAdv_name(String adv_name) {
        this.adv_name = adv_name;
    }

    public String getAdv_img() {
        return adv_img;
    }

    public void setAdv_img(String adv_img) {
        this.adv_img = adv_img;
    }

    public String getAdv_img_small() {
        return adv_img_small;
    }

    public void setAdv_img_small(String adv_img_small) {
        this.adv_img_small = adv_img_small;
    }

    public String getAdv_url() {
        return adv_url;
    }

    public void setAdv_url(String adv_url) {
        this.adv_url = adv_url;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Object getRemark() {
        return remark;
    }

    public void setRemark(Object remark) {
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

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public Integer getIs_show() {
        return is_show;
    }

    public void setIs_show(Integer is_show) {
        this.is_show = is_show;
    }

    public Integer getOrg_count() {
        return org_count;
    }

    public void setOrg_count(Integer org_count) {
        this.org_count = org_count;
    }

    public Long getOrg_order_weight() {
        return org_order_weight;
    }

    public void setOrg_order_weight(Long org_order_weight) {
        this.org_order_weight = org_order_weight;
    }

    public Integer getOrg_is_delete() {
        return org_is_delete;
    }

    public void setOrg_is_delete(Integer org_is_delete) {
        this.org_is_delete = org_is_delete;
    }
}
