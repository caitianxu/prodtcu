package com.cjsz.tech.dev.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.DevicePageBean;
import com.cjsz.tech.dev.domain.DeviceSetting;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DeviceSettingService extends IOfflineService {

	/**
	 * 分页查询配置列表
	 *
	 * @param sort
	 * @param pageCondition
	 * @return
	 */
	PageList pageQuery(Sort sort, PageConditionBean pageCondition);

	/**
	 * 新增配置
	 *
	 * @param conf
	 * @return
	 */
	DeviceSetting saveConfigure(DeviceSetting conf);

	/**
	 * 更新配置
	 *
	 * @param conf
	 * @return
	 */
	DeviceSetting updateConfigure(DeviceSetting conf);

	/**
	 * 删除配置
	 *
	 * @param conf
	 */
	void deleteConfigure(DeviceSetting conf);

	/**
	 * 通过ID删除配置
	 *
	 * @param conf_id
	 */
	void deleteConfById(Long conf_id);

	/**
	 * 分页查询使用该配置的设备列表
	 *
	 * @param sort
	 * @param pageConditionBean
	 * @return
	 */
	Object selectDevices(Sort sort, DevicePageBean pageConditionBean);

	DeviceSetting findByName(String conf_name);

	/**
	 * 获取所有设备列表
	 * @param sort
	 * @param bean
	 * @return
	 */
	Object getDeviceList(DevicePageBean bean,Sort sort );

	/**
	 * 更新配置以推送
	 * @param conf_id
	 */
	void updateTimeByConfId(Long conf_id);
}
