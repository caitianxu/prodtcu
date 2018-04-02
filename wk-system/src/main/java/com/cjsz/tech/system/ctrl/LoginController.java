package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.system.utils.RequestContextUtil;
import com.cjsz.tech.utils.CookieUtil;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.utils.PasswordUtil;
import com.github.pagehelper.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * Created by Administrator on 2016/10/25.
 */
@Controller
public class LoginController {


    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    /**
     * 登录
     *
     * @param sysUser
     * @return
     */
    @RequestMapping(value = "/admin/login", method = {RequestMethod.POST})
    @ResponseBody
    public Object login(HttpServletRequest request, @RequestBody SysUser sysUser) {
        SysUser user = null;
        if (StringUtil.isEmpty(sysUser.getUser_name()) || StringUtil.isEmpty(sysUser.getUser_pwd())) {
            //用户名或密码为空，则token登陆
            // 检查账号是否注册(token)
            user = userService.findByToken(sysUser.getToken());
            if (user == null) {
                return JsonResult.getError(Constants.USER_NOT_EXIST);
            }
        } else {
            //用户名或密码不为空，用户名或密码登陆
            // 检查账号是否注册(用户名)
            user = userService.findByUserName(sysUser.getUser_name());
            if (user == null) {
                return JsonResult.getError(Constants.USER_NOT_EXIST);
            } else {
                String md5Pwd = PasswordUtil.entryptPassword(sysUser.getUser_pwd());
                // 2.校验密码
                if (!md5Pwd.equals(user.getUser_pwd())) {
                    return JsonResult.getError(Constants.PWD_ERROR);
                }
            }
        }
        if (Objects.equals(user.getEnabled(), Constants.DISABLE)) {
            return JsonResult.getError(Constants.USER_DISABLE);
        }
        //用户组织
        Organization org = organizationService.selectById(user.getOrg_id());
        if (org.getEnabled() != 1) {
            return JsonResult.getError("机构未启用！");
        }
        if (StringUtil.isEmpty(sysUser.getUser_name()) || StringUtil.isEmpty(sysUser.getUser_pwd())) {
            //token登陆，token不变
            sysUser = userService.getUserById(user.getUser_id());

            //添加用户根组织
            sysUser.setRoot_org_name(org.getOrg_name());
            sysUser.setUser_pwd("");
            RequestContextUtil.setUser(request, sysUser);
            request.getSession().setAttribute("token", sysUser.getToken());
        } else {
            //生成token并存储(每次登录token重新生成)
            String rand = user.getUser_name() + PasswordUtil.getRandomNum();
            String rand_md5 = PasswordUtil.entryptPassword(rand);
            user.setToken(rand_md5);
            sysUser = userService.getUserById(user.getUser_id());
            sysUser.setToken(rand_md5);

            //添加用户根组织
            sysUser.setRoot_org_name(org.getOrg_name());
            sysUser.setUser_pwd("");
            RequestContextUtil.setUser(request, sysUser);
            request.getSession().setAttribute("token", rand_md5);

            userService.updateUser(user);
        }
        JsonResult jsonResult = JsonResult.getSuccess(Constants.LOGIN_SUCCESS);
        jsonResult.setData(sysUser);
        return jsonResult;
    }

    /**
     * 注销
     *
     * @param session
     * @return
     */
    @RequestMapping("/admin/logout")
    public String logout(HttpSession session, HttpServletResponse resp, HttpServletRequest req) {
        if (null != session.getAttribute("user")) {
            session.removeAttribute("user");
        }
        CookieUtil.removeCookie(req, resp, "user_name");
        CookieUtil.removeCookie(req, resp, "user_pwd");
        return "/admin/index";
    }
}
