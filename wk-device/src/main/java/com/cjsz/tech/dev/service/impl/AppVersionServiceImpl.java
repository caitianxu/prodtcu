package com.cjsz.tech.dev.service.impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.DevicePageBean;
import com.cjsz.tech.dev.domain.AppVersion;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.mapper.AppVersionMapper;
import com.cjsz.tech.dev.service.AppVersionService;
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
public class AppVersionServiceImpl implements AppVersionService {

    @Autowired
    private AppVersionMapper appVersionMapper;

    @Override
    public Object pageQuery(Sort sort, PageConditionBean pageConditionBean) {
        PageHelper.startPage(pageConditionBean.getPageNum(), pageConditionBean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<AppVersion> result = appVersionMapper.getAppVersionList();
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public List<AppVersion> getAllVersions() {
        return appVersionMapper.getAppVersionList();
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "大屏版本")
    public void saveAppVersion(AppVersion appVersion) {
        appVersionMapper.insert(appVersion);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "大屏版本")
    public void updateAppVersion(AppVersion appVersion) {
        appVersionMapper.updateByPrimaryKey(appVersion);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "大屏版本")
    public void deleteByVersionIds(String version_ids_str) {
        appVersionMapper.deleteByVersionIds(version_ids_str);
    }

    @Override
    public Object selectDevices(Sort sort, DevicePageBean devicePageBean) {
        PageHelper.startPage(devicePageBean.getPageNum(), devicePageBean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<Device> result = appVersionMapper.getDeviceVersionRel(devicePageBean.getVersion_id());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "大屏版本")
    public void updateEnabled(AppVersion appVersion) {
        //是否使用（1：是；2：否）
        appVersionMapper.updateEnabled(2);
        appVersionMapper.updateByPrimaryKey(appVersion);
    }

    @Override
    public List<AppVersion> selectByVersionIds(String appVersion_ids_str) {
        return appVersionMapper.selectByVersionIds(appVersion_ids_str);
    }

    @Override
    public AppVersion selectEnabled() {
        return appVersionMapper.selectEnabled();
    }
}
