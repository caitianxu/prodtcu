package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.conditions.AppNavCondition;
import com.cjsz.tech.system.domain.AppNav;
import com.cjsz.tech.system.mapper.AppNavMapper;
import com.cjsz.tech.system.service.AppNavService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
@Service
public class AppNavServiceImpl implements AppNavService {

    @Autowired
    private AppNavMapper appNavMapper;

    @Override
    public Object pageQuery(Sort sort, AppNavCondition condition) {
        PageHelper.startPage(condition.getPageNum(), condition.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<AppNav> result = appNavMapper.getAppNavList(condition.getSearchText(), condition.getOrg_id());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public Object getListByOrgIdAndSearch(Long org_id, String searchText) {
        return appNavMapper.getAppNavList(searchText, org_id);
    }

    @Override
    public void saveAppNav(AppNav appNav) {
        appNavMapper.insert(appNav);
    }

    @Override
    public void updateAppNav(AppNav appNav) {
        AppNav appNav1 = appNavMapper.selectByNavId(appNav.getNav_id());
        BeanUtils.copyProperties(appNav, appNav1);
        appNavMapper.updateByPrimaryKey(appNav);
    }

    @Override
    public void deleteByNavIds(String nav_ids_str) {
        appNavMapper.deleteByNavIds(nav_ids_str);
    }

    @Override
    public AppNav selectByNavId(Long nav_id) {
        return appNavMapper.selectByNavId(nav_id);
    }
}
