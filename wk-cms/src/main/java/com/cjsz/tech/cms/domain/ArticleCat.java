package com.cjsz.tech.cms.domain;


import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 资讯分类
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "article_cat")
public class ArticleCat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long article_cat_id;    //分类ID

    private Long org_id;    //机构ID

    private Long pid;        //分类上级Id

    private String article_cat_name;   //分类名称

    private String article_cat_path;   //分类路径

    private Integer enabled;        //是否启用(1:启用,2:不启用)

    private String remark;  //备注

    private String cat_pic; //类型图标

    private Integer is_delete;//是否删除（1：是；2：否）

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;   //最后修改日期

    private Integer org_count;//使用机构数

    public ArticleCat() {
    }

    public ArticleCat(Long org_id, Long pid, String article_cat_name, Integer enabled, String remark, Integer is_delete, Date create_time, Date update_time, Integer org_count) {
        this.org_id = org_id;
        this.pid = pid;
        this.article_cat_name = article_cat_name;
        this.enabled = enabled;
        this.remark = remark;
        this.is_delete = is_delete;
        this.create_time = create_time;
        this.update_time = update_time;
        this.org_count = org_count;
    }

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
}
