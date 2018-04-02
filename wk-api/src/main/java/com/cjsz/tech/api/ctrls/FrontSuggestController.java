package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.APIConstants;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.domain.DeviceFeedback;
import com.cjsz.tech.dev.service.DeviceFeedbackService;
import com.cjsz.tech.dev.service.DeviceService;
import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.util.ValidCodeUtil;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * Created by shiaihua on 17/1/5.
 */
@Controller
public class FrontSuggestController {


    @Autowired
    DeviceService deviceService;

    @Autowired
    DeviceFeedbackService deviceFeedbackService;

    @Autowired
    UserService userService;

    @RequestMapping("web/fankui")
    public ModelAndView fankui(HttpServletRequest request){
        ModelAndView mv=new ModelAndView();
        String devsn = request.getParameter("devsn");
        String uid = request.getParameter("id");
        try {
            request.setAttribute("devsn",devsn);
            request.setAttribute("uid",uid);
        }catch(Exception e) {
            e.printStackTrace();
        }

        mv.setViewName("web/fankui");
        return mv;
    }



    /**
     * 提交建议
     * @return
     */
    @RequestMapping(value = "/api/suggest/commit", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public  Object suggestcommit(HttpServletRequest request) {

        String token = request.getParameter("token");
        String user_id = request.getParameter("uid");
        SysUser sysUser = null;
        if(StringUtil.isNotEmpty(token)){
            sysUser = userService.findByToken(token);
        }else if(StringUtil.isNotEmpty(user_id)){
            sysUser = userService.selectById(Long.valueOf(user_id));
        }else{
            return JsonResult.getError(APIConstants.TOKEN_NULL);
        }
        if(sysUser==null) {
            return JsonResult.getError("无此用户");
        }

        String ucode = request.getParameter("code");
        String content = request.getParameter("content");
        String validcode = (String)request.getSession().getAttribute(ValidCodeUtil.SESSION_VALID_CODE);
        if(StringUtils.isEmpty(validcode) || StringUtils.isEmpty(ucode) || !ucode.equals(validcode) ) {
            JsonResult result = JsonResult.getError("验证码不正确!");
            result.setData("验证码不正确");
            return result;
        }
        request.getSession().removeAttribute(ValidCodeUtil.SESSION_VALID_CODE);
        if(StringUtils.isEmpty(content)) {
            JsonResult result = JsonResult.getError("提交失败!");
            result.setData("没有建议内容");
            return result;
        }
        if(content.contains("#") || content.contains("^")) {
            JsonResult result = JsonResult.getError("提交失败!");
            result.setData("建议内容不允许包含#、^等特殊符号");
            return result;
        }
        try {
            DeviceFeedback feedback = new DeviceFeedback();
            feedback.setOrg_id(sysUser.getOrg_id());
            feedback.setUser_id(sysUser.getUser_id());
            feedback.setUser_name(sysUser.getUser_real_name());
            feedback.setUser_type(1);//1:管理员用户;2:会员用户
            feedback.setOpinion(content);

            feedback.setDevice_type_code("max");
        	deviceFeedbackService.saveDeviceFeedback(feedback);
            JsonResult result = JsonResult.getSuccess("操作成功!");
            result.setData("建议提交成功");
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            JsonResult result = JsonResult.getSuccess("提交失败!");
            result.setData("请联系客服");
            return result;
        }

    }

}
