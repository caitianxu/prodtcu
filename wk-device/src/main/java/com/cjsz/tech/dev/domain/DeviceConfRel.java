package com.cjsz.tech.dev.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备配置关系
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "device_conf_rel")
public class DeviceConfRel implements Serializable {
	
	@Id
    private Long device_id;	//设备id

    @Id
    private Long org_id;	//机构id

	private Long conf_id;	//配置id

	private Date update_time;	//更新时间

	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	public Long getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Long device_id) {
		this.device_id = device_id;
	}

	public Long getConf_id() {
		return conf_id;
	}

	public void setConf_id(Long conf_id) {
		this.conf_id = conf_id;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}
}
