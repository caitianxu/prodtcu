package com.cjsz.tech.system.conditions;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Bruce on 2016/11/9.
 */
public class UserCondition extends PageConditionBean {
	
	private Long org_id;
	private Long role_id;
	private Long dept_id;
	
	public Long getOrg_id() {
		return org_id;
	}
	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}
	public Long getRole_id() {
		return role_id;
	}
	public void setRole_id(Long role_id) {
		this.role_id = role_id;
	}
	public Long getDept_id() {
		return dept_id;
	}
	public void setDept_id(Long dept_id) {
		this.dept_id = dept_id;
	}
}
