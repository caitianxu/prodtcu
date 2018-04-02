package com.cjsz.tech.system.beans;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Transient;
import java.util.Date;

/**
 * 资讯详情
 * Created by Administrator on 2016/11/21 0021.
 */
public class SubjectArticleBean {

    private Long article_id;//新闻编码

    private Long article_cat_id;    //分类id

    private Integer article_type;   //类型(0:默认;1:图文;2:外链)

    private String article_title;//新闻标题

    private String article_content; //新闻内容

    @JSONField(format = "yyyy-MM-dd")
    private Date publish_time;

    private Long order_weight;//排序

    private String cover_url_small;//新闻封面图片URL压缩

    private Long subject_id;

    private Long rel_id;

    public Long getArticle_id() {
        return article_id;
    }

    public void setArticle_id(Long article_id) {
        this.article_id = article_id;
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

    public String getCover_url_small() {
        return cover_url_small;
    }

    public void setCover_url_small(String cover_url_small) {
        this.cover_url_small = cover_url_small;
    }

    public Long getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Long subject_id) {
        this.subject_id = subject_id;
    }

    public Long getRel_id() {
        return rel_id;
    }

    public void setRel_id(Long rel_id) {
        this.rel_id = rel_id;
    }

    public Long getArticle_cat_id() {
        return article_cat_id;
    }

    public void setArticle_cat_id(Long article_cat_id) {
        this.article_cat_id = article_cat_id;
    }

    public Integer getArticle_type() {
        return article_type;
    }

    public void setArticle_type(Integer article_type) {
        this.article_type = article_type;
    }

    public String getArticle_content() {
        return article_content;
    }

    public void setArticle_content(String article_content) {
        this.article_content = article_content;
    }

    public Date getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(Date publish_time) {
        this.publish_time = publish_time;
    }
}
