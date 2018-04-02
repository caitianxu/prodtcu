package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.DelRoleBean;
import com.cjsz.tech.system.domain.Role;
import com.cjsz.tech.system.domain.SysRoleResRel;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.domain.SysUserRoleRel;
import com.cjsz.tech.system.service.RoleResRelService;
import com.cjsz.tech.system.service.RoleService;
import com.cjsz.tech.system.service.UserRoleRelService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
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
@RequestMapping("/admin/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleRelService userRoleRelService;

    @Autowired
    private RoleResRelService roleResRelService;

    @Autowired
    private UserService userService;

    /**
     * 角色列表
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
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            PageList result = (PageList) roleService.pageQuery(sort, pageCondition, sysUser.getOrg_id(),sysUser.getUser_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增角色
     *
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public Object saveRole(HttpServletRequest request, @RequestBody Role role) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            if (role.getRole_id() == null) {
                //同机构下的角色名不能重复
                Role ro = roleService.getRoleByNameAndOrgId(role.getRole_name(), sysUser.getOrg_id());
                if (ro != null) {
                    return JsonResult.getError(Constants.ROLE_NAME_EXIST);
                }
                role.setOrg_id(sysUser.getOrg_id());
                role.setRole_type(Constants.NO_ADMIN_ROLE);
                roleService.saveRole(role);
                Long roleId = roleService.getMaxRoleId();
                Role data = roleService.selectById(roleId);
                JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_ADD);
                jsonResult.setData(data);
                return jsonResult;
            } else {
                Role exrole = roleService.selectById(role.getRole_id());
                //检查角色是否存在
                if (exrole == null) {
                    return JsonResult.getError(Constants.ROLE_NOT_EXIST);
                } else {
                    if (!role.getRole_name().equals(exrole.getRole_name())) {
                        //检查角色名称在当前组织是否存在
                        Role ro = roleService.getRoleByNameAndOrgId(role.getRole_name(), sysUser.getOrg_id());
                        if (ro != null) {
                            return JsonResult.getError(Constants.ROLE_NAME_EXIST);
                        }
                    }
                    exrole.setRole_name(role.getRole_name());
                    exrole.setRemark(role.getRemark());
                    roleService.updateRole(exrole);
                    Role data = roleService.selectById(role.getRole_id());
                    JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_UPDATE);
                    jsonResult.setData(data);
                    return jsonResult;
                }
            }
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object delRole(HttpServletRequest request, @RequestBody DelRoleBean delRole) {
        String ids = Arrays.toString(delRole.getIds());
        String role_ids = ids.substring(1, ids.length() - 1);
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            List<Role> list = roleService.getRoleList(null, null, role_ids, Constants.IS_ADMIN_ROLE ,0L);
            if (list.size() > 0) {
                return JsonResult.getError(Constants.ROLE_NOT_DELETE);
            }
            //删除的角色下是否有未更新为删除状态的用户
            List<SysUserRoleRel> userRoleRels = userRoleRelService.getRelListByRole(role_ids);
            if (userRoleRels.size() > 0) {
                return JsonResult.getError(Constants.USER_IN_ROLE);
            }
            List<SysRoleResRel> roleResRels = roleResRelService.getRelListByRole(role_ids);
            if (StringUtil.isEmpty(delRole.getMark())) {
                if (roleResRels.size() > 0) {
                    return JsonResult.getError(Constants.DELETE_ROLE_RES_REL);
                } else {
                    roleService.updateIsDelete(role_ids);
                }
            } else {
                roleService.updateIsDelete(role_ids);
                if (userRoleRels.size() > 0) {
                    userRoleRelService.updateIsDeleteByRole(role_ids);
                }
                if (roleResRels.size() > 0) {
                    roleResRelService.deteleByRoles(role_ids);
                }
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
