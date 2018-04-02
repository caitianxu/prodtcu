package com.cjsz.tech.journal.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 期刊详情
 * Created by Administrator on 2016/12/5 0005.
 */
@Entity
@Table(name = "journal")
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long journal_id;    //期刊ID

    private Long journal_cat_id;    //期刊分类

    private Long org_id;    //机构ID

    private Long user_id;   //用户ID

    private String journal_name;    //期刊名称

    private String journal_cover;   //期刊封面

    private String journal_cover_small;   //期刊封面压缩

    private String android_qr_code;   //安卓二维码

    private String android_qr_code_small;   //安卓二维码压缩图

    private String ios_qr_code;   //ios二维码

    private String ios_qr_code_small;   //ios二维码压缩图

    private Long order_weight;    //排序

    private String journal_desc;    //期刊描述

    private Integer journal_status;   //(1:启用；2:停用)

    private Integer headline;   //头条(1:是;2:否)

    private Integer recommend;   //推荐(1:是;2:否)

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;    //创建时间
    
    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;    //更新时间

    @JSONField(format = "yyyy-MM-dd")
    private Date publish_time;    //发布时间

    public Long getJournal_id() {
        return journal_id;
    }

    public void setJournal_id(Long journal_id) {
        this.journal_id = journal_id;
    }

    public Long getJournal_cat_id() {
        return journal_cat_id;
    }

    public void setJournal_cat_id(Long journal_cat_id) {
        this.journal_cat_id = journal_cat_id;
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

    public String getJournal_name() {
        return journal_name;
    }

    public void setJournal_name(String journal_name) {
        this.journal_name = journal_name;
    }

    public String getJournal_cover() {
        return journal_cover;
    }

    public void setJournal_cover(String journal_cover) {
        this.journal_cover = journal_cover;
    }

    public String getJournal_cover_small() {
        return journal_cover_small;
    }

    public void setJournal_cover_small(String journal_cover_small) {
        this.journal_cover_small = journal_cover_small;
    }

    public String getAndroid_qr_code() {
        return android_qr_code;
    }

    public void setAndroid_qr_code(String android_qr_code) {
        this.android_qr_code = android_qr_code;
    }

    public String getAndroid_qr_code_small() {
        return android_qr_code_small;
    }

    public void setAndroid_qr_code_small(String android_qr_code_small) {
        this.android_qr_code_small = android_qr_code_small;
    }

    public String getIos_qr_code() {
        return ios_qr_code;
    }

    public void setIos_qr_code(String ios_qr_code) {
        this.ios_qr_code = ios_qr_code;
    }

    public String getIos_qr_code_small() {
        return ios_qr_code_small;
    }

    public void setIos_qr_code_small(String ios_qr_code_small) {
        this.ios_qr_code_small = ios_qr_code_small;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public String getJournal_desc() {
        return journal_desc;
    }

    public void setJournal_desc(String journal_desc) {
        this.journal_desc = journal_desc;
    }

    public Integer getJournal_status() {
        return journal_status;
    }

    public void setJournal_status(Integer journal_status) {
        this.journal_status = journal_status;
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
}
