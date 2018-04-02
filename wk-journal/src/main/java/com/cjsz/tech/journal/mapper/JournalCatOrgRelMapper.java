package com.cjsz.tech.journal.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.journal.beans.JournalCatBean;
import com.cjsz.tech.journal.domain.JournalCatOrgRel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by LuoLi on 2017/4/24 0024.
 */
public interface JournalCatOrgRelMapper extends BaseMapper<JournalCatOrgRel> {

    //关系表分类表联合详细分类信息
    @Select("select r.*, c.pid, c.journal_cat_name, c.journal_cat_path, c.enabled, c.remark, c.is_delete, c.org_count from journal_cat_org_rel r " +
            "left join journal_cat c on r.journal_cat_id = c.journal_cat_id " +
            "where r.org_id = #{0} and r.journal_cat_id = #{1}")
    public JournalCatBean seletInfoByOrgIdAndCatId(Long org_id, Long journal_cat_id);

    //关系Id查询关系
    @Select("select * from journal_cat_org_rel where rel_id = #{0}")
    public JournalCatOrgRel selectById(Long rel_id);

    //更新排序
    @Update("update journal_cat_org_rel set order_weight = #{0},update_time = now() where rel_id = #{1}")
    public void updateOrderById(Long order_weight, Long rel_id);

    //机构Id分类Id查询关系
    @Select("select * from journal_cat_org_rel where org_id = #{0} and journal_cat_id = #{1}")
    public JournalCatOrgRel selectByOrgIdAndCatId(Long org_id, Long journal_cat_id);

    @Update("update journal_cat_org_rel set is_delete = #{1}, update_time = now() where rel_id = #{0} and is_delete != #{1} ")
    public void updateIsDelete(Long rel_id, Integer is_delete);

    @Update("update journal_cat_org_rel set is_delete = 1, update_time = now() where journal_cat_id in (${cat_ids}) ")
    public void deleteRelsByIds(@Param("cat_ids") String cat_ids);

    @Select("select * from journal_cat_org_rel where journal_cat_id = #{0}")
    public List<JournalCatOrgRel> selectListByCatId(Long cat_id);
}
