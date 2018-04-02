package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.MailSettingBean;
import com.cjsz.tech.system.domain.MailSetting;
import com.cjsz.tech.system.domain.MailTemplate;
import com.cjsz.tech.system.service.MailSettingService;
import com.cjsz.tech.system.service.MailTemplateService;
import com.cjsz.tech.system.utils.SendEmailUtil;
import com.cjsz.tech.utils.DateUtils;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2017/1/24.
 */
@Controller
public class MailSettingController {

    @Autowired
    private MailSettingService mailSettingService;

    @Autowired
    private MailTemplateService mailTemplateService;

    /**
     * 邮件设置列表————分页查询
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/mailSetting/pageQuery", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object pageQuery(@RequestBody PageConditionBean bean) {
        try{
            Sort sort = new Sort(Sort.Direction.ASC, "mail_setting_id");
            Object data = mailSettingService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 邮件设置 新增修改
     * @param mailSetting
     * @return
     */
    @RequestMapping(value = "/admin/mailSetting/update", method = RequestMethod.POST)
    @ResponseBody
    public Object update(@RequestBody MailSetting mailSetting) {
        try {
            JsonResult result = JsonResult.getSuccess("");
            if (mailSetting.getMail_setting_id() == null){
                mailSetting.setIs_authenticate(1);//是否需要身份验证(0：否，1：是)
                mailSetting.setIs_ssl(0);//是否SSL加密(0：否，1：是)
                mailSetting.setUsed_times(0);
                mailSetting.setAvailable_times(600);
                mailSetting.setAuto_zero(1);//是否自动清零(0：否，1：是)
                mailSetting = mailSettingService.save(mailSetting);
                result.setMessage(Constants.ACTION_ADD);
            }
            else {
                mailSetting = mailSettingService.update(mailSetting);
                result.setMessage(Constants.ACTION_UPDATE);
            }
            result.setData(mailSetting);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 邮件设置 删除
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/mailSetting/deleteByIdStr", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteByIdStr(@RequestBody MailSettingBean bean) {
        try{
            Long[] ids = bean.getIds();
            if (ids == null || ids.length == 0) {
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String idStr = StringUtils.join(bean.getIds(), ",");
            if (StringUtil.isNotEmpty(idStr)) {
                mailSettingService.deleteByIdStr(idStr);
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 邮件发送 测试
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/mailSetting/sendMail", method = RequestMethod.POST)
    @ResponseBody
    public Object sendMail(HttpServletRequest request, @RequestBody MailSettingBean bean){
        try {
            if (bean == null){
                return JsonResult.getError(Constants.ACTION_ERROR);
            }
            else {
                MailSetting mailSetting = mailSettingService.selectById(bean.getMail_setting_id());
                MailTemplate mailTemplate = mailTemplateService.findByCode(1);
                boolean flag = SendEmailUtil.send(mailSetting, bean.getEmail(), mailTemplate.getMail_template_name(), mailTemplate.getContent());
                if(flag){
                    //更改邮箱发送次数
                    mailSettingService.updateTimes(mailSetting);
                }else{
                    return JsonResult.getError(Constants.EMAIL_SEND_FAILED);
                }
                JsonResult result = JsonResult.getSuccess(Constants.EMAIL_SEND_SUCCESS);
                result.setData(new ArrayList());
                return result;
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
