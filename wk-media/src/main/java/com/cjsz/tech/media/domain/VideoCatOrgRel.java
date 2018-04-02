package com.cjsz.tech.media.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 视频分类机构关系
 * Created by LuoLi on 2017/4/21 0021.
 */
@Entity
@Table(name = "video_cat_org_rel")
public class VideoCatOrgRel implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rel_id;//视频分类机构关系

    private Long org_id;//机构Id

    private Long video_cat_id;//视频分类ID

    private Long order_weight;//排序

    private Integer is_show;//是否显示（1：是；2：否）

    private Long source_id;//来源Id

    private Integer source_type;//是否自建(1:自建;2:分配)

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;   //最后修改日期

    private Integer is_delete;//是否删除（1：是；2：否）

    public VideoCatOrgRel() {
    }

    public VideoCatOrgRel(Long org_id, Long video_cat_id, Long order_weight, Integer is_show, Long source_id, Integer source_type, Date create_time, Date update_time, Integer is_delete) {
        this.org_id = org_id;
        this.video_cat_id = video_cat_id;
        this.order_weight = order_weight;
        this.is_show = is_show;
        this.source_id = source_id;
        this.source_type = source_type;
        this.create_time = create_time;
        this.update_time = update_time;
        this.is_delete = is_delete;
    }

    public Long getRel_id() {
        return rel_id;
    }

    public void setRel_id(Long rel_id) {
        this.rel_id = rel_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getVideo_cat_id() {
        return video_cat_id;
    }

    public void setVideo_cat_id(Long video_cat_id) {
        this.video_cat_id = video_cat_id;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Integer getIs_show() {
        return is_show;
    }

    public void setIs_show(Integer is_show) {
        this.is_show = is_show;
    }

    public Long getSource_id() {
        return source_id;
    }

    public void setSource_id(Long source_id) {
        this.source_id = source_id;
    }

    public Integer getSource_type() {
        return source_type;
    }

    public void setSource_type(Integer source_type) {
        this.source_type = source_type;
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

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }
}
