package com.cjsz.tech.meb.service.impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.domain.MemberGrade;
import com.cjsz.tech.meb.domain.MemberTokenRel;
import com.cjsz.tech.meb.domain.TbMember;
import com.cjsz.tech.meb.mapper.MemberMapper;
import com.cjsz.tech.meb.mapper.MemberTokenRelMapper;
import com.cjsz.tech.meb.service.MemberGradeService;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.PasswordUtil;
import com.github.pagehelper.PageHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2016/10/25.
 */
@Service
public class MemberServiceImpl implements MemberService {
	
    @Autowired
    private MemberMapper memberMapper;

	@Autowired
	private MemberGradeService memberGradeService;

	@Autowired
	private MemberTokenRelMapper memberTokenRelMapper;

	@Override
	public Object pageQuery(Sort sort, PageConditionBean condition, Long org_id) {
		PageHelper.startPage(condition.getPageNum(), condition.getPageSize());
		String order = ConditionOrderUtil.prepareOrder(sort);
		if (order != null) {
			PageHelper.orderBy(order);
		}
		List<Member> result = memberMapper.getMemberList(condition.getSearchText(), org_id);
		for(Member member : result){
			//是否使用系统头像(1:是;2:否)
			if(member.getIs_sys_icon() != 1){
				member.setMember_header(member.getIcon());
			}
		}
		PageList pageList = new PageList(result, null);
		return pageList;
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "会员")
	public void updateMemberStatus(Integer enabled, String memberIds) {
		memberMapper.updateMemberStatus(enabled, memberIds);
	}

	@Override
	public Member findByToken(String token, String token_type) {
		return memberMapper.findByToken(token, token_type);
	}

	@Override
	public Member getMemberInfoByToken(String member_token, String token_type) {
		Member member = memberMapper.getMemberInfoByToken(member_token, token_type);
		return member;
	}

	@Override
	public Member findByOpenId(String btype, String openid, String token_type) {
		return memberMapper.findByOpenId(btype, openid, token_type);
	}

	@Override
	public void updateMemberToken(Long id, String token, String token_type) {
		MemberTokenRel rel = memberTokenRelMapper.findByMemberIdAndType(id, token_type);
		if(rel == null){
			rel = new MemberTokenRel(id, token, token_type);
			memberTokenRelMapper.insert(rel);
		}else{
			memberTokenRelMapper.updateMemberToken(id, token, token_type);
		}
	}

	@Override
	public Member getMemberInfoByMemberId(Long member_id, String token_type) {
		Member member = memberMapper.getMemberInfoByMemberId(member_id, token_type);
		return member;
	}

	@Transactional
	public void saveMember(Member member, String token_type){
		member.setEnabled(Constants.ENABLE); //启用
		member.setIs_delete(Constants.NOT_DELETE);
		member.setCreate_time(new Date());
		member.setUpdate_time(new Date());
		MemberGrade memberGrade1 = memberGradeService.selectByGradeName(1);
		MemberGrade memberGrade2 = memberGradeService.selectByGradeName(2);
		member.setGrade_point(0L);
		member.setGrade_id(memberGrade1.getGrade_id());
		member.setNext_grade_point(memberGrade2.getGrade_point());
		if(StringUtils.isEmpty(member.getSource())){
			member.setSource("register");
		}
		memberMapper.insert(member);
		MemberTokenRel rel = new MemberTokenRel(member.getMember_id(), member.getToken(), token_type);
		memberTokenRelMapper.insert(rel);
	}

	@Override
	public Member findByLoginName(String login_name, String token_type){
		return memberMapper.findByLoginName(login_name, token_type);
	}

	@Override
	public Member selectById(Long member_id) {
		return memberMapper.selectByMemberId(member_id);
	}

	@Override
	public void updatePwd(Long member_id, String new_md5) {
		memberMapper.updatePwd(member_id, new_md5);
	}

	public void updateMember(Member member){
		member.setUpdate_time(new Date());
		memberMapper.updateByPrimaryKey(member);
	}

	@Override
	public void updateEmail(Long member_id, String email) {
		memberMapper.updateEmail(member_id, email);
	}

	@Override
	public void updatePhone(Long member_id, String phone) {
		memberMapper.updatePhone(member_id, phone);
	}

	@Override
	public void updateNickName(Long member_id, String nick_name) {
		memberMapper.updateNickName(member_id, nick_name);
	}

	@Override
	public Integer getCountByOrgId(Long org_id) {
		return memberMapper.getCountByOrgId(org_id);
	}

	@Override
	@Transactional
	public void updateIcon(Long member_id, String icon, String token_type) {
		memberMapper.updateIcon(member_id, icon);
		if(token_type != null && token_type == "2"){
			memberMapper.updateIsSysIcon(member_id, "2");
		}
	}

	@Override
	public void updateIsSysIcon(Long member_id, String is_sys_icon) {
		memberMapper.updateIsSysIcon(member_id, is_sys_icon);
	}

	@Override
	public TbMember foundByToken(String token,String token_type){
		return memberMapper.foundByToken(token,token_type);
	}

}
