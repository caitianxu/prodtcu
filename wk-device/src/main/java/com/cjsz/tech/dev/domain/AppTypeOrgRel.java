package com.cjsz.tech.dev.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@Entity
@Table(name = "app_type_org_rel")
public class AppTypeOrgRel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rel_id;
    private Long org_id;
    private String org_name;
    private Long app_type_id;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date create_time;

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

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public Long getApp_type_id() {
        return app_type_id;
    }

    public void setApp_type_id(Long app_type_id) {
        this.app_type_id = app_type_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }


}
