package com.cjsz.tech.dev.service;

import com.cjsz.tech.dev.beans.AppTypeOrgRelBean;
import com.cjsz.tech.dev.domain.AppType;
import com.cjsz.tech.dev.domain.AppTypeOrgRel;

import java.util.List;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
public interface AppTypeService {


    //获取app类型列表
    List<AppType> getList();

    //新增app类型
    void add(AppType bean);

    //修改app类型
    void update(AppType bean);

    //批量删除app类型
    void deleteByIds(String ids);

    //新增app类型机构关系
    void addOrg(AppTypeOrgRel bean);

    //查询机构是否分配该app类型配置
    AppTypeOrgRel selectById(Long org_id);

    //app类型机构关系列表
    Object getOrgList(AppTypeOrgRelBean bean);

    List<AppType> getAllList();
}
