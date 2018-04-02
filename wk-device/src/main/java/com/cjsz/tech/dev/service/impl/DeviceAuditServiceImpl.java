package com.cjsz.tech.dev.service.impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.dev.domain.DeviceAudit;
import com.cjsz.tech.dev.mapper.DeviceAuditMapper;
import com.cjsz.tech.dev.service.DeviceAuditService;
import com.github.pagehelper.PageHelper;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by Administrator on 2016/10/25.
 */
@Service
public class DeviceAuditServiceImpl implements DeviceAuditService{
	
    @Autowired
    private DeviceAuditMapper deviceAuditMapper;

    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "设备审核")
	@Transactional
	public void saveDeviceAudit(DeviceAudit device) {
		//如果该设备此前有发送审核的请求，且状态为待审核，则直接驳回
		DeviceAudit waitAuditObj = deviceAuditMapper.getWaitAudit(device.getDevice_code());
		if(waitAuditObj != null){
			waitAuditObj.setAudit_status(1);
			deviceAuditMapper.updateByPrimaryKey(waitAuditObj);
		}
		device.setApply_time(new Date());
		device.setAudit_status(Constants.AUDIT_STATUS_WAIT);
		deviceAuditMapper.insert(device);

	}

    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "设备审核")
	public void updateDeviceAudit(DeviceAudit device) {
		deviceAuditMapper.updateByPrimaryKey(device);		
	}

	@Override
	public Object pageQuery(Sort sort, PageConditionBean pageCondition) {
		PageHelper.startPage(pageCondition.getPageNum(), pageCondition.getPageSize());
  		String order = ConditionOrderUtil.prepareOrder(sort);
  		if (order != null) {
  			PageHelper.orderBy(order);
  		}
  		List<DeviceAudit> result = deviceAuditMapper.getList(pageCondition.getSearchText());
  		PageList pageList = new PageList(result, null);
  		return pageList;
	}

	@Override
	public Long getMaxId() {
		return deviceAuditMapper.getMaxId();
	}

	@Override
	public DeviceAudit selectById(Long deviceId) {
		return deviceAuditMapper.selectByPrimaryKey(deviceId);
	}

	@Override
	public List<Integer> getAuditStatus(String ids) {
		return deviceAuditMapper.getAuditStatus(ids);
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "设备审核")
	public void updateStatusByIds(Integer status, String ids) {
		deviceAuditMapper.updateStatusByIds(status, ids);		
	}

	@Override
	public DeviceAudit getByParam(String device_code, Long org_id, Long user_id) {
		return deviceAuditMapper.getByParam(device_code, org_id, user_id);
	}

	@Override
	public DeviceAudit getWaitAudit(String device_code) {
		return deviceAuditMapper.getWaitAudit(device_code);
	}
}
