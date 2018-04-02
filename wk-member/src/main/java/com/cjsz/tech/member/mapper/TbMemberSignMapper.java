package com.cjsz.tech.member.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.member.beans.MemberSignListBean;
import com.cjsz.tech.member.domain.TbMemberSign;
import com.cjsz.tech.member.provider.TbMemberSignprovider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/12 0012.
 */
public interface TbMemberSignMapper extends BaseMapper<TbMemberSign>{

    @SelectProvider(type = TbMemberSignprovider.class,method = "getList")
    List<TbMemberSign> getList(@Param("bean") MemberSignListBean bean);

    @Select("select tm.*,m.nick_name,m.account from tb_member_sign tm left join (select * from  tb_member group by member_id) m on tm.member_id = m.member_id " +
            " where tm.member_id = #{0} and tm.sign_time like concat(#{1},'%') order by tm.sign_time asc ")
    List<TbMemberSign> getMonthList(Long member_id, String date_time);

    @Select("select * from tb_member_sign where  member_id = #{0} and date_format(sign_time, '%Y%m') = date_format(#{1} , '%Y%m') ")
    List<TbMemberSign> monthList(Long member_id, Date sign_time);

    @Select("select * from tb_member_sign where member_id = #{0} and sign_time = #{1} limit 1 ")
    TbMemberSign selectByMemberId(Long member_id, Date sign_time);

    @Update("update tb_member_sign set sign_gift = #{0} where id = #{1}")
    void updateSignGift(String sign_gift, Long id);
}
