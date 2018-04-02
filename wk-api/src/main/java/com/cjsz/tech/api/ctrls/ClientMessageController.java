package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.beans.CJKJClientMessage;
import com.cjsz.tech.api.service.ClientMessageService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ClientMessageController {

    @Autowired
    private ClientMessageService clientMessageService;

    /**
     * 保存客户信息
     * @param request
     * @return
     */
    @RequestMapping("/clientMessage")
    @ResponseBody
    public Object saveMessage(HttpServletRequest request){
        try {
            String client_name = request.getParameter("client_name");
            if (StringUtils.isEmpty(client_name)){
                return JsonResult.getError("请填写姓名!");
            }
            String client_company = request.getParameter("client_company");
            if (StringUtils.isEmpty(client_company)){
                return JsonResult.getError("请填写公司名称！");
            }
            String contact = request.getParameter("contact");
            if (StringUtils.isEmpty(contact)){
                return JsonResult.getError("请填写联系方式");
            }
            CJKJClientMessage message = new CJKJClientMessage();
            message.setClient_name(client_name);
            message.setClient_company(client_company);
            message.setContact(contact);
            clientMessageService.saveMessage(message);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_ADD);
            jsonResult.setData(message);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
