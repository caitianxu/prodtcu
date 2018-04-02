package com.cjsz.tech.dev.service.impl;

import com.cjsz.tech.dev.beans.SimpleDeviceBean;
import com.cjsz.tech.dev.domain.DeviceSetting;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.dev.domain.DeviceConfRel;
import com.cjsz.tech.dev.mapper.DeviceConfRelMapper;
import com.cjsz.tech.dev.service.DeviceConfRelService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
@Service
public class DeviceConfRelServiceImpl implements DeviceConfRelService{
	
    @Autowired
    private DeviceConfRelMapper deviceConfRelMapper;

	@Autowired
	JdbcTemplate jdbcTemplate;

    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "设备配置关系")
	public DeviceConfRel saveRel(DeviceConfRel rel) {
    	deviceConfRelMapper.insert(rel);
		return rel;
	}
    
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "设备配置关系")
	public List<DeviceConfRel> saveRels(DeviceSetting deviceSetting, List<Device> newDevices) {
		List<DeviceConfRel> newDeviceConfRels = new ArrayList<>();
		if(newDevices != null && newDevices.size() != 0) {
			if(deviceSetting.getDevice_ids().length>0){
				deleteByDeviceIds(StringUtils.join(deviceSetting.getDevice_ids(), ","));
			}
			for (Device device : newDevices){
				DeviceConfRel deviceConfRel = new DeviceConfRel();
				deviceConfRel.setDevice_id(device.getDevice_id());
				deviceConfRel.setOrg_id(device.getOrg_id());
				deviceConfRel.setConf_id(deviceSetting.getConf_id());
				deviceConfRelMapper.insert(deviceConfRel);
				newDeviceConfRels.add(deviceConfRel);
			}
		}
		return newDeviceConfRels;
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "设备配置关系")
	public void deleteRelList(List<DeviceConfRel> deviceConfRels) {
		if (deviceConfRels != null && deviceConfRels.size() != 0) {
			for (DeviceConfRel deviceConfRel : deviceConfRels) {
				deleteRel(deviceConfRel);
			}
		}
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "设备配置关系")
	public void deleteRel(DeviceConfRel deviceConfRel) {
		deviceConfRelMapper.deleteByPrimaryKey(deviceConfRel);
	}

	@Override
	public List<DeviceConfRel> selectDeviceConfRel(Long conf_id) {
		List<DeviceConfRel> deviceConfRels = new ArrayList<>();
		if (conf_id != null) {
			deviceConfRels = deviceConfRelMapper.selectDeviceConfRel(conf_id);
		}
		return deviceConfRels;
	}

	@Override
	public List<SimpleDeviceBean> getDeviceListByConfid(Long confid) {
		String sql = "select device_id as id,device_code as name from device where device_id in (select device_id from device_conf_rel where conf_id="+confid+")";
		return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(SimpleDeviceBean.class));
	}

	@Override
	public DeviceConfRel selectByDeviceId(Long device_id) {
		return deviceConfRelMapper.selectByDeviceId(device_id);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "设备配置关系")
	public void updateRelOrgId(DeviceConfRel deviceConfRel) {
		String sql = "update device_conf_rel set org_id = " + deviceConfRel.getOrg_id() + " where device_id = "+deviceConfRel.getDevice_id();
		jdbcTemplate.update(sql);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "设备配置关系")
	public void saveDeviceConfRel(List<DeviceConfRel> deviceConfRels) {
		for (DeviceConfRel deviceConfRel : deviceConfRels){
			deviceConfRelMapper.insert(deviceConfRel);
		}
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "设备配置关系")
	public void deleteByDeviceIds(String device_ids){
		deviceConfRelMapper.deleteByDeviceIds(device_ids);
	}

	@Override
	public DeviceConfRel selectByDeviceOrgConf (DeviceConfRel bean){
		return deviceConfRelMapper.selectByDeviceOrgConf(bean.getDevice_id());
	}

	@Override
	public void insertDeviceConfRel(DeviceConfRel bean){
		deviceConfRelMapper.insert(bean);
	}

}
