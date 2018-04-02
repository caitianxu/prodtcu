package com.cjsz.tech.journal.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 期刊详情
 * Created by Administrator on 2016/12/5 0005.
 */
@Entity
@Table(name = "periodical_repo")
public class PeriodicalRepo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long periodical_id;    //期刊ID

    private Long periodical_cat_id;    //分类id

    private String series_name;         //期刊系列

    private String periodical_name;    //期刊名称

    private String file_name;    //上传的期刊名称

    private  String child_file_name; //解析目录

    private String periodical_url;   //期刊路径

    private String periodical_cover;    //期刊封面

    private String periodical_cover_small;   //期刊封面

    private Integer total_page;     //总页数

    private Integer periodical_status; //解析状态（1：未解析，2：已解析）

    private String periodical_remark;    //期刊描述

    private Long order_weight;    //排序

    private Integer enabled;   //(1:启用；2:停用)

    private Integer headline;   //头条(1:是;2:否)

    private Integer recommend;   //推荐(1:是;2:否)

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;    //创建时间

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date update_time;    //更新时间

    @JSONField(format = "yyyy-MM-dd")
    private Date publish_time;    //发布时间

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date upload_time;    //上传时间

    @Transient
    private String periodical_cat_name; //分类名称

    @Transient
    private String periodical_cat_path; //分类path

    public Long getPeriodical_id() {
        return periodical_id;
    }

    public void setPeriodical_id(Long periodical_id) {
        this.periodical_id = periodical_id;
    }

    public Long getPeriodical_cat_id() {
        return periodical_cat_id;
    }

    public void setPeriodical_cat_id(Long periodical_cat_id) {
        this.periodical_cat_id = periodical_cat_id;
    }

    public String getSeries_name() {
        return series_name;
    }

    public void setSeries_name(String series_name) {
        this.series_name = series_name;
    }

    public String getPeriodical_name() {
        return periodical_name;
    }

    public void setPeriodical_name(String periodical_name) {
        this.periodical_name = periodical_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getChild_file_name() {
        return child_file_name;
    }

    public void setChild_file_name(String child_file_name) {
        this.child_file_name = child_file_name;
    }

    public String getPeriodical_url() {
        return periodical_url;
    }

    public void setPeriodical_url(String periodical_url) {
        this.periodical_url = periodical_url;
    }

    public String getPeriodical_cover() {
        return periodical_cover;
    }

    public void setPeriodical_cover(String periodical_cover) {
        this.periodical_cover = periodical_cover;
    }

    public String getPeriodical_cover_small() {
        return periodical_cover_small;
    }

    public void setPeriodical_cover_small(String periodical_cover_small) {
        this.periodical_cover_small = periodical_cover_small;
    }

    public Integer getTotal_page() {
        return total_page;
    }

    public void setTotal_page(Integer total_page) {
        this.total_page = total_page;
    }

    public Integer getPeriodical_status() {
        return periodical_status;
    }

    public void setPeriodical_status(Integer periodical_status) {
        this.periodical_status = periodical_status;
    }

    public String getPeriodical_remark() {
        return periodical_remark;
    }

    public void setPeriodical_remark(String periodical_remark) {
        this.periodical_remark = periodical_remark;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
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

    public Date getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(Date publish_time) {
        this.publish_time = publish_time;
    }

    public Date getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(Date upload_time) {
        this.upload_time = upload_time;
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
