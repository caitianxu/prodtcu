package com.cjsz.tech.meb.beans;


import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/25.
 */
public class MemberIdsBean implements Serializable {
	
	private Long[] ids;
	private Integer enabled; //1: 启用  2: 停用

	public Long[] getIds() {
		return ids;
	}

	public void setIds(Long[] ids) {
		this.ids = ids;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}
}
