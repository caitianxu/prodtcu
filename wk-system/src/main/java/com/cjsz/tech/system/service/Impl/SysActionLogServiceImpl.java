package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.conditions.SysActionLogCondition;
import com.cjsz.tech.system.conditions.SysLogCondition;
import com.cjsz.tech.system.domain.SysActionLog;
import com.cjsz.tech.system.domain.SysLog;
import com.cjsz.tech.system.mapper.SysActionLogMapper;
import com.cjsz.tech.system.service.SysActionLogService;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.system.wrappers.SysActionLogWrapper;
import com.cjsz.tech.utils.DateUtils;
import com.github.pagehelper.PageHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Date;
import java.util.List;




/**   
 * @Title: SysActionLogService
 * @Description: 系统操作日志
 * @author Bruce
 * @date 2016-11-09 13:26:26
 * @version V1.0   
 *
 */
@Service
public class SysActionLogServiceImpl implements SysActionLogService{

    @Autowired
    private SysActionLogMapper sysActionLogMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 新增操作日志
     * @param sysActionLog
     */
    @Override
    public Integer saveLog(SysActionLog sysActionLog) {
        sysActionLog.setAction_time(new Date());
        return sysActionLogMapper.insert(sysActionLog);
    }

    /**
     * 批量增加日志
     * @param sysActionLogs
     */
    public void saveLogs(List<SysActionLog> sysActionLogs) {
        sysActionLogMapper.insertList(sysActionLogs);
    }

    /**
     * 通过ID查询日志
     * @param action_log_id
     * @return
     */
    @Override
    public SysActionLog selectById(Long action_log_id) {
        return sysActionLogMapper.selectByPrimaryKey(action_log_id);
    }

    /**
     * 查询所有操作日志
     * @return
     */
    @Override
    public List<SysActionLog> findAll() {
        return sysActionLogMapper.selectAll();
    }

    /**
     * 分页查询操作日志
     * @param sysActionLogCondition
     * @return
     */
    @Override
    public Object pageQuery(SysActionLogCondition sysActionLogCondition) {
        Integer pageNum = sysActionLogCondition.getPageNum() == null ? 1 : sysActionLogCondition.getPageNum();
        Integer pageSize = sysActionLogCondition.getPageSize() == null ? 10 : sysActionLogCondition.getPageSize();
        Sort sort = new Sort(Sort.Direction.DESC, "action_time");
        PageHelper.startPage(pageNum, pageSize);
        String orderBy = ConditionOrderUtil.prepareOrder(sort);
        if (orderBy != null) {
  			PageHelper.orderBy(orderBy);
  		}
        List<SysActionLog> logs = sysActionLogMapper.queryByParams(sysActionLogCondition);
        PageList pageList = new PageList(logs, new SysActionLogWrapper());
        return pageList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.BACKUP, action_log_module_name = "操作日志")
    public void backup(String logRootDir, SysActionLogCondition sysActionLogCondition) {
        Integer pageNum = sysActionLogCondition.getPageNum();
        Integer pageSize = sysActionLogCondition.getPageSize();
        if(!(pageNum == null || pageSize == null)) {
            PageHelper.startPage(pageNum, pageSize);
        }
        Sort sort = new Sort(Sort.Direction.ASC, "action_log_id");
        String orderBy = ConditionOrderUtil.prepareOrder(sort);
        if(StringUtils.isBlank(orderBy)) {
            PageHelper.orderBy(orderBy);
        }
        List<SysActionLog> logs = sysActionLogMapper.queryByParams(sysActionLogCondition);

        String idStr = "";
        if(logs != null && logs.size() > 0) {
            idStr = write(logRootDir, logs);
        }

        if(sysActionLogCondition.getIs_delete() == 1 && idStr.length() > 0) { // 删除备份的记录
            String sql = "delete from sys_action_log where action_log_id in (" + idStr + ")";
            jdbcTemplate.update(sql);
        }
    }

    private File createFile(String logRootDir) {
        String fileName = logRootDir + File.separator + "actionlog" + DateUtils.getCurrentDate() + ".log";
        File logFile = new File(fileName);
        if(!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logFile;
    }

    /**
     * 写日志到文件
     * @param logRootDir
     * @param logs
     * @return
     */
    private String write(String logRootDir, List<SysActionLog> logs) {
        boolean writeFlag = false;
        StringBuilder lineSb = new StringBuilder();

        File logFile = createFile(logRootDir);
        BufferedWriter bw = null;

        StringBuilder idBuffer = new StringBuilder();
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true)));
            for(int i = 0; i < logs.size(); i ++) {
                SysActionLog log = logs.get(i);
                String action_time = DateUtils.getDateTime(log.getAction_time());
                lineSb.append(log.getAction_log_id()).append("\t");
                lineSb.append(log.getAction_type()).append("\t");
                lineSb.append(log.getAction_user_name()).append("\t");
                lineSb.append(log.getAction_log_module_name()).append("\t");
                lineSb.append(action_time).append("\n");
                bw.write(lineSb.toString());
                if(i < logs.size() -1) {
                    idBuffer.append(log.getAction_log_id()).append(",");
                } else {
                    idBuffer.append(log.getAction_log_id());
                }
            }
            writeFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(bw != null){
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return idBuffer.toString();
    }
}