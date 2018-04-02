package com.cjsz.tech.meb.mapper;

import java.util.List;

import com.cjsz.tech.meb.domain.TbMember;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.provider.MemberProvider;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface MemberMapper extends BaseMapper<Member> {

	@SelectProvider(type = MemberProvider.class, method = "selectAll")
	List<Member> getMemberList(@Param("keyword") String keyword, @Param("org_id") Long org_id);

	@Update("update member set enabled = #{enabled}, update_time = now() where member_id in (${memberIds})")
	void updateMemberStatus(@Param("enabled") Integer enabled, @Param("memberIds") String memberIds);

	@Select("select m.*,r.token from member m " +
			"left join member_token_rel r on m.member_id = r.member_id where r.token_type = #{1} and r.token = #{0}")
	Member findByToken(String token, String token_type);

	@Select("select m.*, r.token, mg.grade_icon member_header, mg.grade_title, mg.grade_name from member m " +
			"left join member_token_rel r on r.member_id = m.member_id " +
			"left join member_grade mg on m.grade_id = mg.grade_id " +
			"where r.token = #{0} and r.token_type = #{1}")
	Member getMemberInfoByToken(String member_token, String token_type);

	@Select("select m.*, r.token from member m " +
			"left join (select * from member_token_rel where token_type = #{2}) r on r.member_id = m.member_id " +
			"where m.source = #{0} and m.openid = #{1}")
	Member findByOpenId(String btype, String openid, String token_type);

	@Select("select m.*,r.token, mg.grade_icon member_header, mg.grade_title, mg.grade_name from member m " +
			"left join member_grade mg on m.grade_id = mg.grade_id " +
			"left join member_token_rel r on r.member_id = m.member_id " +
			"where m.member_id = #{0} and r.token_type = #{1}")
	Member getMemberInfoByMemberId(Long member_id, String token_type);

	@Select("select m.*,r.token from member m " +
			"left join (select * from member_token_rel where token_type = #{1}) r on r.member_id = m.member_id " +
			"where (m.phone = #{0} or m.email = #{0}) and m.is_delete = 2 limit 1")
	Member findByLoginName(String login_name, String token_type);

	@Select("select * from member where member_id = #{0}")
	Member selectByMemberId(Long member_id);

	@Update("update member set member_pwd = #{1}, update_time = now() where member_id = #{0}")
	void updatePwd(Long member_id, String new_md5);

	@Update("update member set email = #{1}, update_time = now() where member_id = #{0}")
	void updateEmail(Long member_id, String email);

	@Update("update member set nick_name = #{1}, update_time = now() where member_id = #{0}")
	void updateNickName(Long member_id, String nick_name);

	@Update("update member set icon = #{1}, update_time = now() where member_id = #{0}")
	void updateIcon(Long member_id, String icon);

	@Update("update member set is_sys_icon = #{1}, update_time = now() where member_id = #{0}")
	void updateIsSysIcon(Long member_id, String is_sys_icon);

	@Update("update member set phone = #{1}, update_time = now() where member_id = #{0}")
	void updatePhone(Long member_id, String phone);

	@Select("SELECT count(*) FROM `member` where org_id = #{0} and is_delete = 2 ;")
	Integer getCountByOrgId(Long org_id);

	@Select("select * from tb_member where token=#{0} AND token_type=#{1}")
	TbMember foundByToken(String token, String token_type);
}
