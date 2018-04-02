package com.cjsz.tech.dev.service.impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.domain.AppVersionAndroid;
import com.cjsz.tech.dev.mapper.AppVersionAndroidMapper;
import com.cjsz.tech.dev.service.AppVersionAndroidService;
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
public class AppVersionAndroidServiceImpl implements AppVersionAndroidService {

    @Autowired
    private AppVersionAndroidMapper appVersionAndroidMapper;

    @Override
    public Object pageQuery(Sort sort, PageConditionBean pageConditionBean) {
        PageHelper.startPage(pageConditionBean.getPageNum(), pageConditionBean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<AppVersionAndroid> result = appVersionAndroidMapper.getAppVersionList();
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public List<AppVersionAndroid> getAllVersions() {
        return appVersionAndroidMapper.getAppVersionList();
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "安卓版本")
    public void saveAppVersion(AppVersionAndroid appVersion) {
        appVersionAndroidMapper.insert(appVersion);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "安卓版本")
    public void updateAppVersion(AppVersionAndroid appVersion) {
        appVersionAndroidMapper.updateByPrimaryKey(appVersion);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "安卓版本")
    public void deleteByVersionIds(String version_ids_str) {
        appVersionAndroidMapper.deleteByVersionIds(version_ids_str);
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "安卓版本")
    public void updateEnabled(AppVersionAndroid appVersion) {
        //是否使用（1：是；2：否）
        appVersionAndroidMapper.updateEnabled(2,appVersion.getApp_type_id());
        appVersionAndroidMapper.updateByPrimaryKey(appVersion);
    }

    @Override
    public List<AppVersionAndroid> selectByVersionIds(String appVersion_ids_str) {
        return appVersionAndroidMapper.selectByVersionIds(appVersion_ids_str);
    }

    @Override
    public AppVersionAndroid selectEnabled(Integer app_type) {
        return appVersionAndroidMapper.selectEnabled(app_type);
    }

}
