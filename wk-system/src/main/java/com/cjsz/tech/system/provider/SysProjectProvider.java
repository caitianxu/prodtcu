package com.cjsz.tech.system.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by pc on 2017/3/10.
 */
public class SysProjectProvider {
    public String selectAll(Map<String, Object> param) {
        String keyword = (String) param.get("keyword");

        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from sys_project where 1=1");
        if (StringUtil.isNotEmpty(keyword)) {
            String keywordSql = " and project_name like CONCAT('%','" + keyword + "','%')";
            sqlBuffer.append(keywordSql);
        }
        return sqlBuffer.toString();
    }
}
