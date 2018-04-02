package com.cjsz.tech.system.beans;


import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/25.
 */
public class DelResBean implements Serializable {
	
	private Long[]  res_ids;
	private String  mark;

	public Long[] getRes_ids() {
		return res_ids;
	}

	public void setRes_ids(Long[] res_ids) {
		this.res_ids = res_ids;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
}
