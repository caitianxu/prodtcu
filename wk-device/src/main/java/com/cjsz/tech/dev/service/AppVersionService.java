package com.cjsz.tech.dev.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.dev.beans.DevicePageBean;
import com.cjsz.tech.dev.domain.AppVersion;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Li Yi on 2016/12/20.
 */
public interface AppVersionService {

    /**
     * 分页查询版本列表
     *
     * @param sort
     * @param pageConditionBean
     * @return
     */
    Object pageQuery(Sort sort, PageConditionBean pageConditionBean);

    /**
     * 不分页查询所有版本
     *
     * @return
     */
    List<AppVersion> getAllVersions();

    /**
     * 保存版本信息
     *
     * @param appVersion
     */
    void saveAppVersion(AppVersion appVersion);

    /**
     * 更新版本信息
     *
     * @param appVersion
     */
    void updateAppVersion(AppVersion appVersion);

    /**
     * 删除版本信息
     *
     * @param version_ids_str
     */
    void deleteByVersionIds(String version_ids_str);

    /**
     * 分页查询使用该版本的设备列表
     *
     * @param sort
     * @param devicePageBean
     * @return
     */
    Object selectDevices(Sort sort, DevicePageBean devicePageBean);

    /**
     * 更换当前使用版本
     *
     * @param appVersion
     */
    void updateEnabled(AppVersion appVersion);

    /**
     * 版本Ids查询版本
     *
     * @param appVersion_ids_str
     * @return
     */
    List<AppVersion> selectByVersionIds(String appVersion_ids_str);

    /**
     * 获取正在使用的版本
     * @return
     */
    AppVersion selectEnabled();

}
