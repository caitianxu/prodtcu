package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.member.service.MemberLoginService;
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
 * 第三方注册，登录
 * Created by shiaihua on 16/12/21.
 */
@Controller
public class ApiLoginController {

    @Autowired
    MemberLoginService memberLoginService;


    /**
     * 第三方注册，登录接口
     *
     * @return
     */
    @RequestMapping(value = "/v3/app/otherLogin", method = {RequestMethod.POST})
    @ResponseBody
    public Object otherLogin(HttpServletRequest request) {
        String token_type = request.getParameter("token_type");
        String other_type = request.getParameter("other_type");
        String openid = request.getParameter("openid");
        String nick_name = request.getParameter("nick_name");
        String cover = request.getParameter("cover");
        if (StringUtils.isEmpty(other_type) || StringUtils.isEmpty(openid) || StringUtils.isEmpty(token_type)) {
            return JsonResult.getError(Constants.EXCEPTION);
        }
        JsonResult result = memberLoginService.otherLogin(token_type, other_type, openid, nick_name, cover);
        return result;
    }

}
