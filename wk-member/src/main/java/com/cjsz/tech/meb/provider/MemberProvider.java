package com.cjsz.tech.meb.provider;

import java.util.Map;

import com.github.pagehelper.StringUtil;

/**
 * 
 * @since 2016-11-10
 *
 */
public class MemberProvider {

	public String selectAll(Map<String, Object> param) {
		String keyword = (String) param.get("keyword");
		Long org_id = (Long) param.get("org_id");
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select m.*, mg.grade_icon member_header, mg.grade_title, mg.grade_name from member m " +
				" left join member_grade mg on m.grade_id = mg.grade_id where is_delete = 2 ");
		if(!org_id.equals(1L)){
			sqlBuffer.append(" and org_id = " + org_id );
		}
		if (StringUtil.isNotEmpty(keyword)) {
			String keywordSql = " and (member_real_name like CONCAT('%','" + keyword + "','%')"
					+ " or member_name like CONCAT('%','" + keyword + "','%') "
					+ " or nick_name like CONCAT('%','" + keyword + "','%') "
					+ " or sex like CONCAT('%','" + keyword + "','%') "
					+ " or email like CONCAT('%','" + keyword + "','%') "
					+ " or phone like CONCAT('%','" + keyword + "','%'))";
			sqlBuffer.append(keywordSql);
		}
		return sqlBuffer.toString();
	}
}
