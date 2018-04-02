package com.cjsz.tech.cms.beans;

import com.cjsz.tech.beans.PageConditionBean;


/**
 *  查找资讯分类————条件
 * Created by Administrator on 2016/10/25.
 */
public class FindArticleBean extends PageConditionBean {

	private Long org_id;//机构ID

	private Long article_cat_id;//分类ID

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Long getArticle_cat_id() {
		return article_cat_id;
	}

	public void setArticle_cat_id(Long article_cat_id) {
		this.article_cat_id = article_cat_id;
	}
}
