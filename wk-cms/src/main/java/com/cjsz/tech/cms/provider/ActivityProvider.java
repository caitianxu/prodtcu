package com.cjsz.tech.cms.provider;

import com.cjsz.tech.cms.beans.FindActivityBean;
import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * 查询活动列表
 * Created by Administrator on 2016/11/22 0022.
 */
public class ActivityProvider {

    public String getActivityList(Map<String, Object> param) {
        FindActivityBean bean = (FindActivityBean) param.get("bean");
        Long org_id = bean.getOrg_id();
        String start_time = bean.getStart_time();
        String end_time = bean.getEnd_time();
        String searchText = bean.getSearchText();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT * from activity where 1=1 ");
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and activity_title like '%" + searchText + "%'";
            sqlBuffer.append(searchTextSql);
        }
        if (StringUtil.isNotEmpty(start_time)) {
            String keywordSql = " and UNIX_TIMESTAMP(start_time) >= UNIX_TIMESTAMP('" + start_time + " 00:00:00') ";
            sqlBuffer.append(keywordSql);
        }
        if (StringUtil.isNotEmpty(end_time)) {
            String keywordSql = " and UNIX_TIMESTAMP(end_time) <= UNIX_TIMESTAMP('" + end_time + " 23:59:59') ";
            sqlBuffer.append(keywordSql);
        }
        return sqlBuffer.toString();
    }

}
