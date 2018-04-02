package com.cjsz.tech.dev.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.MessageBean;
import com.cjsz.tech.dev.domain.DeviceFeedback;
import com.cjsz.tech.dev.service.DeviceFeedbackService;
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

/**
 * 用户反馈
 * Created by Administrator on 2016/10/24.
 */
@Controller
@RequestMapping("/admin/deviceFeedback")
public class DeviceFeedbackController {

    @Autowired
    DeviceFeedbackService deviceFeedbackService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    UserService userService;


    /**
     * 反馈列表（大屏）
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
            Order status = new Order(Direction.ASC, "reply_status");
            Order send_time = new Order(Direction.DESC, "send_time");
            Sort sort = new Sort(status, send_time);
            PageList result = (PageList) deviceFeedbackService.pageQuery(sort, pageCondition, sysUser.getOrg_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 反馈列表（pc、移动端）
     *
     * @return
     */
    @RequestMapping("/messageList")
    @ResponseBody
    public Object messageList(HttpServletRequest request, @RequestBody MessageBean pageCondition) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Order status = new Order(Direction.ASC, "reply_status");
            Order send_time = new Order(Direction.DESC, "send_time");
            Sort sort = new Sort(status, send_time);
            PageList result = (PageList) deviceFeedbackService.getMemberMessageList(sort, pageCondition, sysUser.getOrg_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 回复
     *
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public Object updateFeedback(HttpServletRequest request, @RequestBody DeviceFeedback device) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            return deviceFeedbackService.updateFeedback(device,sysUser.getDept_id());
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
