package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.core.page.BaseWrapper;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.conditions.SysActionLogCondition;
import com.cjsz.tech.system.conditions.SysLogCondition;
import com.cjsz.tech.system.domain.SysLog;
import com.cjsz.tech.system.mapper.SysLogMapper;
import com.cjsz.tech.system.service.SysLogService;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.system.wrappers.SysLogWrapper;
import com.cjsz.tech.utils.DateUtils;
import com.github.pagehelper.PageHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * 系统日志Service
 * Created by Bruce on 2016/11/9.
 */
@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;



    /**
     * 新增日志
     * @param sysLog
     */
    @Override
    public void saveLog(SysLog sysLog) {
        sysLog.setCreate_time(new Date());
        sysLogMapper.insert(sysLog);
    }

    /**
     * 批量导入日志
     * @param sysLogs
     */
    @Override
    public void saveLogs(List<SysLog> sysLogs) {

        sysLogMapper.insertList(sysLogs);
    }

    /**
     * 通过ID查询日志
     * @param sys_log_id
     * @return
     */
    @Override
    public SysLog selectById(Long sys_log_id) {
        return sysLogMapper.findById(sys_log_id);
    }

    /**
     * 查询所有日志
     * @return
     */
    @Override
    public List<SysLog> findAll() {
        return sysLogMapper.selectAll();
    }

    /**
     *
     * @param sysLogCondition
     * @return
     */
    @Override
    public Object pageQuery(SysLogCondition sysLogCondition) {
        Integer pageNum = sysLogCondition.getPageNum();
        Integer pageSize = sysLogCondition.getPageSize();
        Sort sort = new Sort(Sort.Direction.DESC, "action_time");
        PageHelper.startPage(pageNum, pageSize);
        String orderBy = ConditionOrderUtil.prepareOrder(sort);
        if(StringUtils.isBlank(orderBy)) {
            PageHelper.orderBy(orderBy);
        }
        List<SysLog> logs = sysLogMapper.queryByParams(sysLogCondition);
        PageList pageList = new PageList(logs, new SysLogWrapper());
        return pageList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.BACKUP, action_log_module_name = "系统日志")
    public void backup(String logRootDir, SysLogCondition sysLogCondition) {
        Integer pageNum = sysLogCondition.getPageNum();
        Integer pageSize = sysLogCondition.getPageSize();
        if(!(pageNum == null || pageSize == null)) {
            PageHelper.startPage(pageNum, pageSize);
        }
        Sort sort = new Sort(Sort.Direction.ASC, "sys_log_id");
        String orderBy = ConditionOrderUtil.prepareOrder(sort);
        if(StringUtils.isBlank(orderBy)) {
            PageHelper.orderBy(orderBy);
        }
        List<SysLog> logs = sysLogMapper.queryByParams(sysLogCondition);

        String idStr = "";
        if(logs != null && logs.size() > 0) {
            idStr = write(logRootDir, logs);
        }

        if(sysLogCondition.getIs_delete() == 1 && idStr.length() > 0) { // 删除备份的记录
            String sql = "delete from sys_log where sys_log_id in (" + idStr + ")";
            jdbcTemplate.update(sql);
        }
    }

    private File createFile(String logRootDir) {
        String fileName = logRootDir + File.separator + "syslog" + DateUtils.getCurrentDate() + ".log";
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
    private String write(String logRootDir, List<SysLog> logs) {
        boolean writeFlag = false;
        StringBuilder lineSb = new StringBuilder();

        File logFile = createFile(logRootDir);
        BufferedWriter bw = null;

        StringBuilder idBuffer = new StringBuilder();
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true)));
            for(int i = 0; i < logs.size(); i ++) {
                SysLog log = logs.get(i);
                String create_time = DateUtils.getDateTime(log.getCreate_time());
                lineSb.append(log.getSys_log_id()).append("\t").append(log.getSys_log_code()).append("\t").append(log.getSys_log_content()).append("\t").append(create_time).append("\n");
                bw.write(lineSb.toString());
                if(i < logs.size() -1) {
                    idBuffer.append(log.getSys_log_id()).append(",");
                } else {
                    idBuffer.append(log.getSys_log_id());
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
