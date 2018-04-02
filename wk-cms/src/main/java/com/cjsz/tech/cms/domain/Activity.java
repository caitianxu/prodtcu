package com.cjsz.tech.cms.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 活动
 * Created by LuoLi on 2017/3/24 0024.
 */
@Entity
@Table(name = "activity")
public class Activity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activity_id;//活动Id

    private Long org_id;//机构ID

    private Long user_id;//系统用户ID

    private String activity_title;//活动标题

    private Long order_weight;//排序

    private Integer activity_status;//1: 启用；2:停用

    private String cover_url;//活动图片URL

    private String cover_url_small;//活动图片压缩

    private Object activity_content;//活动内容

    private Object activity_remark;//活动描述

    private String activity_keywords;//关键字

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;//创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;//最后修改日期

    @JSONField(format = "yyyy-MM-dd")
    private Date start_time;//开始时间

    @JSONField(format = "yyyy-MM-dd")
    private Date end_time;//截止时间

    public Long getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Long activity_id) {
        this.activity_id = activity_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getActivity_title() {
        return activity_title;
    }

    public void setActivity_title(String activity_title) {
        this.activity_title = activity_title;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Integer getActivity_status() {
        return activity_status;
    }

    public void setActivity_status(Integer activity_status) {
        this.activity_status = activity_status;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getCover_url_small() {
        return cover_url_small;
    }

    public void setCover_url_small(String cover_url_small) {
        this.cover_url_small = cover_url_small;
    }

    public Object getActivity_content() {
        return activity_content;
    }

    public void setActivity_content(Object activity_content) {
        this.activity_content = activity_content;
    }

    public Object getActivity_remark() {
        return activity_remark;
    }

    public void setActivity_remark(Object activity_remark) {
        this.activity_remark = activity_remark;
    }

    public String getActivity_keywords() {
        return activity_keywords;
    }

    public void setActivity_keywords(String activity_keywords) {
        this.activity_keywords = activity_keywords;
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

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }
}
