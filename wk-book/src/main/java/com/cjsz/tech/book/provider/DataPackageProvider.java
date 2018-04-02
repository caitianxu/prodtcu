package com.cjsz.tech.book.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public class DataPackageProvider {

    //数据包列表
    public String getPkgList(Map<String, Object> param){
        String searchText = (String) param.get("searchText");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select p.*,u.user_real_name create_user_name from pkg p left join sys_user u on p.create_user_id = u.user_id where 1 = 1 ");
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and p.pkg_name like '%" + searchText + "%' ";
            sqlBuffer.append(searchTextSql);
        }
        return sqlBuffer.toString();
    }
}