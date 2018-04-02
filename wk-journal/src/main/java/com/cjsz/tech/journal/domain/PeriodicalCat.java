package com.cjsz.tech.journal.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 期刊分类
 * Created by Administrator on 2016/12/5 0005.
 */
@Entity
@Table(name = "periodical_cat")
public class PeriodicalCat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long periodical_cat_id;    //分类ID

    private Long periodical_cat_pid;    //分类上级Id

    private String periodical_cat_name;   //分类名称

    private String periodical_cat_path;   //分类路径

    private Integer enabled;        //是否启用(1:启用,2:不启用)

    private Integer org_count;//使用机构数

    private String remark;  //备注

    private Integer is_delete;//是否删除（1：是；2：否）

    private Long order_weight;  //排序

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;   //最后修改日期

    @Transient
    private Integer is_show;    //是否显示

    @Transient
    private Integer org_is_delete;  //分类分配关系是否删除

    @Transient
    private Long org_order_weight;      //机构排序

    public PeriodicalCat(){

    }

    public PeriodicalCat(Long periodical_cat_pid, String periodical_cat_name, String periodical_cat_path,
                         Integer enabled, Integer org_count, String remark, Integer is_delete,
                         Long order_weight, Date create_time, Date update_time) {
        this.periodical_cat_pid = periodical_cat_pid;
        this.periodical_cat_name = periodical_cat_name;
        this.periodical_cat_path = periodical_cat_path;
        this.enabled = enabled;
        this.org_count = org_count;
        this.remark = remark;
        this.is_delete = is_delete;
        this.order_weight = order_weight;
        this.create_time = create_time;
        this.update_time = update_time;
    }

    public Long getOrg_order_weight() {
        return org_order_weight;
    }

    public void setOrg_order_weight(Long org_order_weight) {
        this.org_order_weight = org_order_weight;
    }

    public Integer getIs_show() {
        return is_show;
    }

    public void setIs_show(Integer is_show) {
        this.is_show = is_show;
    }

    public Long getPeriodical_cat_id() {
        return periodical_cat_id;
    }

    public void setPeriodical_cat_id(Long periodical_cat_id) {
        this.periodical_cat_id = periodical_cat_id;
    }

    public Long getPeriodical_cat_pid() {
        return periodical_cat_pid;
    }

    public void setPeriodical_cat_pid(Long periodical_cat_pid) {
        this.periodical_cat_pid = periodical_cat_pid;
    }

    public String getPeriodical_cat_name() {
        return periodical_cat_name;
    }

    public void setPeriodical_cat_name(String periodical_cat_name) {
        this.periodical_cat_name = periodical_cat_name;
    }

    public String getPeriodical_cat_path() {
        return periodical_cat_path;
    }

    public void setPeriodical_cat_path(String periodical_cat_path) {
        this.periodical_cat_path = periodical_cat_path;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getOrg_count() {
        return org_count;
    }

    public void setOrg_count(Integer org_count) {
        this.org_count = org_count;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
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

    public Integer getOrg_is_delete() {
        return org_is_delete;
    }

    public void setOrg_is_delete(Integer org_is_delete) {
        this.org_is_delete = org_is_delete;
    }
}
