package com.cjsz.tech.dev.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.DeviceAuditBean;
import com.cjsz.tech.dev.beans.DeviceInfoBean;
import com.cjsz.tech.dev.beans.OrgDeviceBean;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.service.DeviceService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 设备审核
 * Created by Administrator on 2016/10/24.
 */
@Controller
@RequestMapping("/admin/device")
public class DeviceController {

    @Autowired
    DeviceService deviceService;

    @Autowired
    UserService userService;

    /**
     * 设备列表
     *
     * @return
     */
    @RequestMapping("/listAll")
    @ResponseBody
    public Object listAll(HttpServletRequest request, @RequestBody PageConditionBean pageCondition) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "d.update_time");
            PageList result = (PageList) deviceService.pageQuery(sort, pageCondition, sysUser.getOrg_id(), null);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 设备监控列表。查看启用的设备。
     *
     * @return
     */
    @RequestMapping("/auditList")
    @ResponseBody
    public Object auditList(HttpServletRequest request, @RequestBody PageConditionBean pageCondition) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "d.sync_time").and(new Sort(Sort.Direction.ASC, "d.memory"));
            PageList result = (PageList) deviceService.pageQuery(sort, pageCondition, sysUser.getOrg_id(), 1);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 启用，停用
     *
     * @return
     */
    @RequestMapping("/audit")
    @ResponseBody
    public Object auditDevice(HttpServletRequest request, @RequestBody DeviceAuditBean deviceBean) {
        String ids = Arrays.toString(deviceBean.getIds());
        String da_ids = ids.substring(1, ids.length() - 1);
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Integer status;
            if (deviceBean.getMark().equals("enabled")) {
                status = Constants.ENABLE;
            } else {
                status = Constants.DISABLE;
            }
            deviceService.updateStatusByIds(status, da_ids);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 启用，停用
     *
     * @return
     */
    @RequestMapping("/changeIsSync")
    @ResponseBody
    public Object changeIsSync(HttpServletRequest request, @RequestBody Device device) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            deviceService.updateIsSyncById(device.getDevice_id(), device.getIs_sync());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public Object updateDevice(HttpServletRequest request, @RequestBody Device device) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            return deviceService.updateDeviceInfo(device);
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 添加设备监控信息
     *
     * @return
     */
    @RequestMapping("/addInfo")
    @ResponseBody
    public Object addInfo(HttpServletRequest request, @RequestBody Device device) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            return deviceService.saveDeviceInfo(device);
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取机构及机构设备树
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getOrgDevices", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getOrgDevices(HttpServletRequest request) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            List<OrgDeviceBean> orgDeviceBeans = deviceService.getDeviceOrg();
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(orgDeviceBeans);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 图书离线管理获取机构启用的设备当前信息（在线离线、剩余内存、设备离线图书数量）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getDeviceInfoBean", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getDeviceInfoBean(HttpServletRequest request) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            List<DeviceInfoBean> deviceInfoBeans = deviceService.getDeviceInfoBean(sysUser.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(deviceInfoBeans);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
