package com.cjsz.tech.dev.provider;

import java.util.Map;

import com.cjsz.tech.dev.beans.MessageBean;
import com.github.pagehelper.StringUtil;

public class DeviceFeedbProvider {
	
	public String selectAll(Map<String, Object> param) {
		String keyword = (String) param.get("keyword");	
		Long org_id = (Long) param.get("org_id");
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select f.*, o.org_name from device_feedback f "
						 + " LEFT JOIN sys_organization o on o.org_id = f.org_id and o.is_delete = 2"
						 + " LEFT JOIN sys_user u on u.user_id = f.user_id and u.is_delete = 2"
						 + " where 1=1 ");
		if (StringUtil.isNotEmpty(keyword)) {
			String keywordSql = " and ( o.org_name like CONCAT('%','" + keyword + "','%')"
								+ " or u.user_real_name like CONCAT('%','" + keyword + "','%'))";
			sqlBuffer.append(keywordSql);
		}
		if(org_id != 1){
			String orgSql = " and f.org_id = " + org_id;
			sqlBuffer.append(orgSql);
		}
		return sqlBuffer.toString();
	}

	public String getListByOrgAndDept(Map<String,Object> param){
        String keyword = (String) param.get("searchText");
        Long org_id = (Long) param.get("org_id");
        Long dept_id = (Long)param.get("dept_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select f.*,o.org_name from device_feedback f " +
                         " left join sys_organization o on o.org_id = f.dept_id and o.is_delete =2" +
                         " left join (select * from tb_member group by member_id) m on m.member_id = f.user_id where 1=1");
        if (StringUtil.isNotEmpty(keyword)) {
            String keywordSql = " and ( o.org_name like CONCAT('%','" + keyword + "','%')"
                    + " or m.nick_name like CONCAT('%','" + keyword + "','%'))";
            sqlBuffer.append(keywordSql);
        }
        if(org_id != 1){
            String orgSql = " and f.dept_id = " + dept_id;
            sqlBuffer.append(orgSql);
        }
        return sqlBuffer.toString();
    }

    public String getListByOrg(Map<String,Object> param){
        MessageBean bean = (MessageBean) param.get("bean");
        String keyword = bean.getSearchText();
        Long dept_id = bean.getDept_id();
        Long org_id = (Long) param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select f.*,o.org_name,so.org_name as dept_name from device_feedback f" +
                " left join sys_organization o on o.org_id = f.org_id and o.is_delete =2" +
                " left join (select * from tb_member group by member_id) m on m.member_id = f.user_id" +
                " left join sys_organization so on so.org_id = m.dept_id  and so.is_delete = 2 where 1=1");
        if (StringUtil.isNotEmpty(keyword)) {
            String keywordSql = " and ( o.org_name like CONCAT('%','" + keyword + "','%')"
                    + " or m.nick_name like CONCAT('%','" + keyword + "','%'))";
            sqlBuffer.append(keywordSql);
        }
        if(org_id != 1){
            String orgSql = " and f.org_id = " + org_id;
            sqlBuffer.append(orgSql);
        }
        if (dept_id != null){
            sqlBuffer.append(" and f.dept_id = " + dept_id);
        }
        return sqlBuffer.toString();
    }
}
