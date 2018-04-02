package com.cjsz.tech.dev.service.impl;

import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.AppTypeOrgRelBean;
import com.cjsz.tech.dev.beans.OrgListBean;
import com.cjsz.tech.dev.domain.AppType;
import com.cjsz.tech.dev.domain.AppTypeOrgRel;
import com.cjsz.tech.dev.mapper.AppTypeMapper;
import com.cjsz.tech.dev.mapper.AppTypeOrgRelMapper;
import com.cjsz.tech.dev.service.AppTypeService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@Service
public class AppTypeServiceImpl implements AppTypeService {
    @Autowired
    private AppTypeOrgRelMapper appTypeOrgRelMapper;

    @Autowired
    private AppTypeMapper appTypeMapper;



    @Override
    public List<AppType> getList() {
        List<AppType> list = appTypeMapper.getList();
        Integer count = 0;
        for (AppType appType:list){
            count = appTypeOrgRelMapper.selectCountById(appType.getApp_type_id());
            appType.setOrg_count(count);
        }
        return list;
    }

    @Override
    public List<AppType> getAllList() {
        List<AppType> list = appTypeMapper.getList();
        return list;
    }

    @Override
    public void add(AppType bean) {
        appTypeMapper.insert(bean);
    }

    @Override
    public void update(AppType bean) {
        appTypeMapper.updateByPrimaryKey(bean);
    }

    @Override
    public void deleteByIds(String ids) {
        appTypeMapper.deleteByIds(ids);
    }

    @Override
    public void addOrg(AppTypeOrgRel bean) {
        AppTypeOrgRel rel = appTypeOrgRelMapper.selectByOrgId(bean.getOrg_id());
        if (rel == null) {
            bean.setCreate_time(new Date());
            appTypeOrgRelMapper.insert(bean);
        }else{
            rel.setApp_type_id(bean.getApp_type_id());
            appTypeOrgRelMapper.updateByPrimaryKey(rel);
        }
    }

    @Override
    public AppTypeOrgRel selectById(Long org_id) {
        return appTypeOrgRelMapper.selectById(org_id);
    }


    @Override
    public Object getOrgList(AppTypeOrgRelBean bean) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        List<OrgListBean> result = appTypeOrgRelMapper.orgList(bean.getApp_type_id(),bean.getSearchText());
        PageList pageList = new PageList(result,null);
        return pageList;
    }


}
