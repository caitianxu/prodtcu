package com.cjsz.tech.member.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.dev.beans.IndexCountBean;
import com.cjsz.tech.member.beans.MemberInfo;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Author:Jason
 * Date:2017/6/26
 */
public interface UnifyMemberService {

    JsonResult registe(MemberInfo memberInfo);

    //网站注册
    JsonResult webRegister(MemberInfo memberInfo);

    //登录
    JsonResult login(MemberInfo memberInfo,Long org_id);

    //修改密码
    JsonResult chagePwd(String token,String newPwd , String client_type);

    //忘记密码修改新密码
    JsonResult forgetPwd(String account,String newPwd , String client_type);

    JsonResult loadUserInfo(String token);

    UnifyMember findByToken(String token,String token_type);

    //绑定机构
    JsonResult bindOrg(String token, Long orgId,String token_type);

    //更改会员基本信息
    JsonResult modifyUserInfo(String token, String nickName,String sign, Integer sex,String token_type,String client_type);

    //通过userid 终端类型查找用户
    UnifyMember findByAccount(Long user_id, String token_type);

    //通过userId查找用户
    UnifyMember findUserByToken(String token);

    //获取会员评论总数
    Integer getSum(Long member_id);

    //更换头像
    void updateIcon(Long member_id,String icon);

    //绑定手机号
    void bindPhone(Long member_id,String phone);

    //绑定邮箱
    void bindEmail(Long member_id,String email);

    //更新会员
    void updateMember(UnifyMember member);

    //查询会员详情
    Object userInfo(Long member_id);

    //获取会员当天阅读时长
    Integer getTimes(Long member_id,Date today);

    //获取会员阅读总时长
    Integer getTime(Long member_id);

    //获取会员已读图书数量
    Integer getReadNum(Long member_id);

    //获取机构会员阅读排行
    Integer getMemberRank(Long member_id,Long org_id);

    Long getOrgId();

    void updateToken(String token);

    //分页查询
    public Object pageQuery(Sort sort, PageConditionBean condition, Long org_id);

    Integer getCountByOrgId(Long org_id);

    UnifyMember findByMemberId(Long member_id);

    void saveMember(UnifyMember member);

    //批量导入会员
    String importExcel(Map<Integer, List<String>> content);
}
