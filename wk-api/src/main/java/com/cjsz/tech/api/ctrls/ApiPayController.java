package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.service.ApiPayService;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * APP支付
 * Created by shiaihua on 16/12/21.
 */
@Controller
public class ApiPayController {

    @Autowired
    UnifyMemberService unifyMemberService;

    @Autowired
    ApiPayService apiPayService;

    /**
     * 大众版支付接口
     *
     * @return
     */
    @RequestMapping(value = "/v3/app/payOrder", method = {RequestMethod.POST})
    @ResponseBody
    public Object payOrder(HttpServletRequest request) {
        String pay_type = request.getParameter("pay_type");
        String amount = request.getParameter("amount");
        String member_token = request.getParameter("member_token");
        String token_type = request.getParameter("token_type");
        if (StringUtils.isEmpty(pay_type) || StringUtils.isEmpty(amount)) {
            return JsonResult.getError(Constants.EXCEPTION);
        }
        if (StringUtils.isEmpty(member_token) || StringUtils.isEmpty(token_type)) {
            return JsonResult.getExpire(Constants.TOKEN_FAILED);
        }
        UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
        if (member == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        JsonResult result = apiPayService.payOrder(pay_type, amount, member_token);
        return result;
    }

    /**
     * 大众版支付成功记录
     *
     * @return
     */
    @RequestMapping(value = "/v3/app/payRecord", method = {RequestMethod.POST})
    @ResponseBody
    public Object payRecord(HttpServletRequest request) {
        String pay_type = request.getParameter("pay_type");
        String amount = request.getParameter("amount");
        String member_token = request.getParameter("member_token");
        String token_type = request.getParameter("token_type");
        if (StringUtils.isEmpty(pay_type) || StringUtils.isEmpty(amount)) {
            return JsonResult.getError(Constants.EXCEPTION);
        }
        if (StringUtils.isEmpty(member_token) || StringUtils.isEmpty(token_type)) {
            return JsonResult.getExpire(Constants.TOKEN_FAILED);
        }
        UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
        if (member == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        apiPayService.savePayRecord(member.getMember_id(), amount, pay_type);
        return JsonResult.getSuccess(Constants.PAY_SUCCESS);
    }
}
