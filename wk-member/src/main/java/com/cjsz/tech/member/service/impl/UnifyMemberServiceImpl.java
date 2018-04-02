package com.cjsz.tech.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.member.beans.LoginResultInfo;
import com.cjsz.tech.member.beans.MemberInfo;
import com.cjsz.tech.member.beans.UnifyMemeberConstants;
import com.cjsz.tech.member.beans.UserInfo;
import com.cjsz.tech.member.domain.MemberDept;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.enums.ClientType;
import com.cjsz.tech.member.mapper.MemberDeptMapper;
import com.cjsz.tech.member.mapper.UnifyMemberMapper;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.mapper.OrganizationMapper;
import com.cjsz.tech.utils.HttpClientUtil;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Author:Jason
 * Date:2017/6/26
 */
@Service
public class UnifyMemberServiceImpl implements UnifyMemberService {
    @Autowired
    private UnifyMemberMapper unifyMemberMapper;

    @Autowired
    private MemberDeptMapper memberDeptMapper;

    //判断app是否为企业版
    public static boolean clientTypeQY(String clientType) {
        if (ClientType.ORG_VERSION.code().equals(clientType)) {
            return true;
        } else {
            return false;
        }
    }

    //判断app是否为大众版
    public static boolean clientTypeDZ(String clientType) {
        if (ClientType.PUBLIC_VERSION.code().equals(clientType)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public JsonResult registe(MemberInfo memberInfo) {
        Map<String, Object> params = Maps.newHashMap();
        String account = memberInfo.getAccount();
        params.put("userName", account);
        params.put("userPass", memberInfo.getPwd());
        String result = null;
        try {
            //企业版
            if (clientTypeQY(memberInfo.getClinet_type())) {
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getInvokeApiUrl(UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.REG_USER), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            } else if (clientTypeDZ(memberInfo.getClinet_type())) {
                //大众版
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.REG_USER), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            } else {
                return JsonResult.getError("客户端错误");
            }
        } catch (Exception e) {
            return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
        }
        JSONObject resultInfo = JSONObject.parseObject(result);
        System.out.println(resultInfo.toJSONString());
        String code = String.valueOf(resultInfo.get("code"));
        if ("0".equals(code)) {
            if(memberInfo.getDept_id() != null){
                MemberDept memberDept = new MemberDept();
                memberDept.setOrg_id(memberInfo.getOrg_id());
                memberDept.setAccount(account);
                memberDept.setCreate_time(new Date());
                memberDept.setDept_id(memberInfo.getDept_id());
                memberDeptMapper.insert(memberDept);
            }
            return JsonResult.getSuccess(UnifyMemeberConstants.TipInfo.REGISTE_OK);
        } else {
            return JsonResult.getError((String) resultInfo.get("msg"));
        }
    }

    @Override
    @Transactional
    public JsonResult webRegister(MemberInfo memberInfo) {
        Map<String, Object> params = Maps.newHashMap();
        String account = memberInfo.getAccount();
        if (account.contains("@")) {

        }
        params.put("userName", account);
        params.put("userPass", memberInfo.getPwd());
        String result = null;
        try {
            if (clientTypeQY(memberInfo.getClinet_type())) {
                //企业版
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getInvokeApiUrl(UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.REG_USER), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            } else if (clientTypeDZ(memberInfo.getClinet_type())) {
                //大众版
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.REG_USER), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            } else {
                return JsonResult.getError("客户端错误");
            }
        } catch (Exception e) {
            return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
        }
        JSONObject resultInfo = JSONObject.parseObject(result);
        System.out.println(resultInfo.toJSONString());
        String code = String.valueOf(resultInfo.get("code"));
        if ("0".equals(code)) {
            if(memberInfo.getDept_id() != null){
                MemberDept memberDept = new MemberDept();
                memberDept.setAccount(account);
                memberDept.setCreate_time(new Date());
                memberDept.setDept_id(memberInfo.getDept_id());
                memberDeptMapper.insert(memberDept);
            }
            return JsonResult.getSuccess(UnifyMemeberConstants.TipInfo.REGISTE_OK);
        } else {
            return JsonResult.getError((String) resultInfo.get("msg"));
        }
    }

