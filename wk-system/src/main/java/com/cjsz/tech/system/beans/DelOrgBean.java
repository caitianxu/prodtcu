package com.cjsz.tech.system.beans;


import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/25.
 */
public class DelOrgBean implements Serializable {
	
	private Long  org_id;
	private String  mark;
    
	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
}
