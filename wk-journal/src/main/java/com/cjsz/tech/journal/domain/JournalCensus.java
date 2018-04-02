package com.cjsz.tech.journal.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 期刊扫码统计
 * Created by Administrator on 2017/1/20 0020.
 */
public class JournalCensus {

    private Long org_id;        //机构Id

    private Long device_id;        //设备Id

    private Long journal_id;        //期刊Id

    private Long android_scan_count;        //安卓扫码数量

    private Long ios_scan_count;        //IOS扫描数量

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;        //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;        //更新时间

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Long device_id) {
        this.device_id = device_id;
    }

    public Long getJournal_id() {
        return journal_id;
    }

    public void setJournal_id(Long journal_id) {
        this.journal_id = journal_id;
    }

    public Long getAndroid_scan_count() {
        return android_scan_count;
    }

    public void setAndroid_scan_count(Long android_scan_count) {
        this.android_scan_count = android_scan_count;
    }

    public Long getIos_scan_count() {
        return ios_scan_count;
    }

    public void setIos_scan_count(Long ios_scan_count) {
        this.ios_scan_count = ios_scan_count;
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
