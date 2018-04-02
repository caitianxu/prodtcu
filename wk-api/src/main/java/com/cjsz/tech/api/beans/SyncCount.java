package com.cjsz.tech.api.beans;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 数据同步统计
 * Created by Administrator on 2017/2/4 0004.
 */
@Entity
@Table(name = "sync_count")
public class SyncCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sync_count_id;      //同步统计Id

    private Long org_id;     //机构Id

    private Long user_id;        //用户Id

    private Long device_id;      //设备Id

    private Integer device_type;        //设备类型(1:大屏;2:IOS;3:Android)

    private Integer status;     //同步状态(1:同步中;2:已同步)

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;        //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;        //更新时间

    public Long getSync_count_id() {
        return sync_count_id;
    }

    public void setSync_count_id(Long sync_count_id) {
        this.sync_count_id = sync_count_id;
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

    public Long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Long device_id) {
        this.device_id = device_id;
    }

    public Integer getDevice_type() {
        return device_type;
    }

    public void setDevice_type(Integer device_type) {
        this.device_type = device_type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
