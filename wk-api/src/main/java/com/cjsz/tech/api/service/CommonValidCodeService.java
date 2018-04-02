package com.cjsz.tech.api.service;

import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.MailSetting;
import com.cjsz.tech.system.domain.MailTemplate;
import com.cjsz.tech.system.service.MailSettingService;
import com.cjsz.tech.system.service.MailTemplateService;
import com.cjsz.tech.system.utils.CacheUtil;
import com.cjsz.tech.system.utils.SendEmailUtil;
import com.cjsz.tech.system.utils.SendMessageUtil;
import com.cjsz.tech.util.ValidCodeUtil;
import com.cjsz.tech.utils.JsonResult;
import com.google.common.base.Joiner;
import com.taobao.api.domain.BizResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 验证码
 * Created by LuoLi on 2017/3/31 0031.
 */
@Service
public class CommonValidCodeService {

    @Autowired
    private MailSettingService mailSettingService;

    @Autowired
    private MailTemplateService mailTemplateService;

    public static final String KEY_JOIN_CHAR="$";


    //验证码校验
    public JsonResult matchVerificationCodeService(String type, String code, String account) {
        JsonResult jsonResult = new JsonResult();
        if(StringUtils.isEmpty(code)){
            jsonResult.setCode(1);
            jsonResult.setMessage("未填写验证码!");
            return jsonResult;
        }
        String key = Joiner.on(KEY_JOIN_CHAR).join(type, account);
        String validCode = (String)CacheUtil.get(key);
        if(StringUtils.isEmpty(validCode)){
            jsonResult.setCode(1);
            jsonResult.setMessage("验证码错误!");
            return jsonResult;
        }else{
            if(!StringUtils.equals(validCode,code)){
                jsonResult.setCode(1);
                jsonResult.setMessage("验证码错误!");
                return jsonResult;
            }
            jsonResult.setCode(0);
            jsonResult.setMessage("验证通过!");
            CacheUtil.del(key);
        }
        return jsonResult;
    }


    //验证码发送
    public JsonResult sendVerificationCode(String account, String type) {
        //生成6位随机验证码
        String code = ValidCodeUtil.getRandNum(6);
        if (account.contains("@")) {//邮箱接收验证码
            account = account.toLowerCase();
            MailSetting setting = mailSettingService.getMail();
            if (null == setting) {
                return JsonResult.getError(Constants.EMAIL_SEND_FAILED);
            }
            MailTemplate template = mailTemplateService.findByCode(2);
            if (template == null) {
                return JsonResult.getError(Constants.EMAIL_SEND_FAILED);
            }
            String content = template.getContent().replace("code", code);
            Calendar now = Calendar.getInstance();
            content = content.replace("yyyy", now.get(Calendar.YEAR) + "")
                    .replace("MM", (now.get(Calendar.MONTH) + 1) + "")
                    .replace("dd", now.get(Calendar.DAY_OF_MONTH) + "");
            boolean flag = SendEmailUtil.send(setting, account, template.getMail_template_name(), content);
            if (flag) {
                //更改邮箱发送次数
                mailSettingService.updateTimes(setting);
                //保存验证码
                CacheUtil.set(Joiner.on(KEY_JOIN_CHAR).join(type, account), code, 30 * 60);
                return JsonResult.getSuccess(Constants.EMAIL_SEND_SUCCESS);
            } else {
                return JsonResult.getError(Constants.EMAIL_SEND_FAILED);
            }

        } else {//手机接收验证码
            Map<String, String> param = new HashMap<String, String>();
            param.put("smsFreeSignName", "长江数字");
            param.put("smsParamString", "{\"code\":\"" + code + "\"}");
            param.put("phone", account);
            param.put("smsTemplateCode", "SMS_59875051");
            BizResult bizResult = SendMessageUtil.sendMobileSMS(param);
            if (bizResult != null) {
                CacheUtil.set(Joiner.on(KEY_JOIN_CHAR).join(type, account), code, 10 * 60);
                return JsonResult.getSuccess("短信发送成功");
            } else {
                return JsonResult.getError("短信发送失败");
            }

        }
    }


