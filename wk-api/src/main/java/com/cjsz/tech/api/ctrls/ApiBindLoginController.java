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
public class ApiBindLoginController {

    @Autowired
    MemberService memberService;

    /**
     * 手机第三方注册绑定、登录
     *
     * @return
     */
    @RequestMapping(value = "/api/member/member_bind", method = {RequestMethod.POST})
    @ResponseBody
    public Object userBind(HttpServletRequest request) {
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
                data.put("member_token", token);
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
                member.setIs_sys_icon(2);//是否使用系统头像(1:是;2:否)
                memberService.saveMember(member, "2");

                JsonResult jsonResult = JsonResult.getSuccess(Constants.LOGIN_BIND_SUCCESS);
                Hashtable<String, Object> data = new Hashtable<String, Object>();
                data.put("member_token", token);
                Member memberInfo = memberService.getMemberInfoByMemberId(member.getMember_id(), "2");
                data.put("member_info", memberInfo);
                jsonResult.setData(data);
                return jsonResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getError(Constants.REG_FAIL);
        }
    }


}
