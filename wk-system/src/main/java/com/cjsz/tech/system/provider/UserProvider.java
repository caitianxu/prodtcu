package com.cjsz.tech.system.provider;

import com.cjsz.tech.system.conditions.UserCondition;
import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * 
 * @since 2016-11-10
 *
 */
public class UserProvider {

	public String selectAll(Map<String, Object> param) {
		UserCondition condition = (UserCondition) param.get("condition");
		StringBuffer sqlBuffer = new StringBuffer();
		String keyword = condition.getSearchText();
		Long org_id = condition.getOrg_id();
		Long dept_id = condition.getDept_id();
		sqlBuffer.append("SELECT u.* , o.org_name, d.org_name as dept_name, "
				+ " GROUP_CONCAT(r.role_name order by r.role_id asc) as  role_name, "
				+ " GROUP_CONCAT(r.role_id order by r.role_id asc) as  role_id "
				+ " FROM sys_user u "
				+ " LEFT JOIN sys_organization o on o.org_id = u.org_id and o.is_delete = 2 "
				+ " LEFT JOIN sys_organization d on d.org_id = u.dept_id and d.is_delete = 2 "
				+ " LEFT JOIN sys_user_role_rel ur on u.user_id = ur.user_id and ur.is_delete = 2 "
				+ " LEFT JOIN sys_role r on r.role_id = ur.role_id and r.is_delete = 2 "
				+ " where u.is_delete = 2 ");
		if (StringUtil.isNotEmpty(keyword)) {
			String keywordSql = " and (u.user_real_name like CONCAT('%','" + keyword + "','%')"
					+ " or u.user_name like CONCAT('%','" + keyword + "','%') "
					+ " or u.sex like CONCAT('%','" + keyword + "','%') "
					+ " or u.phone like CONCAT('%','" + keyword + "','%') "
					+ " or r.role_name like CONCAT('%','" + keyword + "','%') "
					+ " or o.org_name like CONCAT('%','" + keyword + "','%'))";
			sqlBuffer.append(keywordSql);
		}
		if(org_id != null){
			String orgSql = " and u.org_id = " + org_id + " " ;
			sqlBuffer.append(orgSql);
		}
		if(dept_id != null && !org_id.equals(dept_id)){
			String orgSql = " and u.dept_id = " + dept_id + " " ;
			sqlBuffer.append(orgSql);
		}
		String endSql = " group by u.user_id";
		sqlBuffer.append(endSql);
		return sqlBuffer.toString();
	}


}
