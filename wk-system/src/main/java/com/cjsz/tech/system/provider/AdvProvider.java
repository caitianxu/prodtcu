package com.cjsz.tech.system.provider;

import com.cjsz.tech.system.beans.AdvOrgListBean;
import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * 
 * @since 2016-11-10
 *
 */
public class AdvProvider {

	public String getAdvList(Map<String, Object> param) {
		String searchText = (String) param.get("searchText");
		Long adv_cat_id = (Long) param.get("adv_cat_id");
		Long org_id = (Long) param.get("org_id");
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select a.* ,r.is_delete as org_is_delete,r.is_show,r.order_weight as org_order_weight,o.org_name " +
						 "from adv_org_rel r LEFT JOIN adv a on a.adv_id = r.adv_id  left join sys_organization o on a.org_id = o.org_id " +
						 "where a.is_delete = 2 and r.is_delete= 2 " );
		if(adv_cat_id != null && adv_cat_id != 0){
			String orgSql = " and a.adv_cat_id = " + adv_cat_id;
			sqlBuffer.append(orgSql);
		}
		if (org_id == 1) {
			sqlBuffer.append(" and r.org_id = " + org_id);
		} else {
			sqlBuffer.append(" and a.enabled = 1 and r.org_id = " + org_id);
		}
		if (StringUtil.isNotEmpty(searchText)) {
			String keywordSql = " and a.adv_name like '%" + searchText + "%'";
			sqlBuffer.append(keywordSql);
		}
		return sqlBuffer.toString();
	}



	public String getOrgList(Map<String, Object> param) {
		AdvOrgListBean bean = (AdvOrgListBean)param.get("bean");
		String searchText = bean.getSearchText();
		Long project_id = bean.getProject_id();
		Long adv_id = bean.getAdv_id();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select acor.adv_id,sp.project_id,sp.project_code,sp.project_name,so.org_id,so.org_name," +
				"soe.extend_code org_code,soe.short_name,acor.update_time,acor.is_delete from sys_org_extend soe " +
				"left join (select * from adv_org_rel where adv_id = " + adv_id + ") acor on soe.org_id = acor.org_id " +
				"left join sys_organization so on soe.org_id = so.org_id " +
				"left join sys_project sp on soe.project_code = sp.project_code " +
				"where soe.is_delete = 2 and so.is_delete = 2 and so.enabled = 1 ");
		if(project_id != null){
			sqlBuffer.append( " and sp.project_id = " + project_id);
		}
		if (StringUtil.isNotEmpty(searchText)) {
			sqlBuffer.append(" and so.org_name like '%" + searchText + "%'");
		}
		return sqlBuffer.toString();
	}
}
