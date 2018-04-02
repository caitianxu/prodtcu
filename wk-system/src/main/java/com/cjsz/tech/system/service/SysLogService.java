package com.cjsz.tech.system.service;




import com.cjsz.tech.system.conditions.SysLogCondition;
import com.cjsz.tech.system.domain.SysLog;

import java.util.List;

/**   
 * @Title: SysLogService
 * @Description: 系统日志
 * @author Bruce
 * @date 2016-11-09 13:26:25
 * @version V1.0   
 *
 */
public interface SysLogService {

    //新增日志
    public void saveLog(SysLog sysLog);

    public void saveLogs(List<SysLog> sysLogs) ;

    //根据id查询日志
    public SysLog selectById(Long sys_log_id);

    //查询所有日志
    public List<SysLog> findAll();

    //分页查询日志
    /**
     *
     * @param sysLogCondition
     * @return
     */
    public Object pageQuery(SysLogCondition sysLogCondition);

    public void backup(String logRootDir, SysLogCondition sysLogCondition);
}