package com.cjsz.tech.cms.beans;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

/**
 * Created by LuoLi on 2017/4/24 0024.
 */
public class ArticleCatBean {

    private Long article_cat_id;    //分类ID

    private Long org_id;    //机构ID

    private Long pid;        //分类上级Id

    private String article_cat_name;   //分类名称

    private String article_cat_path;   //分类路径

    private Integer enabled;        //是否启用(1:启用,2:不启用)

    private String remark;  //备注

    private String cat_pic; //类型图标

    private Integer is_delete;//原始分类是否删除（1：是；2：否）

    private Integer org_is_delete;//分类机构关系是否删除（1：是；2：否）

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;   //最后修改日期

    private Integer org_count;//使用机构数

    private Long rel_id;//资讯分类机构关系Id

    private Long order_weight;//排序

    private Integer is_show;//是否显示（1：是；2：否）

    private Long source_id;//来源Id

    private Integer source_type;//是否自建(1:自建;2:分配)

    private List<ArticleCatBean> children;

    public Long getArticle_cat_id() {
        return article_cat_id;
    }

    public void setArticle_cat_id(Long article_cat_id) {
        this.article_cat_id = article_cat_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getArticle_cat_name() {
        return article_cat_name;
    }

    public void setArticle_cat_name(String article_cat_name) {
        this.article_cat_name = article_cat_name;
    }

    public String getArticle_cat_path() {
        return article_cat_path;
    }

    public void setArticle_cat_path(String article_cat_path) {
        this.article_cat_path = article_cat_path;
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

    public String getCat_pic() {
        return cat_pic;
    }

    public void setCat_pic(String cat_pic) {
        this.cat_pic = cat_pic;
    }

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public Integer getOrg_is_delete() {
        return org_is_delete;
    }

    public void setOrg_is_delete(Integer org_is_delete) {
        this.org_is_delete = org_is_delete;
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

    public Long getSource_id() {
        return source_id;
    }

    public void setSource_id(Long source_id) {
        this.source_id = source_id;
    }

    public Integer getSource_type() {
        return source_type;
    }

    public void setSource_type(Integer source_type) {
        this.source_type = source_type;
    }

    public List<ArticleCatBean> getChildren() {
        return children;
    }

    public void setChildren(List<ArticleCatBean> children) {
        this.children = children;
    }
}
