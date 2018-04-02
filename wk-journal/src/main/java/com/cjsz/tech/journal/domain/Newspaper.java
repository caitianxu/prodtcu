package com.cjsz.tech.journal.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jason on 16/3/3.
 * 报纸库
 */
@Entity
@Table(name = "newspaper")
public class Newspaper implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newspaper_id;
    private Long newspaper_cat_id;//报纸分类id
    private Long org_id;
    private Long user_id;
    private String paper_name;
    private String paper_img;
    private String paper_img_small;
    private String paper_url;
    private Long click_num; //报纸点击量
    private String remark;
    private Long order_weight;
    private Integer is_recommend;//是否推荐(1:推荐;2:不推荐)
    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间
    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;   //最后修改日期
    private Integer is_delete;  //是否删除

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public Long getNewspaper_id() {
        return newspaper_id;
    }

    public void setNewspaper_id(Long newspaper_id) {
        this.newspaper_id = newspaper_id;
    }

    public Long getNewspaper_cat_id() {
        return newspaper_cat_id;
    }

    public void setNewspaper_cat_id(Long newspaper_cat_id) {
        this.newspaper_cat_id = newspaper_cat_id;
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

    public String getPaper_name() {
        return paper_name;
    }

    public void setPaper_name(String paper_name) {
        this.paper_name = paper_name;
    }

    public String getPaper_img() {
        return paper_img;
    }

    public void setPaper_img(String paper_img) {
        this.paper_img = paper_img;
    }

    public String getPaper_img_small() {
        return paper_img_small;
    }

    public void setPaper_img_small(String paper_img_small) {
        this.paper_img_small = paper_img_small;
    }

    public String getPaper_url() {
        return paper_url;
    }

    public void setPaper_url(String paper_url) {
        this.paper_url = paper_url;
    }

    public Long getClick_num() {
        return click_num;
    }

    public void setClick_num(Long click_num) {
        this.click_num = click_num;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Integer getIs_recommend() {
        return is_recommend;
    }

    public void setIs_recommend(Integer is_recommend) {
        this.is_recommend = is_recommend;
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
}
