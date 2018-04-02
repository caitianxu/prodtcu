package com.cjsz.tech.system.utils;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.BizResult;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


/**
 * 发送短信
 * Created by LuoLi on 2017/3/30.
 */
@Configuration
public class SendMessageUtil {


    private final static String url = "http://gw.api.taobao.com/router/rest";
    private final static String appkey = "23728020";
    private final static String secret = "647debeecff3af766a3cc242dadc1fcd";

    public static BizResult sendMobileSMS(Map<String, String> param) {
        String rusult = null;
        //参数
        String smsFreeSignName = param.get("smsFreeSignName");
        String smsParamString = param.get("smsParamString");
        String phone = param.get("phone");
        String smsTemplateCode = param.get("smsTemplateCode");

        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setSmsType("normal");
        req.setSmsFreeSignName(smsFreeSignName);
        req.setSmsParamString(smsParamString);
        req.setRecNum(phone);
        req.setSmsTemplateCode(smsTemplateCode);
        try {
            AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
            System.out.println(rsp.getBody());
            rusult = rsp.getSubMsg();
            System.out.println(rusult);
            return rsp.getResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

