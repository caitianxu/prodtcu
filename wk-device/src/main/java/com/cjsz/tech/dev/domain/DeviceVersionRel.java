package com.cjsz.tech.dev.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备版本关联
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "device_version_rel")
public class DeviceVersionRel implements Serializable {

    @Id
    private Long dev_version_id; //关系id
    
    private Long device_id;		//设备id

    private Long org_id;		//机构id

	private Long version_id;	//版本id

	@JSONField(format = "yyyy-MM-dd")
	private Date create_time;//创建时间

	@JSONField(format = "yyyy-MM-dd")
	private Date update_time;//更新时间
    
	public Long getDev_version_id() {
		return dev_version_id;
	}

	public void setDev_version_id(Long dev_version_id) {
		this.dev_version_id = dev_version_id;
	}

	public Long getVersion_id() {
		return version_id;
	}

	public void setVersion_id(Long version_id) {
		this.version_id = version_id;
	}

	public Long getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Long device_id) {
		this.device_id = device_id;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
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
