package com.cjsz.tech.dev.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Administrator on 2016/10/25.
 */
public class DevicePageBean extends PageConditionBean{
	
	private Long[] device_ids;
	
	private Long version_id;

	private Long conf_id;
	
	public Long[] getDevice_ids() {
		return device_ids;
	}

	public void setDevice_ids(Long[] device_ids) {
		this.device_ids = device_ids;
	}

	public Long getVersion_id() {
		return version_id;
	}

	public void setVersion_id(Long version_id) {
		this.version_id = version_id;
	}

	public Long getConf_id() {
		return conf_id;
	}

	public void setConf_id(Long conf_id) {
		this.conf_id = conf_id;
	}
}
