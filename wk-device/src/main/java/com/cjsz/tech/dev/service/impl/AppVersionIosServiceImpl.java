package com.cjsz.tech.dev.service.impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.domain.AppVersionIos;
import com.cjsz.tech.dev.mapper.AppVersionIosMapper;
import com.cjsz.tech.dev.service.AppVersionIosService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Li Yi on 2016/12/20.
 */
@Service
public class AppVersionIosServiceImpl implements AppVersionIosService {

    @Autowired
    private AppVersionIosMapper appVersionIosMapper;

    @Override
    public Object pageQuery(Sort sort, PageConditionBean pageConditionBean) {
        PageHelper.startPage(pageConditionBean.getPageNum(), pageConditionBean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<AppVersionIos> result = appVersionIosMapper.getAppVersionList();
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public List<AppVersionIos> getAllVersions() {
        return appVersionIosMapper.getAppVersionList();
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "IOS版本")
    public void saveAppVersion(AppVersionIos appVersion) {
        appVersionIosMapper.insert(appVersion);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "IOS版本")
    public void updateAppVersion(AppVersionIos appVersion) {
        appVersionIosMapper.updateByPrimaryKey(appVersion);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "IOS版本")
    public void deleteByVersionIds(String version_ids_str) {
        appVersionIosMapper.deleteByVersionIds(version_ids_str);
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "IOS版本")
    public void updateEnabled(AppVersionIos appVersion) {
        //是否使用（1：是；2：否）
        appVersionIosMapper.updateEnabled(2,appVersion.getApp_type_id());
        appVersionIosMapper.updateByPrimaryKey(appVersion);
    }

    @Override
    public List<AppVersionIos> selectByVersionIds(String appVersion_ids_str) {
        return appVersionIosMapper.selectByVersionIds(appVersion_ids_str);
    }

    @Override
    public AppVersionIos selectEnabled(Integer app_type) {
        return appVersionIosMapper.selectEnabled(app_type);
    }

}
