package com.cjsz.tech.dev.service.impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.DevicePageBean;
import com.cjsz.tech.dev.beans.FindDeviceSettingBean;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.domain.DeviceSetting;
import com.cjsz.tech.dev.mapper.DeviceSettingMapper;
import com.cjsz.tech.dev.service.DeviceSettingService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
@Service
public class DeviceSettingServiceImpl implements DeviceSettingService {

	@Autowired
	private DeviceSettingMapper deviceSettingMapper;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public PageList pageQuery(Sort sort, PageConditionBean pageCondition) {
		PageHelper.startPage(pageCondition.getPageNum(), pageCondition.getPageSize());
		String order = ConditionOrderUtil.prepareOrder(sort);
		if (order != null) {
			PageHelper.orderBy(order);
		}
		List<FindDeviceSettingBean> result = deviceSettingMapper.pageQuery(pageCondition.getSearchText());
		PageList pageList = new PageList(result, null);
		return pageList;
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "设备配置")
	@Transactional
	public DeviceSetting saveConfigure(DeviceSetting conf) {
		deviceSettingMapper.insert(conf);
		return conf;
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "设备配置")
	@Transactional
	public DeviceSetting updateConfigure(DeviceSetting conf) {
		deviceSettingMapper.updateByPrimaryKey(conf);
		return conf;
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "设备配置")
	public void deleteConfigure(DeviceSetting conf) {
		deviceSettingMapper.deleteByPrimaryKey(conf);
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "设备配置")
	public void deleteConfById(Long conf_id) {
		String sql01 = "delete from device_setting where conf_id=" + conf_id;
		String sql02 = "delete from conf_content where conf_id=" + conf_id;
		String sql03 = "delete from device_conf_rel where conf_id=" + conf_id;
		jdbcTemplate.batchUpdate(sql01, sql02, sql03);
//		deviceSettingMapper.deleteByPrimaryKey(conf_id);
	}

	@Override
	public Object selectDevices(Sort sort, DevicePageBean devicePageBean) {
		PageHelper.startPage(devicePageBean.getPageNum(), devicePageBean.getPageSize());
		String order = ConditionOrderUtil.prepareOrder(sort);
		if (order != null) {
			PageHelper.orderBy(order);
		}
		List<Device> result = new ArrayList<Device>();
		if (StringUtils.isEmpty(devicePageBean.getSearchText())) {
			result = deviceSettingMapper.getDeviceConfRel(devicePageBean.getConf_id());
		} else {
			result = deviceSettingMapper.getDeviceConfRelByIdAndSearchText(devicePageBean.getConf_id(), devicePageBean.getSearchText());
		}
		PageList pageList = new PageList(result, null);
		return pageList;
	}

	@Override
	public DeviceSetting findByName(String conf_name) {
		return deviceSettingMapper.findByName(conf_name);
	}


	@Override
	public Integer hasOffLine(Long orgid, String time, Object... otherparam) {
		Integer checknum = deviceSettingMapper.checkOffLineNum(orgid, time, (Long) otherparam[0]);
		if (checknum == null) {
			checknum = 0;
		}
		return checknum;
	}

	@Override
	public List<DeviceSetting> getOffLineNumList(Long orgid, String timev, Object... otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
		return deviceSettingMapper.getOffLineNumList(orgid, timev, (Long) otherparam[0], num, size);
	}

	@Override
	public Object getDeviceList(DevicePageBean bean, Sort sort) {
		PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
		String order = ConditionOrderUtil.prepareOrder(sort);
		if (order != null) {
			PageHelper.orderBy(order);
		}
		List<Device> result = deviceSettingMapper.getDeviceList(bean);
		PageList pageList = new PageList(result, null);
		return pageList;
	}

	@Override
	public void updateTimeByConfId(Long conf_id){
		deviceSettingMapper.updateTime(conf_id);
	}

}
