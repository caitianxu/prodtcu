package com.cjsz.tech.count.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 项目搜索记录统计
 * Created by LuoLi on 2017/3/23 0023.
 */
@Entity
@Table(name = "search_count")
public class SearchCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long search_id;//搜索Id

    private Long org_id;//机构Id

    private String org_name;//机构名称

    private String name;//搜索内容

    private Long search_count;//搜索次数

    private Long order_weight;

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;//创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;//更新时间

    private Integer status;//状态（1：启用；2：停用）

    public Long getSearch_id() {
        return search_id;
    }

    public void setSearch_id(Long search_id) {
        this.search_id = search_id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSearch_count() {
        return search_count;
    }

    public void setSearch_count(Long search_count) {
        this.search_count = search_count;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
