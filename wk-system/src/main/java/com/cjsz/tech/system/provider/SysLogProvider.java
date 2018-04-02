package com.cjsz.tech.system.provider;

import com.cjsz.tech.system.conditions.SysActionLogCondition;
import com.cjsz.tech.system.conditions.SysLogCondition;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
/**
 * 动态拼接系统日志sql
 * @author Bruce
 * Created by Bruce on 2016/11/9.
 */
public class SysLogProvider {
    public String queryByParams(Map<String, Object> param) {
        StringBuffer querySqlBuffer = new StringBuffer();
        querySqlBuffer.append("select * from sys_log ");
        String whereSql = getWhereSql(param);
        querySqlBuffer.append(whereSql);
        return querySqlBuffer.toString();
    }

    public String deleteByParams(Map<String, Object> param) {
        StringBuffer deleteSqlBuffer = new StringBuffer();
        deleteSqlBuffer.append("delete from sys_log ");
        String whereSql = getWhereSql(param);
        deleteSqlBuffer.append(whereSql);
        return deleteSqlBuffer.toString();
    }

    private String getWhereSql(Map<String, Object> param) {
        SysLogCondition condition = (SysLogCondition) param.get("condition");
        StringBuffer whereSqlBuffer = new StringBuffer();
        whereSqlBuffer.append(" where 1 = 1 ");
        Long org_id = condition.getOrg_id();
        if(null != org_id) { // 1: 查询
            whereSqlBuffer.append(" and org_id = ").append(org_id);
        }
        Long sys_log_code = condition.getSys_log_code();
        if(sys_log_code != null) {
            whereSqlBuffer.append(" and sys_log_code = ").append(sys_log_code);
        }
        String start_time = condition.getBegin_time();
        String end_time = condition.getEnd_time();
        if (!StringUtils.isBlank(start_time) && !StringUtils.isBlank(end_time)) {
            whereSqlBuffer.append(" and date(create_time) >= '").append(start_time).append("' and date(create_time) <='").append(end_time).append("'");
        }
        String sys_log_content = condition.getSys_log_content();
        if (!StringUtils.isBlank(sys_log_content)) {
            String likeSql = " and sys_log_content like '%" + sys_log_content + "%'";
            whereSqlBuffer.append(likeSql);
        }
        return whereSqlBuffer.toString();
    }
}
