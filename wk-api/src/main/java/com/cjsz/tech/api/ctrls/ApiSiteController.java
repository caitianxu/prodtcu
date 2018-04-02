package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.service.CommonValidCodeService;
import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by LuoLi on 2017/4/19 0019.
 */
@Controller
public class ApiSiteController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private CommonValidCodeService commonValidCodeService;

    /**
     * 发送验证码
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/validCode/sendValidCode", method = {RequestMethod.POST})
    @ResponseBody
    public Object sendVerificationCode(HttpServletRequest request) {
        try {
            String account = request.getParameter("account");
            String type = request.getParameter("type");
            return commonValidCodeService.sendVerificationCode(account, type);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 检验验证码（修改邮箱、手机时，原邮箱、手机验证）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/validCode/matchValidCode", method = {RequestMethod.POST})
    @ResponseBody
    public Object matchVerificationCode(HttpServletRequest request) {
        try {
            String type = request.getParameter("type");
            String account = request.getParameter("account");
            String code = request.getParameter("code");
            account = account.toLowerCase();
            if ("changeEmail".equals(type) || "changePhone".equals(type)) {
                String member_token = request.getParameter("member_token");
                Member member = null;
                if (member_token != null) {
                    member = memberService.findByToken(member_token, "2");
                }
                if (member == null) {
                    return JsonResult.getExpire(Constants.TOKEN_FAILED);
                }
                if (!(account.equals(member.getEmail()) || account.equals(member.getPhone()))) {
                    return JsonResult.getError("账号不存在！");
                }
            }
            JsonResult jsonCode = commonValidCodeService.matchVerificationCodeService(type, code, account);
            return jsonCode;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
