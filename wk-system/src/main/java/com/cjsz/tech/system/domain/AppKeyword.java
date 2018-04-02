package com.cjsz.tech.system.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.cjsz.tech.beans.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * 关键词实体类
 * Created by Administrator on 2016/11/30 0030.
 */
@Entity
@Table(name = "app_keyword")
public class AppKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keyword_id;    //关键词ID

    private String keyword_name;    //关键词名称

    private Long order_weight;  //排序

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间

    public Long getKeyword_id() {
        return keyword_id;
    }

    public void setKeyword_id(Long keyword_id) {
        this.keyword_id = keyword_id;
    }

    public String getKeyword_name() {
        return keyword_name;
    }

    public void setKeyword_name(String keyword_name) {
        this.keyword_name = keyword_name;
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
}
