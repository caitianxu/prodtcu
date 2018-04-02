package com.cjsz.tech.system.provider;

import java.util.Map;

import com.github.pagehelper.StringUtil;

/**
 * 
 * @since 2016-11-10
 *
 */
public class RoleProvider {

	public String selectAll(Map<String, Object> param) {
		String keyword = (String) param.get("keyword");	
		Long org_id = (Long) param.get("org_id");	
		String role_ids = (String) param.get("role_ids");
		Integer role_type = (Integer) param.get("role_type");
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from sys_role where is_delete = 2");
		if (StringUtil.isNotEmpty(keyword)) {
			String keywordSql = " and role_name like CONCAT('%','" + keyword + "','%')";
			sqlBuffer.append(keywordSql);
		}
		if(org_id != null){
			String orgSql = " and org_id = " + org_id;
			sqlBuffer.append(orgSql);
		}
		if (StringUtil.isNotEmpty(role_ids)) {
			String roleIdsSql = " and role_id in (" + role_ids + ")";
			sqlBuffer.append(roleIdsSql);
		}
		if(role_type != null){
			String typeSql = " and role_type = " + role_type;
			sqlBuffer.append(typeSql);
		}
		return sqlBuffer.toString();
	}
}
