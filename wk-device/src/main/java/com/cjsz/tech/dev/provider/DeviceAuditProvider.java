package com.cjsz.tech.dev.provider;

import java.util.Map;

import com.github.pagehelper.StringUtil;

public class DeviceAuditProvider {
	
	public String selectAll(Map<String, Object> param) {
		String keyword = (String) param.get("keyword");	
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select d.*, o.org_name, u.user_name from device_audit d "
						+ " LEFT JOIN sys_organization o on o.org_id = d.org_id and o.is_delete = 2 "
						+ " LEFT JOIN sys_user u on u.user_id = d.user_id and u.is_delete = 2"
						+ " where 1=1 ");
		if (StringUtil.isNotEmpty(keyword)) {
			String keywordSql = " and d.device_code like CONCAT('%','" + keyword + "','%')"
								+ "	or o.org_name like CONCAT('%','" + keyword + "','%')"
								+ " or u.user_name like CONCAT('%','" + keyword + "','%')";
			sqlBuffer.append(keywordSql);
		}
		return sqlBuffer.toString();
	}
}
