package com.cjsz.tech.dev.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.dev.beans.DevicePageBean;
import com.cjsz.tech.dev.domain.DeviceVersionRel;

import java.util.List;

import org.springframework.data.domain.Sort;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DeviceVersionRelService {
	
	//新增设备版本关系
	void saveRel(DeviceVersionRel rel);
	
	//更新
	void updateRel(DeviceVersionRel rel);

	//删除
	void deleteRels(String ids);
	
	//根据版本id查询关系
	List<DeviceVersionRel> getRelByVersionIds(String ids);
	
	Object pageQuery(Sort sort, PageConditionBean pageCondition);

	//新增修改设备版本信息
	void saveDeviceVersion(DevicePageBean devicePage);

}
