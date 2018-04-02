package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.conditions.SysActionLogCondition;
import com.cjsz.tech.system.domain.SysActionLog;
import com.cjsz.tech.system.provider.SysActionLogProvider;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;


import java.util.List;


/**
 * @author Bruce
 * @version V1.0
 * @Title: 系统操作日志
 * @Description:
 * @date 2016-11-09 13:26:26
 */

public interface SysActionLogMapper extends BaseMapper<SysActionLog> {

    /**
     * 根据日志ID查找日志
     * @param id
     * @return
     */
    @Select("select * from sys_action_log where action_log_id = #{0}")
    public SysActionLog findById(Long id);

    /**
     * 查询所有日志
     * @return
     */
    @Select("select * from sys_action_log")
    public List<SysActionLog> findList();

    /**
     * 通过ID删除日志
     * @param id
     */
    @Delete("delete  from sys_action_log where action_log_id = #{0}")
    public void deleteById(Long id);

    /**
     * 根据条件查询系统操作日志
     * @param condition
     * @return
     */
    @SelectProvider(type = SysActionLogProvider.class, method = "queryByParams")
    List<SysActionLog> queryByParams(@Param("condition") SysActionLogCondition condition);

    @SelectProvider(type = SysActionLogProvider.class, method = "deleteByParams")
    void deleteByParams(@Param("condition") SysActionLogCondition condition);

}
