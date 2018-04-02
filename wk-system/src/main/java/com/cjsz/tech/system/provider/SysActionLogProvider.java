package com.cjsz.tech.system.provider;

import com.cjsz.tech.system.conditions.SysActionLogCondition;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 动态拼接系统操作日志sql
 * @author Bruce
 * Created by Bruce on 2016/11/9.
 */
public class SysActionLogProvider {
    public String queryByParams(Map<String, Object> param) {
        StringBuffer querySqlBuffer = new StringBuffer();
        querySqlBuffer.append("select sal.* from sys_action_log sal left join sys_user su on sal.action_user_name = su.user_name left join sys_organization org on su.org_id = org.org_id ");
        String whereSql = getWhereSql(param);
        querySqlBuffer.append(whereSql);
        return querySqlBuffer.toString();
    }

    public String deleteByParams(Map<String, Object> param) {
        StringBuffer delSqlBuffer = new StringBuffer();
        delSqlBuffer.append("delete from sys_action_log ");
        String whereSql = getDelWhereSql(param);
        delSqlBuffer.append(whereSql);
        return delSqlBuffer.toString();
    }

    /**
     * 获取where sql
     * 动态适配
     * @param param
     * @return
     */
    private String getWhereSql(Map<String, Object> param) {
        SysActionLogCondition condition = (SysActionLogCondition) param.get("condition");
        StringBuffer whereSqlBuffer = new StringBuffer();
        whereSqlBuffer.append(" where 1 = 1 ");
        Long org_id = condition.getOrg_id();
        if(null != org_id) { // 1: 查询
            whereSqlBuffer.append(" and sal.org_id = ").append(org_id);
        }
        String action_type = condition.getAction_type();
        if(!StringUtils.isBlank(action_type)) {
            whereSqlBuffer.append(" and sal.action_type = '").append(action_type).append("'");
        }
        String action_user_name = condition.getAction_user_name();
        if(!StringUtils.isBlank(action_user_name)) {
            whereSqlBuffer.append(" and sal.action_user_name = '").append(action_user_name).append("'");
        }
        String start_time = condition.getBegin_time();
        String end_time = condition.getEnd_time();
        if (!StringUtils.isBlank(start_time) && !StringUtils.isBlank(end_time)) {
            whereSqlBuffer.append(" and date(sal.action_time) >= '").append(start_time).append("' and date(sal.action_time) <='").append(end_time).append("'");
        }
        String action_log_content = condition.getAction_log_content();
        if (!StringUtils.isBlank(action_log_content)) {
            String likeSql = " and sal.action_log_content like '%" + action_log_content + "%'";
            whereSqlBuffer.append(likeSql);
        }
        String searchText = condition.getSearchText();
        if (StringUtils.isNotEmpty(searchText)){
            whereSqlBuffer.append(" and sal.action_log_module_name like '%"+searchText + "%'");
        }

        return whereSqlBuffer.toString();
    }

    /**
     * 获取where sql
     * 动态适配
     * @param param
     * @return
     */
    private String getDelWhereSql(Map<String, Object> param) {
        SysActionLogCondition condition = (SysActionLogCondition) param.get("condition");
        StringBuffer whereSqlBuffer = new StringBuffer();
        whereSqlBuffer.append(" where 1 = 1 ");
        String action_type = condition.getAction_type();
        if(!StringUtils.isBlank(action_type)) {
            whereSqlBuffer.append(" and action_type = '").append(action_type).append("'");
        }
        String action_user_name = condition.getAction_user_name();
        if(!StringUtils.isBlank(action_user_name)) {
            whereSqlBuffer.append(" and action_user_name = '").append(action_user_name).append("'");
        }
        String start_time = condition.getBegin_time();
        String end_time = condition.getEnd_time();
        if (!StringUtils.isBlank(start_time) && !StringUtils.isBlank(end_time)) {
            whereSqlBuffer.append(" and date(action_time) >= '").append(start_time).append("' and date(action_time) <='").append(end_time).append("'");
        }
        String action_log_content = condition.getAction_log_content();
        if (!StringUtils.isBlank(action_log_content)) {
            String likeSql = " and action_log_content like '%" + action_log_content + "%'";
            whereSqlBuffer.append(likeSql);
        }

        return whereSqlBuffer.toString();
    }
}
