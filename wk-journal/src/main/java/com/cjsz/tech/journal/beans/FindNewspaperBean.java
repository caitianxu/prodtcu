package com.cjsz.tech.journal.beans;

import com.cjsz.tech.beans.PageConditionBean;


/**
 *  查找报纸分类——条件
 * Created by Administrator on 2016/10/25.
 */
public class FindNewspaperBean extends PageConditionBean {

	private Long org_id;//机构ID

	private Long newspaper_cat_id;//分类ID

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Long getNewspaper_cat_id() {
		return newspaper_cat_id;
	}

	public void setNewspaper_cat_id(Long newspaper_cat_id) {
		this.newspaper_cat_id = newspaper_cat_id;
	}
}
