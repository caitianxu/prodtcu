package com.cjsz.tech.dev.service;

import com.cjsz.tech.dev.beans.SimpleDeviceBean;
import com.cjsz.tech.dev.domain.DeviceSetting;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.domain.DeviceConfRel;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DeviceConfRelService {
	
	//新增设备配置关系
	DeviceConfRel saveRel(DeviceConfRel rel);

	/**
	 * 保存某配置及使用该配置的设备列表
	 * 需要先删除旧的设备关系，再增添新设备关系
	 *
	 * @param deviceSetting
	 * @param newDevices
	 * @return
	 */
	List<DeviceConfRel> saveRels(DeviceSetting deviceSetting, List<Device> newDevices);

	/**
	 * 批量删除设备配置关系
	 *
	 * @param deviceConfRels
	 */
	void deleteRelList(List<DeviceConfRel> deviceConfRels);

	/**
	 * 删除一条设备配置关系
	 *
	 * @param deviceConfRel
	 */
	void deleteRel(DeviceConfRel deviceConfRel);

	/**
	 * 不分页查询该配置的设备关系列表
	 *
	 * @param conf_id
	 * @return
	 */
	List<DeviceConfRel> selectDeviceConfRel(Long conf_id);

	List<SimpleDeviceBean> getDeviceListByConfid(Long confid);

	
	DeviceConfRel selectByDeviceId(Long device_id);

	void updateRelOrgId(DeviceConfRel deviceConfRel);

	void saveDeviceConfRel(List<DeviceConfRel> deviceConfRels);

	/**
	 * 根据设备ID，机构ID，配置ID查找关系表是否存在已分配关系
	 * @param bean
	 * @return
	 */
	DeviceConfRel selectByDeviceOrgConf(DeviceConfRel bean);

	/**
	 * 新增设备-配置关系
	 * @param bean
	 */
	void insertDeviceConfRel(DeviceConfRel bean);

//	void deleteRelsByIdstr()
}
