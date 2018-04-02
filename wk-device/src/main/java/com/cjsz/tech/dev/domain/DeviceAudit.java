package com.cjsz.tech.dev.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备审核
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "device_audit")
public class DeviceAudit implements Serializable {
	
    @Id
    private Long    device_audit_id;//设备审核id
    private String  device_code;	//设备编码
    private Long    org_id;			//机构id
    private Long    user_id;		//用户id
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date    apply_time;		//申请时间
    private Integer audit_status;	//审核状态，1：驳回， 2 ：同意，3：待审核
    @Transient
    private String  org_name;       //机构名称
    @Transient
    private String  user_name;      //用户名称
    
	public Long getDevice_audit_id() {
		return device_audit_id;
	}
	public void setDevice_audit_id(Long device_audit_id) {
		this.device_audit_id = device_audit_id;
	}
	public String getDevice_code() {
		return device_code;
	}
	public void setDevice_code(String device_code) {
		this.device_code = device_code;
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
	public Date getApply_time() {
		return apply_time;
	}
	public void setApply_time(Date apply_time) {
		this.apply_time = apply_time;
	}
	public Integer getAudit_status() {
		return audit_status;
	}
	public void setAudit_status(Integer audit_status) {
		this.audit_status = audit_status;
	}
	public String getOrg_name() {
		return org_name;
	}
	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
}