    @Override
    public JsonResult bindOrg(String token, Long orgId, String token_type) {
        UnifyMember member = findByToken(token, token_type);
        if (member == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            unifyMemberMapper.updateOrgId(member.getMember_id(), orgId);
            JsonResult jsonResult = JsonResult.getSuccess("绑定成功");
            UnifyMember unifyMember = unifyMemberMapper.findUserByToken(token);
            jsonResult.setData(unifyMember);
            return jsonResult;
        } catch (Exception e) {
            return JsonResult.getError("绑定失败!");
        }
    }

    @Override
    public JsonResult modifyUserInfo(String token, String nickName, String sign, Integer sex, String token_type, String client_type) {

        Map<String, Object> params = Maps.newHashMap();
        params.put("token", token);
        if (StringUtils.isNotEmpty(nickName)) {
            params.put("nickName", nickName);
        }
        if (null != sex) {
            params.put("sex", String.valueOf(sex));
        }
        String result = null;
        try {
            if (clientTypeQY(client_type)) {
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getInvokeApiUrl(UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.UPDATE_USER_INFO), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            } else if (clientTypeDZ(client_type)) {
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.UPDATE_USER_INFO), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            } else {
                return JsonResult.getError("客户端错误");
            }
        } catch (Exception e) {
            return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
        }
        JSONObject resultInfo = JSONObject.parseObject(result);
        System.out.println(resultInfo.toJSONString());
        String code = String.valueOf(resultInfo.get("code"));
        if ("0".equals(code)) {
            try {
                UnifyMember member = findByToken(token, token_type);
                if (member == null) {
                    return JsonResult.getExpire(Constants.TOKEN_FAILED);
                }
                if (StringUtils.isNotEmpty(nickName)) {
                    unifyMemberMapper.updateNickName(member.getMember_id(), nickName);
                }
                if (StringUtils.isNotEmpty(sign)) {
                    unifyMemberMapper.updateSign(member.getMember_id(), sign);
                }
                if (null != sex) {
                    unifyMemberMapper.updateSex(member.getMember_id(), sex);
                }
                UnifyMember unifyMember = unifyMemberMapper.findUserByToken(token);
                JsonResult jsonResult = JsonResult.getSuccess(UnifyMemeberConstants.TipInfo.CHANGE_OK);
                jsonResult.setData(unifyMember);
                return jsonResult;
            } catch (Exception e) {
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.CHANGE_FAILURE);
            }
        } else {
            return JsonResult.getError((String) resultInfo.get("msg"));
        }
    }

    /**
     * 会员登录
     *
     * @param memberInfo
     * @return
     */
    @Override
    public JsonResult login(MemberInfo memberInfo, Long org_Id) {
        Map<String, Object> params = Maps.newHashMap();
        String account = memberInfo.getAccount();
        String token_type = memberInfo.getToken_type();
        params.put("userName", account);
        params.put("userPass", memberInfo.getPwd());
        String result = null;
        try {
            if (clientTypeQY(memberInfo.getClinet_type())) {
                //企业版
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getInvokeApiUrl(UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.USER_LOGIN), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            } else if (clientTypeDZ(memberInfo.getClinet_type())) {
                //大众版
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.USER_LOGIN), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            } else {
                return JsonResult.getError("客户端错误");
            }
        } catch (Exception e) {
            return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
        }
        JSONObject resultInfo = JSONObject.parseObject(result);
        System.out.println(resultInfo.toJSONString());
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
            if (StringUtils.isEmpty(token_type) && token_type == null) {
                return JsonResult.getError(Constants.EXCEPTION);
            }
            UnifyMember member = findByAccount(Long.parseLong(loginResultInfo.getUserId()), token_type);
            UnifyMember mem = unifyMemberMapper.selectByMemberId(Long.parseLong(loginResultInfo.getUserId()));
            MemberDept memberDept = memberDeptMapper.selectByAccount(account);
            if (member == null) { //会员账号在长江中文网上存在并且可以正常登陆，如果本地不存在，则在本地记录
                member = new UnifyMember();

                if (mem != null) {          //曾经登录过其他终端
                    if (mem.getOrg_id() == null) {
                        member.setOrg_id(org_Id);
                    } else {
                        member.setOrg_id(mem.getOrg_id());
                        member.setIcon(mem.getIcon());
                        member.setDept_id(mem.getDept_id());
                    }
                } else {        //未登录过任何终端
                    if (memberDept != null){
                        if (memberDept.getOrg_id().longValue() != org_Id.longValue()){  //带组织注册并且与机构不相符
                            return JsonResult.getError("你无权登录其他企业网站,请重新核对网站地址！");
                        }
                    }
                    member.setOrg_id(org_Id);
                }
                if (memberDept != null) {
                    member.setDept_id(memberDept.getDept_id());
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
                if (memberinfo.getOrg_id() != org_Id.longValue()) {
                    return JsonResult.getOther("你无权登录其他企业网站,请重新核对网站地址！");
                }
            }
            //大众版获取用户余额
            memberinfo.setBalance(loginResultInfo.getBalance());
            jsonResult.setData(memberinfo);
            return jsonResult;
        } else {
            return JsonResult.getError("账号不存在或密码错误");
        }
    }

    /**
     * 修改密码
     *
     * @param token
     * @param newPwd
     * @return
     */
    @Override
    public JsonResult chagePwd(String token, String newPwd, String client_type) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("token", token);
        params.put("newPassword", newPwd);
        String result = null;
        try {
            if (clientTypeQY(client_type)) {
                //企业版
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getInvokeApiUrl(UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.CHANGE_PWD), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            } else if (clientTypeDZ(client_type)) {
                //大众版
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.CHANGE_PWD), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            } else {
                return JsonResult.getError("客户端错误");
            }
        } catch (Exception e) {
            return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
        }
        JSONObject resultInfo = JSONObject.parseObject(result);
        System.out.println(resultInfo.toJSONString());
        String code = String.valueOf(resultInfo.get("code"));
        if ("0".equals(code)) {
            UnifyMember unifyMember = unifyMemberMapper.findUserByToken(token);
            JsonResult jsonResult = JsonResult.getSuccess(UnifyMemeberConstants.TipInfo.CHANGE_PWD_OK);
            jsonResult.setData(unifyMember);
            return jsonResult;
        } else {
            return JsonResult.getError((String) resultInfo.get("msg"));
        }
    }


    /**
     * 忘记密码
     *
     * @param account
     * @param newPwd
     * @return
     */
    @Override
    public JsonResult forgetPwd(String account, String newPwd, String client_type) {
        Map<String, Object> params = Maps.newHashMap();
        if (account.contains("@")) {
            String mail = unifyMemberMapper.getMail(account);
            if (StringUtils.isEmpty(mail)) {
                return JsonResult.getError("该邮箱未绑定账号");
            }
            params.put("mail", account);
        } else {
            String mob = unifyMemberMapper.getMob(account);
            if (StringUtils.isEmpty(mob)) {
                return JsonResult.getError("该手机号未绑定账号");
            }
            params.put("mob", account);
        }
        params.put("newPassword", newPwd);
        String result = null;
        try {
            if (clientTypeQY(client_type)) {
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getInvokeApiUrl(UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.GORGET_PWD), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            } else if (clientTypeDZ(client_type)) {
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.GORGET_PWD), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            } else {
                return JsonResult.getError("客户端错误");
            }
        } catch (Exception e) {
            return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
        }
        JSONObject resultInfo = JSONObject.parseObject(result);
        System.out.println(resultInfo.toJSONString());
        String code = String.valueOf(resultInfo.get("code"));
        if ("0".equals(code)) {
            JsonResult jsonResult = JsonResult.getSuccess(UnifyMemeberConstants.TipInfo.CHANGE_PWD_OK);
            return jsonResult;
        } else {
            return JsonResult.getError((String) resultInfo.get("msg"));
        }
    }

    @Override
    public JsonResult loadUserInfo(String token) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("token", token);
        String result = null;
        try {
            result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getInvokeApiUrl(UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.LOAD_USER_INFO), params);
            if (StringUtils.isEmpty(result)) {
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
        } catch (Exception e) {
            return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
        }
        JSONObject resultInfo = JSONObject.parseObject(result);
        System.out.println(resultInfo.toJSONString());
        String code = String.valueOf(resultInfo.get("code"));
        if ("0".equals(code)) {
            JSONObject data = (JSONObject) resultInfo.get("data");
            UserInfo userInfo = JSONObject.parseObject(data.toJSONString(), UserInfo.class);
            JsonResult jsonResult = JsonResult.getSuccess(UnifyMemeberConstants.TipInfo.LOAD_OK);
            jsonResult.setData(userInfo);
            return jsonResult;
        } else {
            return JsonResult.getError((String) resultInfo.get("msg"));
        }
    }

    @Override
    public UnifyMember findByToken(String token, String token_type) {
        return unifyMemberMapper.findByTokenAndTokenType(token, token_type);
    }

    @Override
    public UnifyMember findByAccount(Long user_id, String token_type) {
        return unifyMemberMapper.findByAccount(user_id, token_type);
    }

    @Override
    public UnifyMember findUserByToken(String token) {
        return unifyMemberMapper.findUserByToken(token);
    }

    @Override
    public Integer getSum(Long member_id) {
        return unifyMemberMapper.getSum(member_id);
    }

    @Override
    public void updateIcon(Long member_id, String icon) {
        unifyMemberMapper.updateIcon(member_id, icon);
    }

    @Override
    public void bindPhone(Long member_id, String phone) {
        unifyMemberMapper.updatePhone(member_id, phone);
    }

    @Override
    public void bindEmail(Long member_id, String email) {
        unifyMemberMapper.updateEmail(member_id, email);
    }

    @Override
    public void updateMember(UnifyMember member) {
        unifyMemberMapper.updateByPrimaryKey(member);
    }

    @Override
    public Object userInfo(Long member_id) {
        try {
            List<UnifyMember> members = unifyMemberMapper.selectUser(member_id);
            if (members.size() <= 0) {
                return JsonResult.getError("用户不存在！");
            }
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(members);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    @Override
    public Integer getTimes(Long member_id, Date today) {
        return unifyMemberMapper.getTimes(member_id, today);

    }

    @Override
    public Integer getTime(Long member_id) {
        Integer num = unifyMemberMapper.getTime(member_id);
        if (num == null) {
            return 0;
        }
        Integer result = num / 60;
        if (result == 0) {
            result = 1;
        }
        return result;
    }

    @Override
    public Integer getMemberRank(Long member_id, Long org_id) {
        return unifyMemberMapper.getMemberRank(member_id, org_id);
    }

    @Override
    public Integer getReadNum(Long member_id) {
        return unifyMemberMapper.getNum(member_id);
    }

    @Override
    public Long getOrgId() {
        return unifyMemberMapper.getOrgId();
    }

    @Override
    public void updateToken(String token) {
        unifyMemberMapper.updateToken(token);
    }

    @Override
    public Object pageQuery(Sort sort, PageConditionBean condition, Long org_id) {
        PageHelper.startPage(condition.getPageNum(), condition.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<UnifyMember> result = unifyMemberMapper.getMemberList(condition.getSearchText(), org_id);
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public Integer getCountByOrgId(Long org_id) {
        if (org_id == 1) {
            return unifyMemberMapper.getCount();
        } else {
            return unifyMemberMapper.getCountByOrgId(org_id);
        }
    }

    @Override
    public UnifyMember findByMemberId(Long member_id) {
        return unifyMemberMapper.selectByMemberId(member_id);
    }

    @Override
    public void saveMember(UnifyMember member) {
        unifyMemberMapper.insert(member);
    }

    @Override
    public String importExcel(Map<Integer, List<String>> content) {
        List<String> errorNumMsg = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : content.entrySet()) {
            List<String> values = entry.getValue();
            String num = values.get(0);//序号:0
            if (StringUtils.isEmpty(num)) {
                continue;
            }
            String account = values.get(1);//账号
            if (StringUtils.isEmpty(account)) {
                errorNumMsg.add(num);
                continue;
            }
            String pwd = values.get(2);//密码
            if (StringUtils.isEmpty(pwd)) {
                errorNumMsg.add(num);
                continue;
            }
            MemberInfo member = new MemberInfo();
            member.setClinet_type("QY");
            member.setAccount(account);
            member.setPwd(pwd);
            JsonResult result = registe(member);
            if (result.getCode() != 0) {
                errorNumMsg.add(num);
            } else {
                System.out.println("序号：" + num + "注册成功");
            }

        }
        if (errorNumMsg.size() > 0) {
            return "序号为" + StringUtils.join(errorNumMsg, ",") + "注册失败";
        } else {
            return null;
        }
    }

}
