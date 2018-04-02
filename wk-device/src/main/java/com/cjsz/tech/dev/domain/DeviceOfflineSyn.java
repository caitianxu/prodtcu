package com.cjsz.tech.dev.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备离线同步记录信息
 * Created by shiaihua on 16/12/21.
 */
@Entity
@Table(name = "device_offline_syn")
public class DeviceOfflineSyn  implements Serializable {

    /**
     * 编码
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 组织编码
     */
    private Long org_id;
    /**
     * 业务模块
     */
    private Integer mod_type;
    /**
     * 设备编码
     */
    private Long device_id;
    /**
     * 设备编号
     */
    private String device_code;
    /**
     * 同步时间戳
     */
    private Long time_stamp;
    /**
     * 文件路径
     */
    private String file_url;
    /**
     * 同步状态
     */
    private Integer syn_status;

    /**
     * 同步开始时间
     */
    private Date time_begin;
    /**
     * 同步结束时间
     */
    private Date time_end;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Integer getMod_type() {
        return mod_type;
    }

    public void setMod_type(Integer mod_type) {
        this.mod_type = mod_type;
    }

    public Long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Long device_id) {
        this.device_id = device_id;
    }

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public Long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public Integer getSyn_status() {
        return syn_status;
    }

    public void setSyn_status(Integer syn_status) {
        this.syn_status = syn_status;
    }

    public Date getTime_begin() {
        return time_begin;
    }

    public void setTime_begin(Date time_begin) {
        this.time_begin = time_begin;
    }

    public Date getTime_end() {
        return time_end;
    }

    public void setTime_end(Date time_end) {
        this.time_end = time_end;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }
}
