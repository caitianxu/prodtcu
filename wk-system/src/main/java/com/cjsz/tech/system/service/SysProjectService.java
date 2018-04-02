package com.cjsz.tech.system.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.domain.SysProject;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by pc on 2017/3/10.
 */
public interface SysProjectService {

    //新增项目
    public void saveProject(SysProject sysProject);

    //更新角色
    public void updateProject(SysProject sysProject);

    //根据id查询角色
    public SysProject selectById(Long project_id);

    //查询所有项目
    public List<SysProject> findAll();

    //查询新增项目ID
    public Long getMaxProjectId();

    //分页查询
    public Object pageQuery(Sort sort, PageConditionBean pageCondition);

    //获取全部项目
    public List<SysProject> getAllProjectsBySearchText(String searchText);

    //根据项目名查询项目
    public SysProject getProjectByName(String project_name);

    //根据项目编号查询项目
    public SysProject getProjectByCode(String project_code);

    //项目机构树
    public Object getProOrgTree();
}
