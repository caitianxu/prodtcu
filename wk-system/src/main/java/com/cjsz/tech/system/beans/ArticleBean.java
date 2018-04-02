package com.cjsz.tech.system.beans;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 资讯详情
 * Created by Administrator on 2016/11/21 0021.
 */
public class ArticleBean {

    private Long article_id;//新闻编码

    private Long article_cat_id;//类型编码

    private Long org_id;//机构ID

    private Long user_id;//系统用户ID

    private String article_title;//新闻标题

    private Long order_weight;//排序

    private Integer article_status;//(0:未启用;1:启用;2:停用)

    private String cover_url;//新闻封面图片URL

    private String cover_url_small;//新闻封面图片URL压缩

    private Integer article_type;//0:默认;1:图文;2:外链

    private Object article_content;//新闻内容

    private Object article_remark; //新闻描述

    private Long cmt_count; //评论数

    private Long click_count;//点击量

    private String article_keywords;//关键字

    private String article_source; //新闻来源

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time; //最后修改日期

    @JSONField(format = "yyyy-MM-dd")
    private Date publish_time; //发布时间

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time; //创建时间

    private Integer parse_status;   //1:解析;2:未解析;3:解析中

    private Integer headline;       //头条(1:是;2:否)

    private Integer recommend;      //推荐(1:是;2:否)

    private Integer is_delete;      //是否删除

    @Transient
    private String user_name;   //用户名称

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public Long getArticle_id() {
        return article_id;
    }

    public void setArticle_id(Long article_id) {
        this.article_id = article_id;
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

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getArticle_title() {
        return article_title;
    }

    public void setArticle_title(String article_title) {
        this.article_title = article_title;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Integer getArticle_status() {
        return article_status;
    }

    public void setArticle_status(Integer article_status) {
        this.article_status = article_status;
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

    public Integer getArticle_type() {
        return article_type;
    }

    public void setArticle_type(Integer article_type) {
        this.article_type = article_type;
    }

    public Object getArticle_content() {
        return article_content;
    }

    public void setArticle_content(Object article_content) {
        this.article_content = article_content;
    }

    public Object getArticle_remark() {
        return article_remark;
    }

    public void setArticle_remark(Object article_remark) {
        this.article_remark = article_remark;
    }

    public Long getCmt_count() {
        return cmt_count;
    }

    public void setCmt_count(Long cmt_count) {
        this.cmt_count = cmt_count;
    }

    public Long getClick_count() {
        return click_count;
    }

    public void setClick_count(Long click_count) {
        this.click_count = click_count;
    }

    public String getArticle_keywords() {
        return article_keywords;
    }

    public void setArticle_keywords(String article_keywords) {
        this.article_keywords = article_keywords;
    }

    public String getArticle_source() {
        return article_source;
    }

    public void setArticle_source(String article_source) {
        this.article_source = article_source;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Date getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(Date publish_time) {
        this.publish_time = publish_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Integer getParse_status() {
        return parse_status;
    }

    public void setParse_status(Integer parse_status) {
        this.parse_status = parse_status;
    }

    public Integer getHeadline() {
        return headline;
    }

    public void setHeadline(Integer headline) {
        this.headline = headline;
    }

    public Integer getRecommend() {
        return recommend;
    }

    public void setRecommend(Integer recommend) {
        this.recommend = recommend;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
