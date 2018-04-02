package com.cjsz.tech.journal.beans;

import com.alibaba.fastjson.annotation.JSONField;
import com.cjsz.tech.beans.PageConditionBean;

import java.util.Date;
import java.util.List;

/**
 * Created by LuoLi on 2017/4/24 0024.
 */
public class PeriodicalCatBean extends PageConditionBean{

    private Long periodical_cat_id;    //分类ID

    private Long org_id;    //机构ID

    private Long periodical_cat_pid;        //分类上级Id

    private String periodical_cat_name;   //分类名称

    private String periodical_cat_path;   //分类路径

    private Integer enabled;        //是否启用(1:启用,2:不启用)

    private String remark;  //备注

    private Integer is_delete;//原始分类是否删除（1：是；2：否）

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;   //最后修改日期

    private Integer org_count;//使用机构数

    private Long rel_id;//分类机构关系Id

    private Long order_weight;//排序

    private Integer is_show;//是否显示（1：是；2：否）

    private List<PeriodicalCatBean> children;


    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
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

    public Integer getOrg_count() {
        return org_count;
    }

    public void setOrg_count(Integer org_count) {
        this.org_count = org_count;
    }

    public Long getRel_id() {
        return rel_id;
    }

    public void setRel_id(Long rel_id) {
        this.rel_id = rel_id;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Integer getIs_show() {
        return is_show;
    }

    public void setIs_show(Integer is_show) {
        this.is_show = is_show;
    }

    public List<PeriodicalCatBean> getChildren() {
        return children;
    }

    public void setChildren(List<PeriodicalCatBean> children) {
        this.children = children;
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
}
