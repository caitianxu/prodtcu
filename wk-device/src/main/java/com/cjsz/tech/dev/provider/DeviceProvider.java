package com.cjsz.tech.dev.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

public class DeviceProvider {

	public String selectAll(Map<String, Object> param) {
		String keyword = (String) param.get("keyword");
		Long org_id = (Long) param.get("org_id");
		Integer enabled = (Integer) param.get("enabled");
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select d.device_id,d.device_code,d.sync_time,d.create_time,d.enabled,d.province,d.off_line," +
				"d.province_id,d.city,d.city_id,d.area,d.area_id ,d.street,d.location,d.version , " +
				"d.memory, o.org_name, u.user_name, CONCAT(d.province , d.city , d.area) as address, "
						+ " conf.conf_name, d.is_sync from device d "
						+ " LEFT JOIN sys_organization o on o.org_id = d.org_id and o.is_delete = 2"
						+ " LEFT JOIN sys_user u on u.user_id = d.user_id and u.is_delete = 2"
						+ " LEFT JOIN device_conf_rel dc on dc.device_id = d.device_id"
						+ " LEFT JOIN device_setting conf on conf.conf_id = dc.conf_id"
						+ " where 1=1");
		if (StringUtil.isNotEmpty(keyword)) {
			String keywordSql = " and (d.device_code like CONCAT('%','" + keyword + "','%')"
								+ "	or o.org_name like CONCAT('%','" + keyword + "','%')"
								+ " or u.user_name like CONCAT('%','" + keyword + "','%')"
								+ " or d.province like CONCAT('%','" + keyword + "','%') "
								+ " or d.city like CONCAT('%','" + keyword + "','%') "
								+ " or d.area like CONCAT('%','" + keyword + "','%') "
								+ " or d.street like CONCAT('%','" + keyword + "','%'))";
			sqlBuffer.append(keywordSql);
		}
		if(org_id != 1){
			String orgSql = " and d.org_id = " + org_id;
			sqlBuffer.append(orgSql);
		}
		if(enabled != null){
			String enabledSql = " and d.enabled = " + enabled;
			sqlBuffer.append(enabledSql);
		}
		return sqlBuffer.toString();
	}

	public String getDevVersionList(Map<String, Object> param) {
		String keyword = (String) param.get("keyword");
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select d.*, o.org_name, v.version_id,"
						+ " v.version_code as version from device_version_rel dv "
						+ " LEFT JOIN device d on dv.device_id = d.device_id "
						+ " LEFT JOIN sys_organization o on o.org_id = d.org_id and o.is_delete = 2 "
						+ " LEFT JOIN app_version v on v.version_id = dv.version_id "
						+ " where 1=1");
		if (StringUtil.isNotEmpty(keyword)) {
			String keywordSql = " and o.org_name like CONCAT('%','" + keyword + "','%')";
			sqlBuffer.append(keywordSql);
		}
		return sqlBuffer.toString();
	}
}
