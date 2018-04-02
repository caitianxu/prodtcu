package com.cjsz.tech.dev.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Li Yi on 2016/12/23.
 */
public class ConfContentProvider {

    public String pageQuery(Map<String, Object> param) {
        String searchText = (String)param.get("searchText");
        Long conf_id = (Long)param.get("conf_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from conf_content where 1=1");
        if (conf_id != null) {
            sqlBuffer.append(" and conf_id = " + conf_id);
        }
        if (StringUtil.isNotEmpty(searchText)) {
            sqlBuffer.append(" and content_name like '%" + searchText + "%'");
        }
        sqlBuffer.append(" group by conf_id");
        return sqlBuffer.toString();
    }
}
