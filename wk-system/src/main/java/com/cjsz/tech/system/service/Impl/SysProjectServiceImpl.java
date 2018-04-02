package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.TreeBean;
import com.cjsz.tech.system.domain.SysProject;
import com.cjsz.tech.system.mapper.OrganizationMapper;
import com.cjsz.tech.system.mapper.SysProjectMapper;
import com.cjsz.tech.system.service.SysProjectService;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pc on 2017/3/10.
 */
@Service
public class SysProjectServiceImpl  implements SysProjectService{

    @Autowired
    private SysProjectMapper sysProjectMapper;
    @Autowired
    private OrganizationMapper organizationMapper;

    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "项目")
    public void saveProject(SysProject sysProject){
        sysProject.setCreate_time(new Date());
        sysProjectMapper.insert(sysProject);
    }

    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "项目")
    public void updateProject(SysProject sysProject){
        sysProject.setUpdate_time(new Date());
        sysProjectMapper.updateByPrimaryKey(sysProject);
    }

    public SysProject selectById(Long project_id) {
        return sysProjectMapper.selectByPrimaryKey(project_id);
    }

    public List<SysProject> findAll(){
        return sysProjectMapper.selectAll();
    }

    //分页查询
    public Object pageQuery(Sort sort, PageConditionBean pageCondition) {
        PageHelper.startPage(pageCondition.getPageNum(), pageCondition.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<SysProject> result = getProjectList(pageCondition.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public Long getMaxProjectId() {
        return sysProjectMapper.getMaxProjectId();
    }

    public List<SysProject> getProjectList(String searchText) {
        return sysProjectMapper.getProjectList(searchText);
    }

    //获取全部项目
    @Override
    public List<SysProject> getAllProjectsBySearchText(String searchText){
        return sysProjectMapper.getProjectList(searchText);
    }

    @Override
    public SysProject getProjectByName(String project_name) {
        return sysProjectMapper.getProjectByName(project_name);
    }

    @Override
    public SysProject getProjectByCode(String project_code) {
        return sysProjectMapper.getProjectByCode(project_code);
    }

    @Override
    public Object getProOrgTree() {
        List<TreeBean> proList = sysProjectMapper.getCurProList();//当前存在并使用的项目
        List<TreeBean> orgList = organizationMapper.getCurOrgList();//当前存在并使用的机构
        for(int i = 0; i < proList.size(); i++){
            List<TreeBean> children = new ArrayList<TreeBean>();
            for(int j = 0; j < orgList.size(); j++){
                if (proList.get(i).getCode().equals(orgList.get(j).getCode())) {
                    children.add(orgList.get(j));
                    orgList.remove(j);
                    j--;
                }
            }
            proList.get(i).setChildren(children);
        }
        return proList;
    }

}
