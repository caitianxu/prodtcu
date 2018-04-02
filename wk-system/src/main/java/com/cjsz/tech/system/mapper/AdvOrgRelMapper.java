package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.domain.AdvOrgRel;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by Administrator on 2017/9/20 0020.
 */
public interface AdvOrgRelMapper extends BaseMapper<AdvOrgRel> {

    @Update("update adv_org_rel set is_delete = #{1} , update_time = now() where rel_id = #{0} and is_delete != #{1}")
    void updateIsDelete(Long rel_id, Integer is_delete);

    @Select("select * from adv_org_rel where org_id = #{0} and adv_id = #{1}")
    AdvOrgRel selectByOrgIdAndAdvId(Long org_id, Long adv_id);

    @Update("update adv_org_rel set is_show = #{2},update_time = now() where org_id =#{1} and adv_id = #{0} ")
    void updateIsShow(Long adv_id, Long org_id, Integer is_show);

    @Select("select count(*) from adv_org_rel where adv_id = #{0} and is_delete =2")
    Integer selectOrgCount(Long adv_id);

    @Update("update adv_org_rel set order_weight = #{1} where adv_id = #{0} and org_id = #{2}")
    void updateOrder(Long adv_id, Long order_weight, Long org_id);
}
