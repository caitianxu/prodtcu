package com.cjsz.tech.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.member.beans.LoginResultInfo;
import com.cjsz.tech.member.beans.UnifyMemeberConstants;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.mapper.UnifyMemberMapper;
import com.cjsz.tech.member.service.MemberLoginService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.mapper.OrganizationMapper;
import com.cjsz.tech.utils.HttpClientUtil;
import com.cjsz.tech.utils.JsonResult;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * Author:Jason
 * Date:2017/6/26
 */
@Service
public class MemberLoginServiceImpl implements MemberLoginService {
    @Autowired
    private UnifyMemberMapper unifyMemberMapper;

    @Autowired
    private OrganizationMapper organizationMapper;


    /**
     * 第三方注册，登录
     *
     * @param other_type
     * @param openid
     * @param nick_name
     * @param cover
     * @return
     */
    @Override
    public JsonResult otherLogin(String token_type, String other_type, String openid, String nick_name, String cover) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("type", other_type);
        params.put("openid", openid);
        params.put("nickname", nick_name);
        params.put("userpic", cover);
        String result;
        try {
            //大众版
            result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(
                    UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ,
                    UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.OTHER_LOGIN), params);
            if (StringUtils.isEmpty(result)) {
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
        } catch (Exception e) {
            return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
        }
        JSONObject resultInfo = JSONObject.parseObject(result);
        String code = String.valueOf(resultInfo.get("code"));
        if ("0".equals(code)) {
            JSONObject data = (JSONObject) resultInfo.get("data");
            LoginResultInfo loginResultInfo = JSONObject.parseObject(data.toJSONString(), LoginResultInfo.class);
            if (StringUtils.isEmpty(loginResultInfo.getNickName())) {
                loginResultInfo.setNickName(loginResultInfo.getUserId());
            }
            String token = loginResultInfo.getToken();
            if (loginResultInfo.getUserId() == null) {
                return JsonResult.getError(Constants.EXCEPTION);
            }
            if (StringUtils.isEmpty(token)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            UnifyMember member = unifyMemberMapper.findByAccount(Long.parseLong(loginResultInfo.getUserId()), token_type);
            UnifyMember mem = unifyMemberMapper.selectByMemberId(Long.parseLong(loginResultInfo.getUserId()));
            if (member == null) { //会员账号在长江中文网上存在并且可以正常登陆，如果本地不存在，则在本地记录
                member = new UnifyMember();
                if (mem != null) {
                    if (mem.getOrg_id() == null) {
                        member.setOrg_id(189L);
                    } else {
                        member.setOrg_id(mem.getOrg_id());
                        member.setIcon(mem.getIcon());
                        member.setSign(mem.getSign());
                    }
                } else {
                    member.setIcon(cover);
                    member.setOrg_id(189L);
                }
                member.setAccount(loginResultInfo.getUserName());
                member.setSex(loginResultInfo.getNickSex());
                member.setNick_name(loginResultInfo.getNickName());
                member.setEmail(loginResultInfo.getUserEmail());
                member.setPhone(loginResultInfo.getUserMobi());
                member.setToken(token);
                member.setMember_id(Long.parseLong(loginResultInfo.getUserId()));
                member.setToken_type(token_type);
                unifyMemberMapper.insert(member);
            } else {
                //存储token
                unifyMemberMapper.saveToken(loginResultInfo.getUserId(), token, token_type);
            }
            JsonResult jsonResult = JsonResult.getSuccess(UnifyMemeberConstants.TipInfo.LOGIN_OK);
            //根据用户ID重新获取用户信息
            UnifyMember memberinfo = unifyMemberMapper.findUserByToken(token);
            if (token_type.equals("weixin") || token_type.equals("pc")) {
                if (memberinfo.getOrg_id() != 189L) {
                    return JsonResult.getOther("你无权登录其他企业网站,请重新核对网站地址！");
                }
            }
            memberinfo.setBalance(loginResultInfo.getBalance());
            jsonResult.setData(memberinfo);
            return jsonResult;
        } else {
            return JsonResult.getError((String) resultInfo.get("msg"));
        }
    }

}
