package com.cjsz.tech.meb.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.meb.domain.MemberTokenRel;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by LuoLi on 2017/4/26 0026.
 */
public interface MemberTokenRelMapper extends BaseMapper<MemberTokenRel> {

    @Update("update member_token_rel set token = #{1} where  member_id = #{0} and token_type = #{2}")
    void updateMemberToken(Long id, String token, String token_type);

    @Select("select * from member_token_rel where member_id = #{0} and token_type = #{1}")
    MemberTokenRel findByMemberIdAndType(Long id, String token_type);
}
