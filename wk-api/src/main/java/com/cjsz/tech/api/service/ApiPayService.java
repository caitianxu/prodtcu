package com.cjsz.tech.api.service;


import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.meb.domain.MemberPayRecord;
import com.cjsz.tech.meb.mapper.MemberPayRecordMapper;
import com.cjsz.tech.member.beans.UnifyMemeberConstants;
import com.cjsz.tech.utils.HttpClientUtil;
import com.cjsz.tech.utils.JsonResult;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * api支付
 * Created by caitianxu on 2017/04/18.
 */
@Service
public class ApiPayService {

    @Autowired
    MemberPayRecordMapper memberPayRecordMapper;

    public JsonResult payOrder(String pay_type, String amount, String member_token) {
        if ("alipay".equals(pay_type)) {
            pay_type = UnifyMemeberConstants.Module.Action.ALIPAY;
        } else {
            pay_type = UnifyMemeberConstants.Module.Action.WEIXIN_PAY;
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("token", member_token);
        params.put("Amount", amount);
        String result;
        try {
            result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(
                    UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ,
                    UnifyMemeberConstants.Module.PAY, pay_type) , params);
            System.out.println("=====生成订单接口：" + UnifyMemeberConstants.getNewInvokeApiUrl(
                    UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ,
                    UnifyMemeberConstants.Module.PAY, pay_type));
            System.out.println("=====接口参数：" + params);
            if (StringUtils.isEmpty(result)) {
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
        } catch (Exception e) {
            return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
        }
        JSONObject resultInfo = JSONObject.parseObject(result);
        String code = String.valueOf(resultInfo.get("code"));
        if ("0".equals(code)) {
            JsonResult jsonResult = JsonResult.getSuccess((String) resultInfo.get("msg"));
            jsonResult.setData(resultInfo.get("data"));
            System.out.println("======返回参数：" + resultInfo.get("data"));
            return jsonResult;
        } else {
            return JsonResult.getError((String) resultInfo.get("msg"));
        }
    }

    public void savePayRecord(Long member_id, String amount, String pay_type) {
        MemberPayRecord record = new MemberPayRecord();
        record.setMember_id(member_id);
        record.setAmount(new BigDecimal(amount));
        record.setPay_type(pay_type);
        record.setCreate_time(new Date());
        memberPayRecordMapper.insert(record);
    }
}
