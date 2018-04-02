package com.cjsz.tech.system.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Administrator on 2017/8/8 0008.
 */
public class HelpCenterProvider {

    public String getList(Map<String, Object> param) {
        String searchText = (String) param.get("searchText");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from help_center  where 1=1 ");
        if (StringUtil.isNotEmpty(searchText)) {
            sqlBuffer.append(" and title like '"+searchText+"'  ");
        }
        return sqlBuffer.toString();
    }
}
