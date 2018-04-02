package com.cjsz.tech.media.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 视频详情
 * Created by Li Yi on 2016/12/5.
 */
@Entity
@Table(name = "video")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long video_id;//视频编码

    private Long video_cat_id;//视频分类编码

    private Long org_id;//机构ID

    private Long user_id;//系统用户ID

    private String video_title;//视频标题

    private String cover_url;//视频封面路径

    private String cover_url_small;//视频压缩封面路径

    private String video_url;//视频路径

    private Object video_remark;//视频描述

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

    public Long getVideo_id() {
        return video_id;
    }

    public void setVideo_id(Long video_id) {
        this.video_id = video_id;
    }

    public Long getVideo_cat_id() {
        return video_cat_id;
    }

    public void setVideo_cat_id(Long video_cat_id) {
        this.video_cat_id = video_cat_id;
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

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
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

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public Object getVideo_remark() {
        return video_remark;
    }

    public void setVideo_remark(Object video_remark) {
        this.video_remark = video_remark;
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
