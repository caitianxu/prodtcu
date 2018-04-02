package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.conditions.SysLogCondition;
import com.cjsz.tech.system.domain.SysLog;
import com.cjsz.tech.system.provider.SysLogProvider;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Param;
import java.util.List;





/**   
 * @Title: 系统异常日志
 * @Description: 
 * @author Bruce
 * @date 2016-11-09 13:26:25
 * @version V1.0   
 *
 */
public interface SysLogMapper extends BaseMapper<SysLog> {

    /**
     * 根据ID查找日志
     * @param id
     * @return
     */
    @Select("select * from sys_log where id=#{0}")
    public SysLog findById(Long id) ;

    /**
     * 查询所有记录
     * @return
     */
    @Select("select * from sys_log")
    public List<SysLog> findList() ;

    /**
     * 通过ID删除日志
     * @param id
     */
    @Delete("delete  from sys_log where id=#{0}")
    public void deleteById(Long id) ;

    /**
     * 根据条件查询系统日志
     * @param condition
     * @return
     */
    @SelectProvider(type = SysLogProvider.class, method = "queryByParams")
    List<SysLog> queryByParams(@Param("condition") SysLogCondition condition);
}
