package com.cjsz.tech.dev.beans;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * Created by Administrator on 2017/9/19 0019.
 */
public class OrgListBean {

    private Long org_id;
    private String short_name;
    private String org_name;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String shor_name) {
        this.short_name = shor_name;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
