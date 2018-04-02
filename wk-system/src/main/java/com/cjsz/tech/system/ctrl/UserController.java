package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.DelUserBean;
import com.cjsz.tech.system.beans.PassWordBean;
import com.cjsz.tech.system.conditions.UserCondition;
import com.cjsz.tech.system.domain.Role;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.domain.SysUserRoleRel;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.system.service.RoleService;
import com.cjsz.tech.system.service.UserRoleRelService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.utils.PasswordUtil;
import com.github.pagehelper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
@Controller
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleRelService userRoleRelService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private RoleService roleService;

    /**
     * 用户列表
     *
     * @return
     */
    @RequestMapping("/listAll")
    @ResponseBody
    public Object listAll(HttpServletRequest request, @RequestBody UserCondition userCondition) {
        SysUser sUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "user_id");
            if (!sUser.getOrg_id().toString().equals("1")) {
                userCondition.setOrg_id(sUser.getOrg_id());
            }
            Object object = userService.pageQuery(sort, userCondition);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(object);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }

    }

    /**
     * 用户选择角色
     *
     * @return
     */
    @RequestMapping("/getRoleList")
    @ResponseBody
    public Object getRoleList(HttpServletRequest request, @RequestBody PageConditionBean pageCondition) {
        SysUser sUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            List<Role> list = roleService.getRoleList(pageCondition.getSearchText(), sUser.getOrg_id(), null, null , sUser.getUser_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(list);
            return jsonResult;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增用户
     *
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public Object saveUser(HttpServletRequest request, @RequestBody SysUser sysUser) {
        SysUser sUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        if (StringUtil.isEmpty(sysUser.getUser_name())) {
            return JsonResult.getError(Constants.USER_NAME_NULL);
        }
        if (StringUtil.isEmpty(sysUser.getUser_pwd())) {
            return JsonResult.getError(Constants.USER_PWD_NULL);
        }
        if (StringUtil.isEmpty(sysUser.getRole_id())) {
            return JsonResult.getError(Constants.ROLE_NOT_NULL);
        }
        if (sysUser.getDept_id() == null || sysUser.getDept_id() == 0) {
            return JsonResult.getError(Constants.ORG_PID_NULL);
        }
        try {
            if (sysUser.getUser_id() == null) {
                // 检查账号是否注册
                SysUser userInfo = userService.findByUserName(sysUser.getUser_name());
                if (userInfo != null) {
                    return JsonResult.getError(Constants.USER_NAME_EXIST);
                }
                sysUser.setOrg_id(sUser.getOrg_id());
                SysUser user = userService.saveUserAndRel(sysUser);
                user.setUser_pwd("");
                JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_ADD);
                jsonResult.setData(user);
                return jsonResult;
            } else {
                if (!sysUser.getOrg_id().equals(sUser.getOrg_id())) {
                    return JsonResult.getError("不能修改其他机构用户！");
                }
                SysUser user = userService.selectById(sysUser.getUser_id());
                if (user == null) {
                    return JsonResult.getError(Constants.USER_NOT_EXIST);
                }
                SysUser data = userService.updateUserAndRel(sysUser);
                data.setUser_pwd("");
                JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_UPDATE);
                jsonResult.setData(data);
                return jsonResult;
            }
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Object delUser(HttpServletRequest request, @RequestBody DelUserBean delUser) {
        String ids = Arrays.toString(delUser.getIds());
        String user_ids = ids.substring(1, ids.length() - 1);
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            //检查是否包含其他机构用户
            List<SysUser> other_users = userService.getOtherUserByIds(user_ids, sysUser.getOrg_id());
            if (other_users.size() > 0) {
                return JsonResult.getError("其他机构用户不能删除！");
            }
            //检查是否包含系统管理员用户
            List<SysUser> admin_users = userService.getAdminUserByIds(user_ids);
            if (admin_users.size() > 0) {
                return JsonResult.getError(Constants.USER_NOT_DELETE);
            }
            userService.updateIsDelete(user_ids);
            List<SysUserRoleRel> list = userRoleRelService.getRelListByUser(user_ids);
            if (list.size() > 0) {
                userRoleRelService.updateIsDeleteByUsers(user_ids);
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }

    }

    @RequestMapping("/updatePwd")
    @ResponseBody
    public Object updatePwd(HttpServletRequest request, @RequestBody PassWordBean pwd) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            //检查旧密码输入是否正确
            String oldPwd = PasswordUtil.entryptPassword(pwd.getOldpwd());
            SysUser user = userService.selectById(sysUser.getUser_id());
            if (!oldPwd.equals(user.getUser_pwd())) {
                return JsonResult.getError(Constants.OLDPWD_ERROR);
            }
            //更新密码
            String newPwd = PasswordUtil.entryptPassword(pwd.getNewpwd());
            user.setUser_pwd(newPwd);
            userService.updateUser(user);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            //返回值里面去掉密码
            sysUser.setUser_pwd("");
            result.setData(user);
            return result;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }

    }

}
