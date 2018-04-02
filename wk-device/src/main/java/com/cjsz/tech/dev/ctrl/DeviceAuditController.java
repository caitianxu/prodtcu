package com.cjsz.tech.dev.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.DeviceAuditBean;
import com.cjsz.tech.dev.domain.DeviceAudit;
import com.cjsz.tech.dev.service.DeviceAuditService;
import com.cjsz.tech.dev.service.DeviceService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 设备审核
 * Created by Administrator on 2016/10/24.
 */
@Controller
@RequestMapping("/admin/deviceAudit")
public class DeviceAuditController {

    @Autowired
    DeviceAuditService deviceAuditService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    UserService userService;

    /**
     * 设备审核列表
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
            Order status = new Order(Direction.DESC, "audit_status");
            Order apply_time = new Order(Direction.DESC, "apply_time");
            Sort sort = new Sort(status, apply_time);
            PageList result = (PageList) deviceAuditService.pageQuery(sort, pageCondition);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增设备审核记录
     *
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public Object saveDeviceAudit(HttpServletRequest request, @RequestBody DeviceAudit device) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            deviceAuditService.saveDeviceAudit(device);
            Long deviceId = deviceAuditService.getMaxId();
            DeviceAudit data = deviceAuditService.selectById(deviceId);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_ADD);
            jsonResult.setData(data);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }

    }

    /**
     * 审核
     *
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public Object updateDeviceAudit(HttpServletRequest request, @RequestBody DeviceAuditBean deviceBean) {
        String ids = Arrays.toString(deviceBean.getIds());
        String da_ids = ids.substring(1, ids.length() - 1);
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            List<Integer> statusList = deviceAuditService.getAuditStatus(da_ids);
            if (statusList.contains(Constants.AUDIT_STATUS_YES) || statusList.contains(Constants.AUDIT_STATUS_NO)) {
                return JsonResult.getError(Constants.IS_AUDIT);
            }
            Integer status;
            if (deviceBean.getMark().equals("yes")) {
                status = Constants.AUDIT_STATUS_YES;
            } else {
                status = Constants.AUDIT_STATUS_NO;
            }
            deviceAuditService.updateStatusByIds(status, da_ids);
            //设备审核通过后新增设备
            if (Objects.equals(status, Constants.AUDIT_STATUS_YES)) {
                deviceService.initDevice(da_ids, status, deviceBean);
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
