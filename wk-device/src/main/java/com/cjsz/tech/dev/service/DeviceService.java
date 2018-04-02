package com.cjsz.tech.dev.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.dev.beans.DeviceAuditBean;
import com.cjsz.tech.dev.beans.DeviceInfoBean;
import com.cjsz.tech.dev.beans.OrgDeviceBean;
import com.cjsz.tech.dev.domain.DeviceSetting;
import com.cjsz.tech.system.service.IOfflineService;
import com.cjsz.tech.dev.domain.Device;

import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DeviceService extends IOfflineService {

	//新增设备
	void saveDevice(Device device);

	//更新
	void updateDevice(Device device);

	//分页查询
	Object pageQuery(Sort sort, PageConditionBean pageCondition, Long org_id, Integer enabled);

	//获取新增数据的id
	Long getMaxId();

	//根据id查询
	Device selectById(Long deviceId);

	//初始化设备
	void initDevice(String ids, Integer status, DeviceAuditBean deviceBean);

	//更新设备状态
	void updateStatusByIds(Integer status, String da_ids);

	//通过机构，用户， 设备编码查询设备
	Device getDevice(Long org_id, Long user_id, String device_code);

	//更新
	Object updateDeviceInfo(Device device);

	//添加设备监控信息
	Object saveDeviceInfo(Device device);

	//查询所有机构及对应的设备
	List<OrgDeviceBean> getDeviceOrg();

	//通过设备编码查询设备
	Device getDeviceByCode(String device_code);

	//通过设备ID及更新时间查询设备的配置
	DeviceSetting getDeviceConfig(Long device_id, Long update_time);

	//图书离线管理获取机构启用的设备当前信息（在线离线、剩余内存、设备离线图书数量）
	List<DeviceInfoBean> getDeviceInfoBean(Long org_id);

	//所有设备
	List<Device> findAll();

	/**
	 * 终端数量
	 *
	 * @param org_id
	 * @return
	 */
	Integer getCountByOrgId(Long org_id);

	/**
	 * 数据同步时：更新设备离线状态、剩余内存、同步时间、版本信息
	 * @param memory
	 */
	void updeatOfflineParam(String memory,String deviceCode,String version);

	/**
	 * 根据文件同步start，end判断是否在下载中
	 * @param offLineStatus
	 */
	void updateDeviceOffLine(String offLineStatus,String deviceCode);

	/**
	 * 更新设备更新时间
	 * @param device_id
	 */
	void updateDeviceTime(Long device_id);

	Long  selectByIds(String device_code);

    void updateIsSyncById(Long device_id, Integer is_sync);

    List<String> getSyncDeviceCodes();
}