package com.cjsz.tech.dev.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.dev.domain.DeviceAudit;

import java.util.List;

import org.springframework.data.domain.Sort;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DeviceAuditService {
	
	//新增设备审核
	void saveDeviceAudit(DeviceAudit device);
	
	//更新
	void updateDeviceAudit(DeviceAudit device);
	
	//分页查询
	Object pageQuery(Sort sort, PageConditionBean pageCondition);

	//获取新增数据的id
	Long getMaxId();

	//根据id查询
	DeviceAudit selectById(Long deviceId);

	//通过ids查询审核状态
	List<Integer> getAuditStatus(String ids);

	//通过ids更新审核状态
	void updateStatusByIds(Integer status, String ids);
	
	//通过设备code，用户查询审核记录
	DeviceAudit getByParam(String device_code, Long org_id, Long user_id);

	//通过设备code，查询待审核记录
	DeviceAudit getWaitAudit(String device_code);
}
