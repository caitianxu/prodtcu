package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.MailTemplateBean;
import com.cjsz.tech.system.domain.MailTemplate;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.MailTemplateService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.system.utils.RequestContextUtil;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by Li Yi on 2017/1/24.
 */
@Controller
public class MailTemplateController {

    @Autowired
    private MailTemplateService mailTemplateService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/admin/mailTemplate/pageQuery", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object pageQuery(@RequestBody MailTemplateBean bean){
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "update_time");
            Object data = mailTemplateService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    @RequestMapping(value = "/admin/mailTemplate/update", method = RequestMethod.POST)
    @ResponseBody
    public Object update(HttpServletRequest request, @RequestBody MailTemplate mailTemplate) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if(sysUser == null){
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            JsonResult result = JsonResult.getSuccess("");
            mailTemplate.setUser_id(sysUser.getUser_id());
            mailTemplate.setUpdate_time(new Date());
            mailTemplate = mailTemplateService.update(mailTemplate);
            result.setMessage(Constants.ACTION_UPDATE);

            result.setData(mailTemplate);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
