package com.cjsz.tech.system.service;

import com.cjsz.tech.system.domain.OrgExtend;

import java.util.List;


/**
 * 机构扩展属性
 * Created by Administrator on 2016/10/25.
 */
public interface OrgExtendService {

	// 新增
    public void saveExtend(OrgExtend extend);
    
    //更新
    public void updateExtend(OrgExtend extend);
    
    //根据id删除
    public void deleteById(Long extend_id);
    
    //根据id查询
    public OrgExtend selectById(Long extend_id);

    //根据id查询
    public OrgExtend getByOrgId(Long org_id);

    //根据id删除
    public void updateIsDelete(Long org_id);

    //根据简称查询
	public OrgExtend getByShortName(String short_name);

    //根据项目编号查询
    public List<OrgExtend> selectByProjectCode(String project_code);
}
