package com.cjsz.tech.system.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 网站导航配置
 */
@Entity
@Table(name = "web_config")
public class WebConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long config_id;//网站导航Id

    private Long pro_org_extend_id;//项目机构扩展id

    private Long org_id;//机构Id

    private String name;//导航名称

    private String config_cover;//导航图片

    private String config_cover_small;//导航压缩图片

    private String config_remark;//导航简介

    private String content_url;//导航地址

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;//创建时间



    public Long getPro_org_extend_id() {
        return pro_org_extend_id;
    }

    public void setPro_org_extend_id(Long pro_org_extend_id) {
        this.pro_org_extend_id = pro_org_extend_id;
    }

    public Long getConfig_id() {
        return config_id;
    }

    public void setConfig_id(Long config_id) {
        this.config_id = config_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfig_cover() {
        return config_cover;
    }

    public void setConfig_cover(String config_cover) {
        this.config_cover = config_cover;
    }

    public String getConfig_cover_small() {
        return config_cover_small;
    }

    public void setConfig_cover_small(String config_cover_small) {
        this.config_cover_small = config_cover_small;
    }

    public String getConfig_remark() {
        return config_remark;
    }

    public void setConfig_remark(String config_remark) {
        this.config_remark = config_remark;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

}
