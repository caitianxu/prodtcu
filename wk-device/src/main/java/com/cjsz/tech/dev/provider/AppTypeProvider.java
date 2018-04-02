package com.cjsz.tech.dev.provider;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by Administrator on 2017/9/21 0021.
 */
public class AppTypeProvider {

    public String getOrgList(Map<String,Object> param){
        Long app_type_id = (Long)param.get("app_type_id");
        String searchText = (String)param.get("searchText");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select so.org_id,so.org_name,soe.short_name ,ato.create_time,ato.app_type_id from sys_org_extend soe left join" +
                " sys_organization so on so.org_id = soe.org_id left join (select * from app_type_org_rel where app_type_id = "+app_type_id+" ) ato " +
                " on so.org_id = ato.org_id where soe.is_delete =2 and so.is_delete = 2 ");
        if (StringUtils.isNotEmpty(searchText)){
            sqlBuffer.append(" and so.org_name like '%"+searchText+"%'");
        }
        sqlBuffer.append(" order by ato.create_time desc ,so.org_id asc");
        return sqlBuffer.toString();
    }
}
