package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.beans.TreeBean;
import com.cjsz.tech.system.domain.SysProject;
import com.cjsz.tech.system.provider.SysProjectProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by pc on 2017/3/10.
 */
public interface SysProjectMapper extends BaseMapper<SysProject>{

    @SelectProvider(type = SysProjectProvider.class, method = "selectAll")
    List<SysProject> getProjectList(@Param("keyword") String keyword);

    @Select("select * from sys_project where project_name = #{0}")
    SysProject getProjectByName(String project_name);

    @Select("select project_id from sys_project order by project_id desc limit 1")
    Long getMaxProjectId();

    @Select("select * from sys_project where project_code = #{0}")
    SysProject getProjectByCode(String project_code);

    //当前存在并使用的项目
    @Select("select project_id id, project_code code, project_name text, 'pro' type from sys_project")
    List<TreeBean> getCurProList();
}
