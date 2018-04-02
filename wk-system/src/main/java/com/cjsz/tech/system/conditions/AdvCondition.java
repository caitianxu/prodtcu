package com.cjsz.tech.system.conditions;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Bruce on 2016/11/9.
 */
public class AdvCondition extends PageConditionBean{
	
	private Long adv_cat_id;

	private Long org_id;

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Long getAdv_cat_id() {
		return adv_cat_id;
	}

	public void setAdv_cat_id(Long adv_cat_id) {
		this.adv_cat_id = adv_cat_id;
	}
}
