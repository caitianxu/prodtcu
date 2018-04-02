package com.cjsz.tech.web.service;

import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.utils.PasswordUtil;
import com.github.pagehelper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by pc on 03/30/2017.
 */
@Service
public class OauthOpenService {
    @Autowired
    MemberService memberService;
    public Map<String, Object> login(String btype, String openid, String nick_name, String icon, String org_id, String token_type){
        if(StringUtil.isEmpty(org_id)){
            return null;
        }
        if(StringUtil.isEmpty(btype)){
            return null;
        }
        if(StringUtil.isEmpty(openid)){
            return null;
        }
        Member member = memberService.findByOpenId(btype, openid, token_type);
        if(member!=null) {
            Hashtable<String,Object> data = new Hashtable<String,Object>();
            String rand = PasswordUtil.getRandomNum();
            String token = PasswordUtil.entryptPassword(openid + "$" + rand);
            memberService.updateMemberToken(member.getMember_id(), token, "1");
            data.put("member_token", token);
            Member memberInfo = memberService.getMemberInfoByMemberId(member.getMember_id(), token_type);
            data.put("memberInfo", memberInfo);
            return data;
        }else {
            member = new Member();
            member.setNick_name(nick_name);
            member.setSource(btype);
            member.setOpenid(openid);
            member.setOrg_id(Long.valueOf(org_id));
            member.setIcon(icon);

            String rand = PasswordUtil.getRandomNum();
            String token = PasswordUtil.entryptPassword(openid + "$" + rand);
            member.setToken(token);
            member.setIs_sys_icon(1);//是否使用系统头像(1:是;2:否)
            memberService.saveMember(member, token_type);

            Hashtable<String,Object> data = new Hashtable<String,Object>();
            data.put("member_token",token);
            Member memberInfo = memberService.getMemberInfoByMemberId(member.getMember_id(), "1");
            data.put("memberInfo", memberInfo);
            return data;
        }
    }

}
