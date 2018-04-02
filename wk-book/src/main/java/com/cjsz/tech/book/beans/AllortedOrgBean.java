package com.cjsz.tech.book.beans;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 被分配的机构
 * Created by Administrator on 2016/12/23 0023.
 */
public class AllortedOrgBean {

    private String  org_code;   //机构编码

    private String  org_name;   //机构名

    private String  short_name;   //机构名简称

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //分配时间

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
