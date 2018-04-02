package com.cjsz.tech.journal.beans;

import com.cjsz.tech.beans.PageConditionBean;


/**
 *  查找期刊分类——条件
 * Created by Administrator on 2016/10/25.
 */
public class FindJournalBean extends PageConditionBean {

	private Long org_id;//机构ID

	private Long journal_cat_id;//分类ID

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Long getJournal_cat_id() {
		return journal_cat_id;
	}

	public void setJournal_cat_id(Long journal_cat_id) {
		this.journal_cat_id = journal_cat_id;
	}
}
