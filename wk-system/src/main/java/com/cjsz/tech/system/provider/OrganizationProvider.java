package com.cjsz.tech.system.provider;

import java.util.Map;

import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @since 2016-11-10
 *
 */
public class OrganizationProvider {

	public String getOrgExtend(Map<String, Object> param) {
		String keyword = (String) param.get("keyword");
		String project_code = (String) param.get("project_code");
		Long org_id = (Long) param.get("org_id");
		String myself = (String) param.get("myself");	
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select o.org_name,o.pwd_lock, pro.project_name, o.enabled, o.create_time, e.*, CONCAT(e.province , e.city , e.area) as address"
				+ " from sys_organization o "
				+ " LEFT JOIN sys_org_extend e ON e.org_id = o.org_id "
				+ " LEFT JOIN sys_project pro ON pro.project_code = e.project_code "
				+ " where o.is_delete = 2 and e.is_delete = 2 "
				+ " and (o.pid is null or o.pid = 0) ");
		if (StringUtil.isNotEmpty(keyword)) {
			String keywordSql = " and (o.org_name like CONCAT('%','" + keyword + "','%') "
					+ " or e.province like CONCAT('%','" + keyword + "','%') "
					+ " or e.city like CONCAT('%','" + keyword + "','%') "
					+ " or e.area like CONCAT('%','" + keyword + "','%') "
					+ " or e.street like CONCAT('%','" + keyword + "','%')"
					+ " or pro.project_name like CONCAT('%','" + keyword + "','%'))";
			sqlBuffer.append(keywordSql);
		}
		if(org_id != null){
			String orgSql = " and o.org_id = " + org_id;
			sqlBuffer.append(orgSql);
		}
		if(StringUtils.isNotEmpty(project_code)){
			String orgSql = " and pro.project_code = '" + project_code + "'";
			sqlBuffer.append(orgSql);
		}
		if(StringUtil.isNotEmpty(myself)){
			String selfSql = " and o.org_id <> 1";
			sqlBuffer.append(selfSql);
		}else{
			String selfSql = " and o.enabled = 1";
			sqlBuffer.append(selfSql);
		}
		return sqlBuffer.toString();
	}
}
