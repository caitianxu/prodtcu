package com.cjsz.tech.meb.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.meb.domain.Member;

import com.cjsz.tech.meb.domain.TbMember;
import org.springframework.data.domain.Sort;

import java.util.List;


/**
 * Created by Administrator on 2016/10/25.
 */
public interface MemberService {

    //分页查询
    public Object pageQuery(Sort sort, PageConditionBean condition, Long org_id);

    //停用启用会员
    public void updateMemberStatus(Integer enabled, String memberIds);

    //通过token查找会员
    public Member findByToken(String token, String token_type);//1:web;2:移动端

    public Member getMemberInfoByToken(String member_token, String token_type);//1:web;2:移动端

    public Member findByOpenId(String btype, String openid, String token_type);

    public void updateMemberToken(Long member_id, String token, String token_type);//1:web;2:移动端

    public Member getMemberInfoByMemberId(Long member_id, String token_type);//1:web;2:移动端

    //新增会员
    public void saveMember(Member member, String token_type);

    public Member findByLoginName(String login_name, String token_type);

    //根据id查询会员
    public Member selectById(Long member_id);

    //更新会员
    public void updateMember(Member member);

    public void updatePwd(Long member_id, String new_md5);

    public void updateEmail(Long member_id, String email);

    public void updatePhone(Long member_id, String phone);

    public void updateIsSysIcon(Long member_id, String is_sys_icon);

    public void updateIcon(Long member_id, String icon, String token_type);

    public void updateNickName(Long member_id, String nick_name);

    /**
     * 机构会员数量
     * @param org_id
     * @return
     */
    Integer getCountByOrgId(Long org_id);

    TbMember foundByToken(String token, String token_type);
}
