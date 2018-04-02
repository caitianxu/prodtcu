package com.cjsz.tech.member.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.provider.MemberProvider;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.provider.UnifyMemberProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Author:Jason
 * Date:2017/6/26
 */
public interface UnifyMemberMapper extends BaseMapper<UnifyMember> {


    @Update("update tb_member set token = #{1} where member_id=#{0} and token_type=#{2}")
    void saveToken(String userId, String token, String token_type);

    @Select("select * from tb_member where token=#{0} and token_type = #{1}")
    UnifyMember findByTokenAndTokenType(String token,String token_type);
    
    @Select("select * from tb_member where member_id=#{0} and token_type=#{1} limit 1")
    UnifyMember findByAccount(Long user_id, String token_type);

    @Select("select m.*,o.org_name from tb_member m left join sys_organization o on o.org_id = m.org_id where token=#{0}")
    UnifyMember findUserByToken(String token);

    @Select("select count(review_id) from book_review where member_id = #{0} and is_delete =2  ")
    Integer getSum(Long member_id);

    @Select("select org_id from sys_org_extend where short_name = 'CJZWW' ")
    Long getOrgId();

    @Update("update tb_member set icon = #{1} where member_id = #{0}")
    void updateIcon(Long member_id,String icon);

    @Update("update tb_member set phone = #{1} where member_id = #{0}")
    void updatePhone(Long member_id, String phone);

    @Update("update tb_member set email = #{1} where member_id = #{0}")
    void updateEmail(Long member_id, String email);

    @Update("update tb_member set org_id = #{1} where member_id = #{0}")
    void updateOrgId(Long member_id,Long org_id);

    @Update("update tb_member set sex = #{1} where member_id = #{0}")
    void updateSex(Long member_id,Integer sex);

    @Update("update tb_member set nick_name = #{1} where member_id = #{0}")
    void updateNickName(Long member_id,String nickName);

    @Update("update tb_member set sign = #{1} where member_id = #{0}")
    void updateSign(Long member_id,String sign);

    @Select("select * from tb_member where member_id=#{0} LIMIT 1")
    List<UnifyMember> selectUser(Long member_id);

    @Select("select sum(total_time) from member_read_record where member_id = #{member_id} and to_days(end_time)=to_days(#{today})")
    Integer getTimes(@Param("member_id") Long member_id,@Param("today") Date today);

    @Select("select total_time from member_read_index where member_id = #{0} limit 1")
    Integer getTime(Long member_id);

    @Select("select count(*) from book_shelf where user_id = #{0} limit 1")
    Integer getNum(Long member_id);

    @Select("select * from tb_member where member_id = #{0} limit 1")
    UnifyMember selectByMemberId(Long member_id);

    @Select("select email from tb_member where email = #{0} limit 1")
    String getMail(String mail);

    @Select("select phone from tb_member where phone = #{0} limit 1")
    String getMob(String mob);

    @Update("update tb_member set token = null where token = #{0}")
    void updateToken(String token);

    @SelectProvider(type = UnifyMemberProvider.class, method = "selectAll")
    List<UnifyMember> getMemberList(@Param("keyword") String keyword, @Param("org_id") Long org_id);

    @Select("select count(*) from (select count(*) from tb_member group by member_id)aaa")
    Integer getCount();

    @Select("select count(*) from (select count(*) from tb_member where org_id = #{0} group by member_id)aaa")
    Integer getCountByOrgId(Long org_id);

    @Select("select rank from \n" +
            "(select (@i := @i + 1) as rank,read_index,member_id from \n" +
            "(SELECT m.*,r.read_index from  tb_member m LEFT JOIN member_read_index r " +
            "on m.member_id = r.member_id where m.org_id = #{1} GROUP BY m.member_id ORDER BY r.read_index desc) a,(select @i:=0)b )c\n" +
            "where member_id = #{0} limit 1 ")
    Integer getMemberRank(Long member_id,Long org_id);
}
