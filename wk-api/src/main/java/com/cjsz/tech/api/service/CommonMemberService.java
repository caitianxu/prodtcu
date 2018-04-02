package com.cjsz.tech.api.service;

import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.utils.PasswordUtil;
import com.github.pagehelper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 会员接口集合
 * Created by caitianxu on 2017/04/18.
 */
@Service
public class CommonMemberService {

    @Autowired
    MemberService memberService;
    @Autowired
    CommonValidCodeService commonValidCodeService;

    /**
     * 用户登录
     *
     * @param login_name
     * @param login_pwd
     * @return
     */
    public JsonResult memberLogin(String login_name, String login_pwd, String token_type) {
        try {
            if (StringUtil.isEmpty(login_name)) {
                return JsonResult.getObjError(Constants.USER_NAME_NULL);
            }
            if (StringUtil.isEmpty(login_pwd)) {
                return JsonResult.getObjError(Constants.USER_PWD_NULL);
            }
            // 1.检查账号是否注册
            login_name = login_name.toLowerCase();
            Member member = memberService.findByLoginName(login_name, token_type);
            if (member == null) {
                return JsonResult.getObjError(Constants.USER_NOT_EXIST);
            }
            String md5Pwd = PasswordUtil.entryptPassword(login_pwd);
            // 2.校验密码
            if (!md5Pwd.equals(member.getMember_pwd())) {
                return JsonResult.getObjError(Constants.PWD_ERROR);
            }
            // 检查账号是否停用
            if (Objects.equals(member.getEnabled(), Constants.DISABLE)) {
                return JsonResult.getObjError(Constants.USER_DISABLE);
            }
            // 3.生成token并存储(每次登录token重新生成)
            String rand = login_name + PasswordUtil.getRandomNum();
            String new_token = PasswordUtil.entryptPassword(rand);
            memberService.updateMemberToken(member.getMember_id(), new_token, token_type);
            member = memberService.getMemberInfoByMemberId(member.getMember_id(), token_type);
            // 4.返回会员信息
            Member memberInfo = memberService.getMemberInfoByMemberId(member.getMember_id(), token_type);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOGIN_SUCCESS);
            memberInfo.setMember_pwd("");
            jsonResult.setData(memberInfo);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 会员注册
     *
     * @param reg_name
     * @param password
     * @param nick_name
     * @param org_id
     * @param code
     * @return
     */
    public JsonResult memberReg(String reg_name, String password, String nick_name, Object org_id, String code, String token_type) {
        try {
            if (StringUtil.isEmpty(reg_name)) {
                return JsonResult.getObjError(Constants.USER_NAME_NULL);
            }
            if (StringUtil.isEmpty(password)) {
                return JsonResult.getObjError(Constants.USER_PWD_NULL);
            }
            // 检查是否注册
            reg_name = reg_name.toLowerCase();
            nick_name = nick_name.toLowerCase();
            Member member = memberService.findByLoginName(reg_name, token_type);
            if (member != null) {
                return JsonResult.getObjError(Constants.USER_NAME_EXIST);
            }
            //判断验证码是否正确
            JsonResult jsonCode = commonValidCodeService.matchVerificationCodeService("register", code, reg_name);
            if (jsonCode.getCode() != 0) {
                return jsonCode;
            }

            //注册保存用户
            String md5Pwd = PasswordUtil.entryptPassword(password);
            Member reg_member = new Member();
            if (org_id != null) {
                reg_member.setOrg_id(Long.valueOf(org_id.toString()));
            }
            reg_member.setMember_pwd(md5Pwd);
            reg_member.setNick_name(nick_name);
            if (reg_name.contains("@")) {
                reg_member.setEmail(reg_name);
            } else {
                reg_member.setPhone(reg_name);
            }
            reg_member.setIs_sys_icon(1);//是否使用系统头像(1:是;2:否)
            memberService.saveMember(reg_member, token_type);

            //注册成功
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOGIN_SUCCESS);
            jsonResult.setData(reg_member);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 找回密码
     *
     * @param login_name
     * @param code
     * @param new_password
     * @return
     */
    public JsonResult retrievePwd(String login_name, String new_password, String code, String token_type) {
        try {
            if (StringUtil.isEmpty(login_name)) {
                return JsonResult.getObjError("请输入你的手机号或者邮箱！");
            }
            if (StringUtil.isEmpty(code)) {
                return JsonResult.getObjError("请输入验证码！");
            }
            if (StringUtil.isEmpty(new_password)) {
                return JsonResult.getObjError("请输入新密码！");
            }
            // 检查是否注册
            login_name = login_name.toLowerCase();
            Member member = memberService.findByLoginName(login_name, token_type);
            if (member == null) {
                return JsonResult.getError("你的手机或邮箱输入错误！");
            }
            //判断验证码是否正确
            JsonResult jsonCode = commonValidCodeService.matchVerificationCodeService("forgetPwd", code, login_name);
            if (jsonCode.getCode() != 0) {
                return jsonCode;
            }
            //注册保存用户
            String md5Pwd = PasswordUtil.entryptPassword(new_password);
            member.setMember_pwd(md5Pwd);
            memberService.updateMember(member);
            return JsonResult.getSuccess("密码修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改密码
     *
     * @param member_token
     * @param old_pwd
     * @param new_pwd
     * @return
     */
    public JsonResult updatePwd(String member_token, String old_pwd, String new_pwd, String token_type) {
        try {
            if (StringUtil.isEmpty(member_token)) {
                return JsonResult.getObjError(Constants.TOKEN_FAILED);
            }
            if (StringUtil.isEmpty(old_pwd)) {
                return JsonResult.getObjError("请输入旧密码！");
            }
            if (StringUtil.isEmpty(new_pwd)) {
                return JsonResult.getObjError("请输入新密码！");
            }
            // 检查是否注册
            Member member = memberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            }
            String old_md5 = PasswordUtil.entryptPassword(old_pwd);
            if (old_md5.equals(member.getMember_pwd())) {
                String new_md5 = PasswordUtil.entryptPassword(new_pwd);
                memberService.updatePwd(member.getMember_id(), new_md5);
            } else {
                return JsonResult.getError("原密码错误！");
            }
            return JsonResult.getSuccess(Constants.ACTION_UPDATE);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 绑定邮箱
     *
     * @param member_token
     * @param email
     * @param code
     * @return
     */
    public JsonResult bindEmail(String member_token, String email, String code, String token_type) {
        try {
            if (StringUtil.isEmpty(member_token)) {
                return JsonResult.getObjError(Constants.TOKEN_FAILED);
            }
            if (StringUtil.isEmpty(email)) {
                return JsonResult.getObjError("请输入邮箱！");
            }
            if (StringUtil.isEmpty(code)) {
                return JsonResult.getObjError("请输入邮箱验证码！");
            }
            // 检查是否注册
            Member member = memberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getObjError(Constants.TOKEN_FAILED);
            }
            // 检查是否已绑定
            email = email.toLowerCase();
            Member p_member = memberService.findByLoginName(email, token_type);
            if (p_member != null) {
                return JsonResult.getError("该邮箱已被绑定！");
            }
            //判断验证码是否正确
            JsonResult jsonCode = commonValidCodeService.matchVerificationCodeService("changeEmail", code, email);
            if (jsonCode.getCode() != 0) {
                return jsonCode;
            }

            //修改邮箱
            memberService.updateEmail(member.getMember_id(), email);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            member.setEmail(email);
            result.setData(member);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 绑定手机
     *
     * @param member_token
     * @param phone
     * @param code
     * @return
     */
    public JsonResult updatePhone(String member_token, String phone, String code, String token_type) {
        try {
            if (StringUtil.isEmpty(member_token)) {
                return JsonResult.getObjError(Constants.TOKEN_FAILED);
            }
            if (StringUtil.isEmpty(phone)) {
                return JsonResult.getObjError("请输入手机号码！");
            }
            if (StringUtil.isEmpty(code)) {
                return JsonResult.getObjError("请输入手机验证码！");
            }
            // 检查是否注册
            Member member = memberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getObjError(Constants.TOKEN_FAILED);
            }
            // 检查是否已绑定
            Member p_member = memberService.findByLoginName(phone, token_type);
            if (p_member != null) {
                return JsonResult.getObjError("该手机已被使用！");
            }
            //判断验证码是否正确
            JsonResult jsonCode = commonValidCodeService.matchVerificationCodeService("changePhone", code, phone);
            if (jsonCode.getCode() != 0) {
                return jsonCode;
            }

            //修改邮箱
            memberService.updatePhone(member.getMember_id(), phone);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            member.setPhone(phone);
            result.setData(member);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 是否使用系统头像
     *
     * @param member_token
     * @param is_sys_icon
     * @return
     */
    public JsonResult updateIsSysIcon(String member_token, String is_sys_icon, String token_type) {
        try {
            if (StringUtil.isEmpty(member_token)) {
                return JsonResult.getObjError(Constants.TOKEN_FAILED);
            }
            if (StringUtil.isEmpty(is_sys_icon)) {
                return JsonResult.getObjError("参数缺失: is_sys_icon ！");
            }
            // 检查是否注册
            Member member = memberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getObjError(Constants.TOKEN_FAILED);
            }
            int is_sys_ico = 1;
            if ("2".equals(is_sys_icon)) {
                if (StringUtil.isEmpty(member.getIcon())) {
                    return JsonResult.getObjError("会员未设置自定义头像！");
                } else {
                    is_sys_ico = 2;
                }
            }
            //修改是否使用系统头像
            memberService.updateIsSysIcon(member.getMember_id(), is_sys_icon);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            member.setIs_sys_icon(is_sys_ico);
            result.setData(member);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改头像
     *
     * @param member_token
     * @param icon
     * @return
     */
    public JsonResult updateIcon(String member_token, String icon, String token_type) {
        try {
            if (StringUtil.isEmpty(member_token)) {
                return JsonResult.getObjError(Constants.TOKEN_FAILED);
            }
            if (StringUtil.isEmpty(icon)) {
                return JsonResult.getObjError("请上传头像！");
            }
            // 检查是否注册
            Member member = memberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getObjError(Constants.TOKEN_FAILED);
            }
            //修改-是否使用系统头像
            memberService.updateIcon(member.getMember_id(), icon, token_type);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            member.setIcon(icon);
            result.setData(member);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改昵称
     *
     * @param member_token
     * @param nick_name
     * @return
     */
    public JsonResult updateNickName(String member_token, String nick_name, String token_type) {
        try {
            if (StringUtil.isEmpty(member_token)) {
                return JsonResult.getObjError(Constants.TOKEN_FAILED);
            }
            if (StringUtil.isEmpty(nick_name)) {
                return JsonResult.getObjError("请填写你的账号昵称！");
            }
            // 检查是否注册
            Member member = memberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getObjError(Constants.TOKEN_FAILED);
            }
            //修改是否使用系统头像
            memberService.updateNickName(member.getMember_id(), nick_name);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            member.setNick_name(nick_name);
            result.setData(member);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
