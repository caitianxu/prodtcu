package com.cjsz.tech.system.service;

import com.cjsz.tech.system.conditions.SysActionLogCondition;
import com.cjsz.tech.system.domain.SysActionLog;

import java.util.List;

/**
 * @Title: SysActionLogService
 * @Description: 操作日志
 * @author Bruce
 * @date 2016-11-09 13:26:26
 * @version V1.0   
 *
 */
public interface SysActionLogService {

    //新增操作日志
    public Integer saveLog(SysActionLog sysActionLog);

    /**
     * 批量增加日志
     * @param sysActionLogs
     */
    public void saveLogs(List<SysActionLog> sysActionLogs) ;

    //根据id查询操作日志
    public SysActionLog selectById(Long action_log_id);

    //查询所有操作日志
    public List<SysActionLog> findAll();

    //分页查询操作日志
    public Object pageQuery(SysActionLogCondition sysActionLogCondition);

    public void backup(String logRootDir, SysActionLogCondition sysActionLogCondition);
}