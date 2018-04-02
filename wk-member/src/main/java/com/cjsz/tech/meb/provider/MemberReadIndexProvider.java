package com.cjsz.tech.meb.provider;

import com.cjsz.tech.system.beans.SearchBean;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by LuoLi on 2017/4/28 0028.
 */
public class MemberReadIndexProvider {

    public String pageQuery(Map<String, Object> map){
        SearchBean bean = (SearchBean)map.get("searchBean");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select i.*, m.nick_name from member_read_index i left join tb_member m on i.member_id = m.member_id where 1 = 1 ");
        if(bean.getId() != null && bean.getId() != 0){
            if(bean.getType().equals("pro")){
                String sql = " and i.member_id in (select member_id from tb_member where org_id in " +
                        " (select org_id from sys_org_extend where project_code = '" + bean.getCode() + "') " +
                        ")";
                sqlBuffer.append(sql);
            }else{
                sqlBuffer.append(" and i.member_id in (select member_id from tb_member where org_id = " + bean.getId() + ") ");
            }
        }
        if(StringUtils.isNotEmpty(bean.getCreate_time())){
            String sql = " and TO_DAYS(i.create_time) = TO_DAYS('" + bean.getCreate_time() + "') ";
            sqlBuffer.append(sql);
        }
        sqlBuffer.append(" group by member_id ");
        return sqlBuffer.toString();
    }

}
