package com.cjsz.tech.dev.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.DelVersionBean;
import com.cjsz.tech.dev.beans.DevicePageBean;
import com.cjsz.tech.dev.domain.DeviceVersionRel;
import com.cjsz.tech.dev.service.DeviceVersionRelService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 大屏版本
 * Created by Li Yi on 2016/12/21.
 */
@Controller
@RequestMapping("/admin/deviceVersion")
public class DeviceVersionController {

    @Autowired
    private DeviceVersionRelService deviceVersionRelService;

    @Autowired
    private UserService userService;

    /**
     * 设备版本列表
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
            Sort sort = new Sort(Sort.Direction.DESC, "org_id").and(new Sort(Sort.Direction.DESC, "create_time"));
            PageList result = (PageList) deviceVersionRelService.pageQuery(sort, pageCondition);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 保存设备版本
     *
     * @param request
     * @param devicePage
     * @return
     */
    @RequestMapping(value = "/saveDeviceVersion", method = RequestMethod.POST)
    @ResponseBody
    public Object saveDeviceVersion(HttpServletRequest request, @RequestBody DevicePageBean devicePage) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            deviceVersionRelService.saveDeviceVersion(devicePage);
            List<DeviceVersionRel> deviceVersionRels = new ArrayList<>();
            JsonResult result = JsonResult.getSuccess("");
            result.setMessage(Constants.ACTION_ADD);
            result.setData(deviceVersionRels);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除设备版本信息
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/deleteDeviceVersion", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteDeviceVersion(HttpServletRequest request, @RequestBody DelVersionBean bean) {
        String dev_ids = StringUtils.join(bean.getIds(), ",");
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            deviceVersionRelService.deleteRels(dev_ids);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
