package com.cjsz.tech.system.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/25 0025.
 */
@Entity
@Table(name = "subject_article_rel")
public class SubjectArticleRel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long rel_id;
    private Long subject_id;
    private Long article_id;
    private Long order_weight;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;


    public Long getRel_id() {
        return rel_id;
    }

    public void setRel_id(Long rel_id) {
        this.rel_id = rel_id;
    }

    public Long getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Long subject_id) {
        this.subject_id = subject_id;
    }

    public Long getArticle_id() {
        return article_id;
    }

    public void setArticle_id(Long article_id) {
        this.article_id = article_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }
}
