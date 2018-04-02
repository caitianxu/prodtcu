package com.cjsz.tech.system.provider;

import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class AppNavProvider {

    public String getAppNavList(Map<String, Object> param){
        String searchText = (String) param.get("searchText");
        Long org_id = (Long) param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from app_nav where org_id= " + org_id);
        if(!StringUtils.isEmpty(searchText)){
            sqlBuffer.append(" and nav_name like concat('%','"+ searchText +"','%')");
        }
        sqlBuffer.append(" order by order_weight desc");
        return sqlBuffer.toString();
    }

}
