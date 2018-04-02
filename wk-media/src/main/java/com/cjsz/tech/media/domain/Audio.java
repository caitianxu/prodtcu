package com.cjsz.tech.media.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 音频详情
 *
 * Created by Li Yi on 2016/12/7.
 */
@Entity
@Table(name = "audio")
public class Audio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long audio_id;//音频编码

    private Long audio_cat_id;//音频分类编码

    private Long org_id;//机构ID

    private Long user_id;//系统用户ID

    private String audio_title;//音频标题

    private String cover_url;//音频封面路径

    private String cover_url_small;//音频封面路径压缩

    private String audio_url;//音频路径

    private Object audio_remark;//音频描述

    private Long order_weight;//排序

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;//创建时间

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date update_time;//最后修改日期

    private Integer is_delete;//是否删除

    @Transient
    private String user_name;//用户名称

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public Long getAudio_id() {
        return audio_id;
    }

    public void setAudio_id(Long audio_id) {
        this.audio_id = audio_id;
    }

    public Long getAudio_cat_id() {
        return audio_cat_id;
    }

    public void setAudio_cat_id(Long audio_cat_id) {
        this.audio_cat_id = audio_cat_id;
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

    public String getAudio_title() {
        return audio_title;
    }

    public void setAudio_title(String audio_title) {
        this.audio_title = audio_title;
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

    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }

    public Object getAudio_remark() {
        return audio_remark;
    }

    public void setAudio_remark(Object audio_remark) {
        this.audio_remark = audio_remark;
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

    public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
