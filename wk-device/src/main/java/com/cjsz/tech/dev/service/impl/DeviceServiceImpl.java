package com.cjsz.tech.dev.service.impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.DeviceAuditBean;
import com.cjsz.tech.dev.beans.DeviceInfoBean;
import com.cjsz.tech.dev.beans.OrgDeviceBean;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.domain.DeviceAudit;
import com.cjsz.tech.dev.domain.DeviceConfRel;
import com.cjsz.tech.dev.domain.DeviceSetting;
import com.cjsz.tech.dev.mapper.ConfContentMapper;
import com.cjsz.tech.dev.mapper.DeviceAuditMapper;
import com.cjsz.tech.dev.mapper.DeviceMapper;
import com.cjsz.tech.dev.mapper.DeviceSettingMapper;
import com.cjsz.tech.dev.service.DeviceConfRelService;
import com.cjsz.tech.dev.service.DeviceService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.PageHelper;
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
public class DeviceServiceImpl implements DeviceService{

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    DeviceAuditMapper deviceAuditMapper;

	@Autowired
	DeviceSettingMapper deviceSettingMapper;

	@Autowired
	ConfContentMapper confContentMapper;

	@Autowired
	DeviceConfRelService deviceConfRelService;

	@Autowired
	JdbcTemplate jdbcTemplate;

    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "设备")
	public void saveDevice(Device device) {
    	device.setEnabled(Constants.ENABLE);
		device.setCreate_time(new Date());
		device.setUpdate_time(new Date());
		deviceMapper.insert(device);
	}

    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "设备")
	public void updateDevice(Device device) {
    	device.setUpdate_time(new Date());
		deviceMapper.updateByPrimaryKey(device);
	}

	@Override
	public Object pageQuery(Sort sort, PageConditionBean pageCondition, Long org_id, Integer enabled) {
		PageHelper.startPage(pageCondition.getPageNum(), pageCondition.getPageSize());
  		String order = ConditionOrderUtil.prepareOrder(sort);
  		if (order != null) {
  			PageHelper.orderBy(order);
  		}
  		List<Device> result = deviceMapper.getList(pageCondition.getSearchText(), org_id, enabled);
  		for (Device device : result){
  			if (device.getSync_time()!=null) {
				if (((new Date().getTime() - device.getSync_time().getTime())/1000) < 600) {
					device.setStatus(2);
				}else {
					device.setStatus(1);
				}
			}else {
				device.setStatus(1);
			}
		}
  		PageList pageList = new PageList(result, null);
  		return pageList;
	}

	@Override
	public Long getMaxId() {
		return deviceMapper.getMaxId();
	}

	@Override
	public Device selectById(Long deviceId) {
		return deviceMapper.selectByPrimaryKey(deviceId);
	}

	@Transactional
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "设备")
	public void initDevice(String ids, Integer status, DeviceAuditBean deviceBean) {
		List<DeviceAudit> daList = deviceAuditMapper.getListByIds(ids, status);
		List<Device> devices = deviceMapper.getAllList();
		List<String> dev_codes = new ArrayList<String>();
		List<DeviceConfRel> deviceConfRels = new ArrayList<DeviceConfRel>();
		for(Device device : devices){
			dev_codes.add(device.getDevice_code());
		}
		for(DeviceAudit da : daList){
			if(dev_codes.contains(da.getDevice_code())){
				//修改
				Device device = deviceMapper.getDeviceByCode(da.getDevice_code());
				if(!da.getOrg_id().equals(device.getOrg_id())){
					//删除图书设备离线关系
					String del_sql = "delete from book_device_rel where device_id = " + device.getDevice_id();
					jdbcTemplate.update(del_sql);
				}
				device.setOrg_id(da.getOrg_id());
				device.setUser_id(da.getUser_id());
				device.setEnabled(Constants.ENABLE);
				device.setUpdate_time(new Date());
				device.setProvince_id(deviceBean.getProvince_id());
				device.setProvince(deviceBean.getProvince());
				device.setCity_id(deviceBean.getCity_id());
				device.setCity(deviceBean.getCity());
				device.setArea_id(deviceBean.getArea_id());
				if (deviceBean.getArea().equals("无")){
					deviceBean.setArea("");
				}
				device.setArea(deviceBean.getArea());
				device.setLocation(deviceBean.getLocation());
				device.setStreet(deviceBean.getStreet());
                device.setIs_sync(Constants.DISABLE);
				deviceMapper.updateByPrimaryKey(device);

				//大屏设备配置
				DeviceConfRel deviceConfRel = deviceConfRelService.selectByDeviceId(device.getDevice_id());
				if(deviceConfRel != null){
					deviceConfRel.setOrg_id(da.getOrg_id());
					deviceConfRelService.updateRelOrgId(deviceConfRel);
				}else{
					deviceConfRel = new DeviceConfRel();
					deviceConfRel.setOrg_id(da.getOrg_id());
					deviceConfRel.setDevice_id(device.getDevice_id());
					deviceConfRel.setConf_id(17L);
					deviceConfRels.add(deviceConfRel);
				}
			}else{
				//新增
				Device device = new Device();
				device.setDevice_code(da.getDevice_code());
				device.setOrg_id(da.getOrg_id());
				device.setUser_id(da.getUser_id());
				device.setEnabled(Constants.ENABLE);
				device.setCreate_time(new Date());
				device.setUpdate_time(new Date());
				device.setProvince_id(deviceBean.getProvince_id());
				device.setProvince(deviceBean.getProvince());
				device.setCity_id(deviceBean.getCity_id());
				device.setCity(deviceBean.getCity());
				device.setArea_id(deviceBean.getArea_id());
				device.setArea(deviceBean.getArea());
				device.setLocation(deviceBean.getLocation());
				device.setStreet(deviceBean.getStreet());
                device.setIs_sync(Constants.DISABLE);
				deviceMapper.insert(device);

				DeviceConfRel deviceConfRel = new DeviceConfRel();
				deviceConfRel.setOrg_id(da.getOrg_id());
				deviceConfRel.setDevice_id(device.getDevice_id());
				deviceConfRel.setConf_id(17L);
				deviceConfRels.add(deviceConfRel);
			}
		}
		if(deviceConfRels.size()>0){
			deviceConfRelService.saveDeviceConfRel(deviceConfRels);
		}

	}

	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "设备")
	public void updateStatusByIds(Integer status, String da_ids) {
		deviceMapper.updateStatusByIds(status, da_ids);
	}

	@Override
	public Device getDevice(Long org_id, Long user_id, String device_code) {
		return deviceMapper.getDevice(org_id, user_id, device_code);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "设备")
	public Object updateDeviceInfo(Device device) {
		Device data = selectById(device.getDevice_id());
		if(data == null){
			return JsonResult.getError(Constants.DEVICE_NOT_EXIST);
		}
		if (device.getArea().equals("无")){
			device.setArea("");
		}
		data.setProvince(device.getProvince());
		data.setProvince_id(device.getProvince_id());
		data.setCity(device.getCity());
		data.setCity_id(device.getCity_id());
		data.setArea(device.getArea());
		data.setArea_id(device.getArea_id());
		data.setStreet(device.getStreet());
		data.setLocation(device.getLocation());
		updateDevice(data);
		JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
		result.setData(data);
		return result;
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "设备")
	public Object saveDeviceInfo(Device device){
		Device data = getDevice(device.getOrg_id(), device.getUser_id(), device.getDevice_code());
		if(data == null){
			return JsonResult.getError(Constants.DEVICE_NOT_EXIST);
		}
		data.setMemory(device.getMemory());
		//data.setOff_line(device.getOff_line());
		data.setVersion(device.getVersion());
		updateDevice(data);
		JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
		result.setData(data);
		return result;
	}

	@Override
	public List<OrgDeviceBean> getDeviceOrg() {
		List<OrgDeviceBean> orgDeviceBeans = new ArrayList<OrgDeviceBean>();
		List<Organization> orgs = deviceMapper.getDeviceOrg();
		List<Device> devices = deviceMapper.getAllList();
		for(Organization org : orgs){
			OrgDeviceBean orgDev = new OrgDeviceBean();
			orgDev.setOrg_id(org.getOrg_id());
			orgDev.setOrg_name(org.getOrg_name());
			List<Device> deviceList = new ArrayList<Device>();
			for(Device dev : devices){
				if(org.getOrg_id().equals(dev.getOrg_id())){
					deviceList.add(dev);
				}
			}
			orgDev.setOrgDevices(deviceList);
			orgDeviceBeans.add(orgDev);
		}
		return orgDeviceBeans;
	}

	@Override
	public Device getDeviceByCode(String device_code) {
		return deviceMapper.getDeviceByCode(device_code);
	}

	@Override
	public DeviceSetting getDeviceConfig(Long device_id, Long update_time) {
		return deviceSettingMapper.getDeviceConfig(device_id, update_time);
	}

	@Override
	//图书离线管理获取机构启用的设备当前信息（在线离线、剩余内存、设备离线图书数量）
	public List<DeviceInfoBean> getDeviceInfoBean(Long org_id){
		return deviceMapper.getDeviceInfoBean(org_id);
	}

	@Override
	//所有设备
	public List<Device> findAll(){
		return deviceMapper.findAll();
	}

	@Override
	public Integer getCountByOrgId(Long org_id) {
		return deviceMapper.getCountByOrgId(org_id);
	}

	@Override
	public void updeatOfflineParam(String memory,String deviceCode,String version) {
		deviceMapper.updeatOfflineParam(memory,deviceCode,version);
	}


	@Override
    public Integer hasOffLine(Long orgid,String time,Object...otherparam) {
        Integer checknum = deviceMapper.checkOffLineNum(orgid,time, (Long)otherparam[0]);
        if(checknum==null ) {
            checknum =0;
        }
        return checknum;
    }

    @Override
    public List<Device> getOffLineNumList(Long orgid,String timev,Object...otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return deviceMapper.getOffLineNumList(orgid,timev, (Long)otherparam[0], num, size);
    }

    @Override
	public void updateDeviceOffLine(String offLineStatus,String deviceCode){
		if (offLineStatus.equals("start")){
			deviceMapper.updateStatusStart(deviceCode);
		}
		if (offLineStatus.equals("end")){
			deviceMapper.updateStatusEnd(deviceCode);
		}
	}

	@Override
	public void updateDeviceTime(Long device_id){
		deviceMapper.updateDeviceTime(device_id);
	}

    @Override
    public Long  selectByIds(String device_code) {
        return deviceMapper.selectByIds(device_code);
    }

    @Override
    public void updateIsSyncById(Long device_id, Integer is_sync) {
        deviceMapper.updateIsSyncById(device_id, is_sync);
    }

    @Override
    public List<String> getSyncDeviceCodes() {
        return deviceMapper.getSyncDeviceCodes();
    }
}
