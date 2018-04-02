package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 图书组织关系
 * Created by Administrator on 2016/12/21 0021.
 */
@Entity
@Table(name = "book_org_rel")
public class BookOrgRel {

    private Long book_id;       //图书ID

    private Long org_id;        //机构ID

    private Long book_cat_id;        //图书分类ID

    private Long pkg_id;        //数据包ID

    private Long order_weight;      //排序

    private Integer is_hot;         //是否热门(1:是;2:否)

    private Integer is_recommend;       //是否推荐(1:是;2:否)

    private Integer offline_status;     //0:不离线;1:发送离线

    private Long click_count;       //点击量

    private Long collect_count;     //收藏数

    private Long share_count;       //分享数

    private Long review_count;      //评论数

    private Long read_count;      //阅读数

    private Integer enabled;     //是否启用(1: 启用；2:停用)

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;       //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;     //最后修改时间

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getBook_cat_id() {
        return book_cat_id;
    }

    public void setBook_cat_id(Long book_cat_id) {
        this.book_cat_id = book_cat_id;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Integer getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(Integer is_hot) {
        this.is_hot = is_hot;
    }

    public Integer getIs_recommend() {
        return is_recommend;
    }

    public void setIs_recommend(Integer is_recommend) {
        this.is_recommend = is_recommend;
    }

    public Integer getOffline_status() {
        return offline_status;
    }

    public void setOffline_status(Integer offline_status) {
        this.offline_status = offline_status;
    }

    public Long getClick_count() {
        return click_count;
    }

    public void setClick_count(Long click_count) {
        this.click_count = click_count;
    }

    public Long getCollect_count() {
        return collect_count;
    }

    public void setCollect_count(Long collect_count) {
        this.collect_count = collect_count;
    }

    public Long getShare_count() {
        return share_count;
    }

    public void setShare_count(Long share_count) {
        this.share_count = share_count;
    }

    public Long getReview_count() {
        return review_count;
    }

    public void setReview_count(Long review_count) {
        this.review_count = review_count;
    }

    public Long getRead_count() {
        return read_count;
    }

    public void setRead_count(Long read_count) {
        this.read_count = read_count;
    }

    public Long getPkg_id() {
        return pkg_id;
    }

    public void setPkg_id(Long pkg_id) {
        this.pkg_id = pkg_id;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
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
