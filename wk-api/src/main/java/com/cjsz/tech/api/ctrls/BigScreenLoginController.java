package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.APIConstants;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.domain.DeviceAudit;
import com.cjsz.tech.dev.service.DeviceAuditService;
import com.cjsz.tech.dev.service.DeviceService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.utils.PasswordUtil;
import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Hashtable;

/**
 * 大屏登录注册
 * Created by shiaihua on 16/12/23.
 */
@Controller
public class BigScreenLoginController {

    @Autowired
    DeviceService deviceService;

    @Autowired
    DeviceAuditService deviceAuditService;

    @Autowired
    UserService userService;

    @Autowired
    OrganizationService organizationService;

    /**
     * 大屏登录
     *
     * @return
     */
    @RequestMapping(value = "/api/bigscreen/login", method = { RequestMethod.POST,RequestMethod.GET })
    @ResponseBody
    public Object login(HttpServletRequest request) {
        String username = request.getParameter("username");
        String pwd = request.getParameter("userpwd");
        if(StringUtil.isEmpty(username)){
            return JsonResult.getError(Constants.USER_NAME_NULL);
        }
        if(StringUtil.isEmpty(pwd)){
            return JsonResult.getError(Constants.USER_PWD_NULL);
        }
        SysUser user = userService.selectLoginUser(username, pwd);
        if(user==null) {
            return JsonResult.getError(APIConstants.NO_USER);
        }
        if(user.getEnabled()!=1 || user.getIs_delete()==1) {
            return  JsonResult.getError(APIConstants.USER_DISABLED);
        }

        String deviceCode = request.getParameter("devcode");
        if(StringUtils.isEmpty(deviceCode)) {
            return JsonResult.getError(APIConstants.DEVICE_INVALID);
        }
        Long orgid = user.getOrg_id();
        if(orgid==null || orgid.intValue()<=0) {
            return JsonResult.getError(APIConstants.ORG_INVALID);
        }
        Organization org = organizationService.selectById(orgid);
        if(org==null) {
            return JsonResult.getError(APIConstants.NO_ORG);
        }
        Long rootorgid = org.getOrg_id();
        Device deviceObj = deviceService.getDeviceByCode(deviceCode);
        if(deviceObj==null) {
            //设备不存在
            //设备是否待审核
            DeviceAudit waitAuditObj = deviceAuditService.getByParam(deviceCode,rootorgid,user.getUser_id());
            if(waitAuditObj!=null) {
                return JsonResult.getError(APIConstants.DEVICE_INAUDIT);
            }
            //没有设备就注册设备，待审核
            waitAuditObj = new DeviceAudit();
            waitAuditObj.setDevice_code(deviceCode);
            waitAuditObj.setAudit_status(3);
            waitAuditObj.setUser_id(user.getUser_id());
            waitAuditObj.setOrg_id(rootorgid);
            waitAuditObj.setApply_time(new Date());
            deviceAuditService.saveDeviceAudit(waitAuditObj);
            return JsonResult.getSuccess(APIConstants.DEVICE_SENDREG);
        }else {
            //设备存在
            if(deviceObj.getEnabled()!=1) {
                return JsonResult.getError(APIConstants.DEVICE_DISABLED);
            }
            //换用户或机构
            if(rootorgid.intValue()!= deviceObj.getOrg_id().intValue() || deviceObj.getUser_id().intValue()!=user.getUser_id().intValue()){
                //设备是否待审核
                DeviceAudit waitAuditObj = deviceAuditService.getByParam(deviceCode,rootorgid,user.getUser_id());
                if(waitAuditObj!=null) {
                    return JsonResult.getError(APIConstants.DEVICE_INAUDIT);
                }
                //没有设备就注册设备，待审核
                waitAuditObj = new DeviceAudit();
                waitAuditObj.setDevice_code(deviceCode);
                waitAuditObj.setAudit_status(3);
                waitAuditObj.setUser_id(user.getUser_id());
                waitAuditObj.setOrg_id(rootorgid);
                waitAuditObj.setApply_time(new Date());
                deviceAuditService.saveDeviceAudit(waitAuditObj);
                return JsonResult.getError(APIConstants.DEVICE_SENDREG);
            }
            //生成token并存储(每次登录token重新生成)
            String rand = user.getUser_name() + PasswordUtil.getRandomNum();
            String rand_md5 = PasswordUtil.entryptPassword(rand);
            Hashtable data = new Hashtable();
            data.put("token",rand_md5);
            user.setUser_pwd("");
            data.put("user",user);
            data.put("device",deviceObj);
            JsonResult success = JsonResult.getSuccess(APIConstants.DEVICE_LOGINOK);
            success.setData(data);
            return success;
        }
    }


    /**
     * 大屏退出密码验证
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/bigscreen/exit", method = { RequestMethod.POST,RequestMethod.GET })
    @ResponseBody
    public Object exit(HttpServletRequest request){
        try{
            String user_id = request.getParameter("user_id");
            String pwd = request.getParameter("userpwd");
            if (StringUtils.isEmpty(user_id)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            if (StringUtils.isEmpty(pwd)){
                return JsonResult.getError("请输入密码!");
            }
            SysUser user = userService.getUserById(Long.parseLong(user_id));
            if (user == null){
                return JsonResult.getError("账号不存在!");
            }
            String userPwd = PasswordUtil.entryptPassword(pwd);
            if (!user.getUser_pwd().equals(userPwd)){
                return JsonResult.getError("密码错误！");
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            return jsonResult;
        }catch (Exception e){
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }



}
