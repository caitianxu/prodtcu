package com.cjsz.tech.member.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Administrator on 2017/7/25 0025.
 */
public class UnifyMemberProvider {
    public String selectAll(Map<String, Object> param) {
        String keyword = (String) param.get("keyword");
        Long org_id = (Long) param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select m.*,org.org_name,dp.org_name as dept_name from tb_member m left join sys_organization org on m.org_id = org.org_id" +
                         " left join sys_organization dp on dp.org_id = m.dept_id where 1=1 ");
        if(!org_id.equals(1L)){
            sqlBuffer.append(" and m.org_id = " + org_id );
        }
        if (StringUtil.isNotEmpty(keyword)) {
            String keywordSql = " and (nick_name like CONCAT('%','" + keyword + "','%')"
                    + " or sex like CONCAT('%','" + keyword + "','%') "
                    + " or email like CONCAT('%','" + keyword + "','%') "
                    + " or phone like CONCAT('%','" + keyword + "','%'))";
            sqlBuffer.append(keywordSql);
        }
        sqlBuffer.append(" group by m.member_id ");
        return sqlBuffer.toString();
    }
}
