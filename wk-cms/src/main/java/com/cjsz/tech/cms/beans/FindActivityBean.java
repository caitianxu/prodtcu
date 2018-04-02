package com.cjsz.tech.cms.beans;

import com.cjsz.tech.beans.PageConditionBean;


/**
 *  查找活动————条件
 * Created by Administrator on 2016/10/25.
 */
public class FindActivityBean extends PageConditionBean {

	private Long org_id;//机构ID

	private String start_time;//开始时间

	private String end_time;//结束时间

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
}
