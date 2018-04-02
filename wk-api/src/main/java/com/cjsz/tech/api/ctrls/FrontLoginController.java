package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.utils.PasswordUtil;
import com.github.pagehelper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;

/**
 * APP登录
 * Created by shiaihua on 16/12/21.
 */
@Controller
public class FrontLoginController {

    @Autowired
    MemberService memberService;


//    /**
//     * 手机账号密码登录
//     *
//     * @return
//     */
//    @RequestMapping(value = "/api/user/login", method = { RequestMethod.POST })
//    @ResponseBody
//    public Object user_login(HttpServletRequest request) {
//        String username = request.getParameter("username");
//        String pwd = request.getParameter("userpwd");
//        if(StringUtil.isEmpty(username)){
//            return JsonResult.getError(Constants.USER_NAME_NULL);
//        }
//        if(StringUtil.isEmpty(pwd)){
//            return JsonResult.getError(Constants.USER_PWD_NULL);
//        }
//        Member member = memberService.findByMemberName(username);
//        if(member==null) {
//            return JsonResult.getError(Constants.MEMBER_NOTREG);
//        }
//        //生成token并存储(每次登录token重新生成)
//        String rand =  PasswordUtil.getRandomNum();
//        String token = PasswordUtil.entryptPassword(member.getMember_id()+"$"+rand);
//        request.getSession().setAttribute("token",token);
//
//        memberService.updateMemberToken(member.getMember_id(),token);
//
//        Hashtable result = new Hashtable();
//        result.put("token",token);
//        result.put("info",member);
//
//        JsonResult jsonResult = JsonResult.getSuccess(Constants.LOGIN_SUCCESS);
//
//        jsonResult.setData(result);
//        return jsonResult;
//    }
//
//
//    /**
//     * 手机注册
//     *
//     * @return
//     */
//    @RequestMapping(value = "/api/user/reg", method = { RequestMethod.POST })
//    @ResponseBody
//    public Object user_reg(HttpServletRequest request) {
//        String username = request.getParameter("username");
//        String pwd = request.getParameter("userpwd");
//        String phone = request.getParameter("phone");
//        String email = request.getParameter("email");
//        String org_id = request.getParameter("org_id");
//        if(StringUtil.isEmpty(username)){
//            return JsonResult.getError(Constants.USER_NAME_NULL);
//        }
//        if(StringUtil.isEmpty(pwd)){
//            return JsonResult.getError(Constants.USER_PWD_NULL);
//        }
//        if(StringUtil.isEmpty(phone)){
//            return JsonResult.getError(Constants.USER_PHONE_NULL);
//        }else{
//            Member member = memberService.selectByPhone(phone);
//            if(member != null){
//                return JsonResult.getError(Constants.USER_PHONE_REPEAT);
//            }
//        }
//        if(StringUtil.isNotEmpty(email)){
//            Member member = memberService.selectByEmail(email);
//            if(member != null){
//                return JsonResult.getError(Constants.USER_EMAIL_REPEAT);
//            }
//        }
//        if(StringUtil.isEmpty(org_id)){
//            org_id = "-1";
//        }
//        try {
//            Member member = new Member();
//            member.setMember_name(username);
//            member.setMember_pwd(PasswordUtil.entryptPassword(pwd));
//            member.setPhone(phone);
//            member.setEmail(email);
//            member.setOrg_id(Long.valueOf(org_id));
//            //生成token并存储(每次登录token重新生成)
//            String rand = PasswordUtil.getRandomNum();
//            String token = PasswordUtil.entryptPassword(member.getMember_id() + "$" + rand);
//            member.setToken(token);
//            memberService.saveMember(member);
//
//            request.getSession().setAttribute("token", token);
//
//            JsonResult jsonResult = JsonResult.getSuccess(Constants.REG_SUCCESS);
//            return jsonResult;
//        }catch(Exception e) {
//            e.printStackTrace();
//            return JsonResult.getError(Constants.REG_FAIL);
//        }
//    }


    /**
     * 手机第三方注册绑定、登录
     *
     * @return
     */
    @RequestMapping(value = "/api/user/user_bind", method = {RequestMethod.POST})
    @ResponseBody
    public Object user_bind(HttpServletRequest request) {
        String btype = request.getParameter("btype");
        String openid = request.getParameter("openid");
        String nick_name = request.getParameter("name");
        String icon = request.getParameter("iconurl");
        String org_id = request.getParameter("org_id");
        if (StringUtil.isEmpty(org_id)) {
            org_id = "-1";
        }
        if (StringUtil.isEmpty(btype)) {
            return JsonResult.getError(Constants.LOGIN_BIND_NULLTYPE);
        }
        if (StringUtil.isEmpty(openid)) {
            return JsonResult.getError(Constants.LOGIN_BIND_NULLOPENID);
        }
        try {
            Member member = memberService.findByOpenId(btype, openid, "2");
            if (member != null) {
                JsonResult result = JsonResult.getSuccess(Constants.LOGIN_BIND_AREADYBIND);
                Hashtable<String, Object> data = new Hashtable<String, Object>();
                String rand = PasswordUtil.getRandomNum();
                String token = PasswordUtil.entryptPassword(openid + "$" + rand);
                memberService.updateMemberToken(member.getMember_id(), token, "2");
                data.put("token", token);
                Member memberInfo = memberService.getMemberInfoByMemberId(member.getMember_id(), "2");
                data.put("memberInfo", memberInfo);
                result.setData(data);
                return result;
            } else {
                member = new Member();
                member.setNick_name(nick_name);
                member.setSource(btype);
                member.setOpenid(openid);
                member.setOrg_id(Long.valueOf(org_id));
                member.setIcon(icon);

                String rand = PasswordUtil.getRandomNum();
                String token = PasswordUtil.entryptPassword(openid + "$" + rand);
                member.setToken(token);
                member.setIs_sys_icon(1);//是否使用系统头像(1:是;2:否)
                memberService.saveMember(member, "2");

                JsonResult jsonResult = JsonResult.getSuccess(Constants.LOGIN_BIND_SUCCESS);
                Hashtable<String, Object> data = new Hashtable<String, Object>();
                data.put("token", token);
                Member memberInfo = memberService.getMemberInfoByMemberId(member.getMember_id(), "2");
                data.put("memberInfo", memberInfo);
                jsonResult.setData(data);
                return jsonResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getError(Constants.REG_FAIL);
        }
    }


}
