package com.cjsz.tech.dev.service.impl;

import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.DevicePageBean;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.domain.DeviceVersionRel;
import com.cjsz.tech.dev.mapper.DeviceMapper;
import com.cjsz.tech.dev.mapper.DeviceVersionRelMapper;
import com.cjsz.tech.dev.service.DeviceVersionRelService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
@Service
public class DeviceVersionRelServiceImpl implements DeviceVersionRelService{
	
    @Autowired
    private DeviceVersionRelMapper deviceVersionRelMapper;
    
    @Autowired
    private DeviceMapper deviceMapper;

    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "设备版本关系")
	public void saveRel(DeviceVersionRel rel) {
    	deviceVersionRelMapper.insert(rel);		
	}

    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "设备版本关系")
	public void updateRel(DeviceVersionRel rel) {
    	deviceVersionRelMapper.updateByPrimaryKey(rel);		
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "设备版本关系")
	public void deleteRels(String ids) {
		deviceVersionRelMapper.deleteRels(ids);
	}

	@Override
	public List<DeviceVersionRel> getRelByVersionIds(String ids) {
		return deviceVersionRelMapper.getRelByVersionIds(ids);
	}

	@Override
	public Object pageQuery(Sort sort, PageConditionBean pageCondition) {
		PageHelper.startPage(pageCondition.getPageNum(), pageCondition.getPageSize());
  		String order = ConditionOrderUtil.prepareOrder(sort);
  		if (order != null) {
  			PageHelper.orderBy(order);
  		}
  		List<Device> result = deviceVersionRelMapper.getList(pageCondition.getSearchText());
  		PageList pageList = new PageList(result, null);
  		return pageList;
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "设备版本关系")
	public void saveDeviceVersion(DevicePageBean devicePage) {
		String dev_ids = StringUtils.join(devicePage.getDevice_ids(), ",");
		//删除已存在的设备版本。
		deviceVersionRelMapper.deleteRels(dev_ids);
		List<DeviceVersionRel> rels = new ArrayList<DeviceVersionRel>();
		List<Device> devList = deviceMapper.getListByIds(dev_ids);
		for(Device dev : devList){
			DeviceVersionRel rel = new DeviceVersionRel();
			rel.setDevice_id(dev.getDevice_id());
			rel.setVersion_id(devicePage.getVersion_id());
			rel.setOrg_id(dev.getOrg_id());
			rel.setCreate_time(new Date());
			rel.setUpdate_time(new Date());
			rels.add(rel);
		}
		deviceVersionRelMapper.insertList(rels);		
	}
}
