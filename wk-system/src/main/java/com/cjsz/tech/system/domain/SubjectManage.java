package com.cjsz.tech.system.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/25 0025.
 */
@Entity
@Table(name = "subject_manage")
public class SubjectManage {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long subject_id;    //专题id
    private Long org_id;    //机构id
    private String subject_cover;   //专题封面
    private String subject_name;    //专题名称
    private String subject_remark;  //专题简介
    private String subject_url; //专题外链
    private Integer enabled;    //是否启用
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date update_time;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;
    private Integer is_delete;  //是否删除
    @Transient
    private Integer is_show;    //是否显示
    @Transient
    private Long order_weight;  //排序
    @Transient
    private Integer org_count;  //已分配机构数量


    public Long getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Long subject_id) {
        this.subject_id = subject_id;
    }

    public String getSubject_cover() {
        return subject_cover;
    }

    public void setSubject_cover(String subject_cover) {
        this.subject_cover = subject_cover;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getSubject_remark() {
        return subject_remark;
    }

    public void setSubject_remark(String subject_remark) {
        this.subject_remark = subject_remark;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Integer getIs_show() {
        return is_show;
    }

    public void setIs_show(Integer is_show) {
        this.is_show = is_show;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Integer getOrg_count() {
        return org_count;
    }

    public void setOrg_count(Integer org_count) {
        this.org_count = org_count;
    }

    public String getSubject_url() {
        return subject_url;
    }

    public void setSubject_url(String subject_url) {
        this.subject_url = subject_url;
    }
}
