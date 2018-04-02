package com.cjsz.tech.journal.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.journal.beans.NewspaperCatBean;
import com.cjsz.tech.journal.domain.NewspaperCatOrgRel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by LuoLi on 2017/4/24 0024.
 */
public interface NewspaperCatOrgRelMapper extends BaseMapper<NewspaperCatOrgRel> {

    //关系表分类表联合详细分类信息
    @Select("select r.*, c.pid, c.newspaper_cat_name, c.newspaper_cat_path, c.enabled, c.remark, c.is_delete, c.org_count from newspaper_cat_org_rel r " +
            "left join newspaper_cat c on r.newspaper_cat_id = c.newspaper_cat_id " +
            "where r.org_id = #{0} and r.newspaper_cat_id = #{1}")
    public NewspaperCatBean seletInfoByOrgIdAndCatId(Long org_id, Long newspaper_cat_id);

    //关系Id查询关系
    @Select("select * from newspaper_cat_org_rel where rel_id = #{0}")
    public NewspaperCatOrgRel selectById(Long rel_id);

    //更新排序
    @Update("update newspaper_cat_org_rel set order_weight = #{0},update_time = now() where rel_id = #{1}")
    public void updateOrderById(Long order_weight, Long rel_id);

    //机构Id分类Id查询关系
    @Select("select * from newspaper_cat_org_rel where org_id = #{0} and newspaper_cat_id = #{1}")
    public NewspaperCatOrgRel selectByOrgIdAndCatId(Long org_id, Long newspaper_cat_id);

    @Update("update newspaper_cat_org_rel set is_delete = #{1}, update_time = now() where rel_id = #{0} and is_delete != #{1} ")
    public void updateIsDelete(Long rel_id, Integer is_delete);

    @Update("update newspaper_cat_org_rel set is_delete = 1, update_time = now() where newspaper_cat_id in (${cat_ids}) ")
    public void deleteRelsByIds(@Param("cat_ids") String cat_ids);

    @Select("select * from newspaper_cat_org_rel where newspaper_cat_id = #{0}")
    public List<NewspaperCatOrgRel> selectListByCatId(Long cat_id);

    @Update("update newspaper_cat_org_rel set order_weight = #{1} , update_time = now() where newspaper_cat_id = #{0} and org_id = #{2}")
    void orderByOrg(Long newspaper_cat_id, Long order_weight, Long org_id);
}
