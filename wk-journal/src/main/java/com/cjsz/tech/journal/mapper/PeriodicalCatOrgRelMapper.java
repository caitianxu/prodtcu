package com.cjsz.tech.journal.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.journal.domain.PeriodicalCatOrgRel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by LuoLi on 2017/4/24 0024.
 */
public interface PeriodicalCatOrgRelMapper extends BaseMapper<PeriodicalCatOrgRel> {

    //更新排序
    @Update("update periodical_cat_org_rel set order_weight = #{0},update_time = now() where rel_id = #{1}")
    void updateOrderWeightById(Long order_weight, Long rel_id);

    @Update("update periodical_cat_org_rel set is_delete = 1, update_time = now() where periodical_cat_id in (${cat_ids}) ")
    void deleteRelsByCatIds(@Param("cat_ids") String cat_ids);

    @Update("update periodical_cat_org_rel set is_show = 1, update_time = now() " +
            "where is_show = 2 and org_id = #{0} and periodical_cat_id in (${cat_ids})")
    void updateShowByCatIds(Long org_id, @Param("cat_ids") String cat_ids);

    @Update("update periodical_cat_org_rel set is_show = 2, update_time = now() " +
            "where is_show = 1 and org_id = #{0} and periodical_cat_id in " +
            "(select periodical_cat_id from periodical_cat " +
            "where periodical_cat_path like concat('${cat_path}','%'))")
    void updateShowByCatPath(Long org_id, @Param("cat_path") String cat_path);

    @Update("update periodical_cat_org_rel set is_show = 2, update_time = now() " +
            "where is_show = 1 and periodical_cat_id in " +
            "(select periodical_cat_id from periodical_cat " +
            "where org_id = #{org_id} and periodical_cat_path like concat('${cat_path}','%'))")
    void updateAllShowByCatPath(@Param("cat_path") String cat_path,@Param("org_id") Long org_id);

    //机构Id分类Id查询关系
    @Select("select * from periodical_cat_org_rel where org_id = #{0} and periodical_cat_id = #{1} limit 1")
    PeriodicalCatOrgRel selectByOrgIdAndCatId(Long org_id, Long cat_id);

    @Update("update periodical_cat_org_rel set is_delete = #{1}, update_time = now() " +
            "where rel_id in (${rel_ids}) and is_delete != #{1} ")
    void updateIsDelete(@Param("rel_ids") String rel_ids, Integer status);

    @Update("update periodical_cat_org_rel set order_weight = #{1},update_time = now() where periodical_cat_id = #{0} and org_id = #{2}")
    void orderByOrg(Long periodical_cat_id, Long order_weight,Long org_id);
}