    //验证码校验
//    public JsonResult matchVerificationCodeService(String type, String code, String account) {
//        String reg_code = "";
//        if (type.equals("changeEmail")) {
//            reg_code = (String) CacheUtil.get("email_code");
//        } else if (type.equals("changePhone")) {
//            reg_code = (String) CacheUtil.get("phone_code");
//        } else if (type.equals("register")) {
//            reg_code = (String) CacheUtil.get("reg_code");
//        } else if (type.equals("forgetPwd")) {
//            reg_code = (String) CacheUtil.get("pwd_code");
//        }
//        JsonResult jsonResult = new JsonResult();
//        if (StringUtils.isEmpty(code)) {
//            jsonResult.setCode(1);
//            jsonResult.setMessage("未填写验证码!");
//        } else if (!(account + code).equals(reg_code)) {
//            jsonResult.setCode(1);
//            jsonResult.setMessage("验证码错误!");
//        } else {
//            jsonResult.setCode(0);
//            jsonResult.setMessage("验证通过!");
//            if (type.equals("changeEmail")) {
//                CacheUtil.del("email_code");
//            } else if (type.equals("changePhone")) {
//                CacheUtil.del("phone_code");
//            } else if (type.equals("register")) {
//                CacheUtil.del("reg_code");
//            } else if (type.equals("forgetPwd")) {
//                CacheUtil.del("pwd_code");
//            }
//        }
//        jsonResult.setData(new ArrayList<>());
//        return jsonResult;
//    }

    //验证码发送
//    public JsonResult sendVerificationCode(String account, String type) {
//        //生成6位随机验证码
//        String code = ValidCodeUtil.getRandNum(6);
//        if (account.contains("@")) { //邮箱接收验证码
//            account = account.toLowerCase();
//            MailSetting setting = mailSettingService.getMail();
//            if (null == setting) {
//                return JsonResult.getError(Constants.EMAIL_SEND_FAILED);
//            }
//            MailTemplate template = mailTemplateService.findByCode(2);
//            if (template == null) {
//                return JsonResult.getError(Constants.EMAIL_SEND_FAILED);
//            }
//            String content = template.getContent().replace("code", code);
//
//            Calendar now = Calendar.getInstance();
//            content = content.replace("yyyy", now.get(Calendar.YEAR) + "")
//                    .replace("MM", (now.get(Calendar.MONTH) + 1) + "")
//                    .replace("dd", now.get(Calendar.DAY_OF_MONTH) + "");
//            boolean flag = SendEmailUtil.send(setting, account, template.getMail_template_name(), content);
//            if (flag) {
//                //更改邮箱发送次数
//                mailSettingService.updateTimes(setting);
//                //保存验证码
//                if (type.equals("register")) {
//                    CacheUtil.set("reg_code", account + code, 30 * 60);
//                } else if (type.equals("forgetPwd")) {
//                    CacheUtil.set("pwd_code", account + code, 30 * 60);
//                } else if (type.equals("changeEmail")) {
//                    CacheUtil.set("email_code", account + code, 30 * 60);
//                }
//                return JsonResult.getSuccess(Constants.EMAIL_SEND_SUCCESS);
//            } else {
//                return JsonResult.getError(Constants.EMAIL_SEND_FAILED);
//            }
//        } else {//手机接收验证码
//            Map<String, String> param = new HashMap<String, String>();
//            param.put("smsFreeSignName", "长江数字");
//            param.put("smsParamString", "{\"code\":\"" + code + "\"}");
//            param.put("phone", account);
//            param.put("smsTemplateCode", "SMS_59875051");
//            BizResult bizResult = SendMessageUtil.sendMobileSMS(param);
//            if (bizResult != null) {
//                if (type.equals("register")) { //注册验证码
//                    CacheUtil.set("reg_code", account + code, 10 * 60);
//                } else if (type.equals("forgetPwd")) { //忘记密码
//                    CacheUtil.set("pwd_code", account + code, 10 * 60);
//                } else if (type.equals("changePhone")) {//更换手机号
//                    CacheUtil.set("phone_code", account + code, 10 * 60);
//                }
//                return JsonResult.getSuccess("短信发送成功");
//            } else {
//                return JsonResult.getSuccess("短信发送失败");
//            }
//        }
//    }

}
