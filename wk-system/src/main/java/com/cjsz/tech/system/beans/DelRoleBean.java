package com.cjsz.tech.system.beans;


import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/25.
 */
public class DelRoleBean implements Serializable {
	
	private String[]  ids;
	private String  mark;

	public String[] getIds() {
		return ids;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
}
