package com.cjsz.tech.journal.beans;

import com.cjsz.tech.beans.PageConditionBean;


/**
 *  查找期刊分类——条件
 * Created by Administrator on 2016/10/25.
 */
public class FindPeriodicalBean extends PageConditionBean {

	private Long org_id;//机构ID

	private Long periodical_cat_id;//分类ID

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Long getPeriodical_cat_id() {
		return periodical_cat_id;
	}

	public void setPeriodical_cat_id(Long periodical_cat_id) {
		this.periodical_cat_id = periodical_cat_id;
	}
}
