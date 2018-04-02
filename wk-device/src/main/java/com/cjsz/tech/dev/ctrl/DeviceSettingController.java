package com.cjsz.tech.dev.ctrl;

import com.alibaba.fastjson.JSON;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.DelConfigBean;
import com.cjsz.tech.dev.beans.DevicePageBean;
import com.cjsz.tech.dev.beans.FindDeviceSettingBean;
import com.cjsz.tech.dev.beans.SimpleDeviceBean;
import com.cjsz.tech.dev.domain.ConfContent;
import com.cjsz.tech.dev.domain.DeviceSetting;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.domain.DeviceConfRel;
import com.cjsz.tech.dev.service.ConfContentService;
import com.cjsz.tech.dev.service.DeviceSettingService;
import com.cjsz.tech.dev.service.DeviceConfRelService;
import com.cjsz.tech.dev.service.DeviceService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.system.utils.RequestContextUtil;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 配置管理
 * Created by Li Yi on 2016/12/22.
 */
@Controller
@RequestMapping("/admin/deviceset")
public class DeviceSettingController {

    @Autowired
    private DeviceSettingService deviceSettingService;

    @Autowired
    private DeviceConfRelService deviceConfRelService;

    @Autowired
    private ConfContentService confContentService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserService userService;

    /**
     * 配置分页列表
     *
     * @param request
     * @param pageCondition
     * @return
     */
    @RequestMapping(value = "/configList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object configList(HttpServletRequest request, @RequestBody PageConditionBean pageCondition) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if(sysUser == null){
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Sort sort = new Sort(Sort.Direction.ASC, "is_default");
            PageList data = deviceSettingService.pageQuery(sort, pageCondition);
            if(data.getRows()!=null && data.getRows().size()>0) {
                List<FindDeviceSettingBean> list = data.getRows();
                Map<String,List<ConfContent>> cache = new HashMap<String,List<ConfContent>>();
                for(int i=0;i<list.size();i++) {
                    FindDeviceSettingBean curBean = list.get(i);
                    Long conf_id = curBean.getConf_id();
                    List<ConfContent> contents = cache.get(conf_id+"");
                    if(contents==null) {
                        contents = confContentService.getConfContentsByConfid(conf_id);
                    }
                    curBean.setTabList(contents);
                    curBean.setContent_num(curBean.getTabList().size());
                    List<SimpleDeviceBean> deviceList = deviceConfRelService.getDeviceListByConfid(conf_id);
                    curBean.setDevice_num(deviceList.size());
                    curBean.setDevice_ids(deviceList);
                }
            }
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增/修改配置
     *
     * @param request
     * @param deviceSetting
     * @return
     */
    @RequestMapping(value = "/updateConfig", method = RequestMethod.POST)
    @ResponseBody
    public Object updateConfig(HttpServletRequest request, @RequestBody DeviceSetting deviceSetting) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if(sysUser == null){
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            JsonResult result = JsonResult.getSuccess("");
            if (deviceSetting.getConf_id() == null) {
                DeviceSetting tmp = deviceSettingService.findByName(deviceSetting.getConf_name());
                if(tmp!=null) {
                    return JsonResult.getError("存在相同配置，保存失败。");
                }
                //新增
                deviceSetting.setIs_default(2);
                deviceSetting.setCreate_time(new Date());
                deviceSetting.setUpdate_time(new Date());
                deviceSettingService.saveConfigure(deviceSetting);
                DeviceSetting savedSetting = deviceSettingService.findByName(deviceSetting.getConf_name());


                List<ConfContent> confList = deviceSetting.getTabList();
                if(confList!=null && confList.size()>0) {
                    confContentService.deleteConfContentsByConfid(deviceSetting.getConf_id());
                    for(ConfContent content:confList) {
                        content.setCreate_time(new Date());
                        content.setUpdate_time(new Date());
                        content.setConf_id(savedSetting.getConf_id());
                    }
                    confContentService.saveContent(confList);
                }
                result.setMessage(Constants.ACTION_ADD);
            }
            else {
                //更新
                // 修改时如果更改了使用该配置的设备？先将原来使用该配置的设备关系全删除，再插入新关系？
                //先保存修改后的配置
                deviceSetting.setUpdate_time(new Date());
                deviceSettingService.updateConfigure(deviceSetting);
                List<ConfContent> confList = deviceSetting.getTabList();
                if(confList!=null && confList.size()>0) {
                    confContentService.deleteConfContentsByConfid(deviceSetting.getConf_id());
                    for(ConfContent content:confList) {
                        content.setUpdate_time(new Date());
                        content.setConf_id(deviceSetting.getConf_id());
                    }
                    confContentService.saveContent(confList);
                }
                result.setMessage(Constants.ACTION_UPDATE);
            }

            result.setData(deviceSetting);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除配置，如果有设备使用，则不能删除
     * @param request
     * @param delConfigBean
     * @return
     */
    @RequestMapping(value = "/delectConfig", method = RequestMethod.POST)
    @ResponseBody
    public Object delectConfig(HttpServletRequest request, @RequestBody DelConfigBean delConfigBean) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if(sysUser == null){
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            if(delConfigBean.getConfs()==null || delConfigBean.getConfs().size()==0) {
                return JsonResult.getError("非法删除!");
            }
            List<Long> confs = delConfigBean.getConfs();
            Iterator<Long> confsIter = confs.iterator();
            while(confsIter.hasNext()) {
                Long confid = confsIter.next();
                List<SimpleDeviceBean> checkRel = deviceConfRelService.getDeviceListByConfid(confid);
                if (checkRel != null && checkRel.size() > 0) {
                    return JsonResult.getError("配置已有设备在使用，无法删除");
                }
            }
            Iterator<Long> confsIter2 = confs.iterator();
            while(confsIter2.hasNext()) {
                Long curconfid = confsIter2.next();
                deviceSettingService.deleteConfById(curconfid);
            }
            JsonResult result = JsonResult.getSuccess("");
            result.setMessage(Constants.ACTION_DELETE);
            result.setData(new Object());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 设置默认配置
     *
     * @param request
     * @param deviceSetting
     * @return
     */
    @RequestMapping(value = "/updateDefault", method = RequestMethod.POST)
    @ResponseBody
    public Object updateDefault(HttpServletRequest request, @RequestBody DeviceSetting deviceSetting) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if(sysUser == null){
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            deviceSetting = deviceSettingService.updateConfigure(deviceSetting);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(deviceSetting);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }

    }

    /**
     * 分页获取使用该配置的设备列表
     *
     * @param request
     * @param pageConditionBean
     * @return
     */
    @RequestMapping(value = "/selectDevices", method = RequestMethod.POST)
    @ResponseBody
    public Object selectDevices(HttpServletRequest request, @RequestBody DevicePageBean pageConditionBean){
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if(sysUser == null){
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "update_time").and(new Sort(Sort.Direction.DESC, "create_time"));
            Object data = deviceSettingService.selectDevices(sort, pageConditionBean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除使用某配置的设备列表
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/deleteDevices", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteConfDevice(HttpServletRequest request, @RequestBody DevicePageBean bean) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if(sysUser == null){
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            if (null != bean.getConf_id()) {
                if (null != bean.getDevice_ids() && bean.getDevice_ids().length != 0) {
                    List<DeviceConfRel> deviceConfRels = new ArrayList<>();
                    for (Long device_id : bean.getDevice_ids()) {
                        Device device = deviceService.selectById(device_id);
                        DeviceConfRel deviceConfRel = new DeviceConfRel();
                        deviceConfRel.setConf_id(bean.getConf_id());
                        deviceConfRel.setDevice_id(device.getDevice_id());
                        deviceConfRel.setOrg_id(device.getOrg_id());
                        deviceConfRels.add(deviceConfRel);
                    }
                    deviceConfRelService.deleteRelList(deviceConfRels);
                }
            }
            JsonResult result = JsonResult.getSuccess("");
            result.setMessage(Constants.ACTION_DELETE);
            result.setData(new Object());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 选取设备更新设备配置
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/updateDeviceConfig", method = RequestMethod.POST)
    @ResponseBody
    public Object updateDeviceConfig(HttpServletRequest request, @RequestBody DeviceConfRel bean) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try{
            if (bean.getDevice_id() == null){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            if (bean.getConf_id() == null){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            //查找设备是否存已存在配置，若有，删除原有配置使用当前配置；
            DeviceConfRel devConRel = deviceConfRelService.selectByDeviceOrgConf(bean);
            if (devConRel.getOrg_id()==bean.getOrg_id() && devConRel.getDevice_id()==bean.getDevice_id() && devConRel.getConf_id()==bean.getConf_id()){
                return JsonResult.getError(Constants.ACTION_ERROR);
            }
            bean.setUpdate_time(new Date());
            if (devConRel == null ){
                deviceConfRelService.insertDeviceConfRel(bean);
            }else {
                deviceConfRelService.deleteRel(bean);
                deviceConfRelService.insertDeviceConfRel(bean);
            }
            deviceSettingService.updateTimeByConfId(bean.getConf_id());
            //更新该配置菜单时间以推送菜单信息
            confContentService.updateContentTime(bean.getConf_id());
            //更改被更新配置设备时间已推送设备信息
            deviceService.updateDeviceTime(bean.getDevice_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 所有设备列表（已使用设备列表）
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/getDeviceList", method = RequestMethod.POST)
    @ResponseBody
    public Object getDeviceList(HttpServletRequest request, @RequestBody DevicePageBean bean) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "conf_id");
            if (bean.getConf_id() == null){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            PageList result = (PageList)deviceSettingService.getDeviceList(bean,sort);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
