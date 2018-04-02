package com.cjsz.tech.journal.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.journal.beans.FindNewCatOrgBean;
import com.cjsz.tech.journal.beans.PeriodicalCatBean;
import com.cjsz.tech.journal.beans.PeriodicalCatOrgBean;
import com.cjsz.tech.journal.domain.PeriodicalCat;
import com.cjsz.tech.journal.provider.PeriodicalCatProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface PeriodicalCatMapper extends BaseMapper<PeriodicalCat> {

    @Select("select * from periodical_cat where periodical_cat_name = #{0} and is_delete = 2 ")
    List<PeriodicalCat> selectByCatName(String periodical_cat_name);

    //更新层次路径
    @Update("update periodical_cat set periodical_cat_path = #{1}, update_time = now() where periodical_cat_id = #{0}")
    void updateCatPathByCatId(Long cat_id, String cat_path);

    //分类名称重复(分类名查找不包括本身)
    @Select("select * from periodical_cat where periodical_cat_name = #{0} and periodical_cat_id != #{1} and is_delete = 2")
    List<PeriodicalCat> selectOtherByCatName(String cat_name, Long cat_id);

    //将层次路径中包含old_full_path的，更新为new_full_path
    @Update("update periodical_cat set periodical_cat_path = REPLACE(periodical_cat_path,#{0},#{1}), update_time = now() " +
            "where periodical_cat_path like concat(#{0},'%')")
    void updateFullPath(String old_full_path, String new_full_path);

    //通过cat_ids找到full_paths
    @Select("select periodical_cat_path from periodical_cat where periodical_cat_id in(${cat_ids})")
    List<String> getFullPathsByCatIds(@Param("cat_ids") String cat_ids);

    //通过full_paths找到journal_cat_ids
    @Select("select periodical_cat_id from periodical_cat where periodical_cat_path like '${full_path}%'")
    List<Long> getCatIdsByFullPath(@Param("full_path") String full_path);

    @Update("update periodical_cat set is_delete = 1, update_time = now() where periodical_cat_id in (${cat_ids}) ")
    void deleteJournalCatsByIds(@Param("cat_ids") String cat_ids);

    @Update("update periodical_cat set enabled = 1, update_time = now() " +
            "where enabled = 2 and periodical_cat_id in(${cat_ids})")
    void updateEnabledByCatIds(@Param("cat_ids") String cat_ids);

    //不启用
    @Update("update periodical_cat set enabled = 2, update_time = now() " +
            "where enabled = 1 and periodical_cat_path like concat('${cat_path}','%')")
    void updateEnabledByCatPath(@Param("cat_path") String cat_path);

    @SelectProvider(type = PeriodicalCatProvider.class, method = "getCatsByOrgId")
    List<PeriodicalCatBean> getCatsByOrgId(@Param("org_id") Long org_id);

    @Update("update periodical_cat set org_count = (org_count + #{1}), update_time = now() where periodical_cat_id in (${cat_ids})")
    void updateOrgCount(@Param("cat_ids") String cat_ids, Integer count);

    @SelectProvider(type = PeriodicalCatProvider.class, method = "getOrgQuery")
    List<PeriodicalCatOrgBean> getOrgQuery(@Param("bean") FindNewCatOrgBean bean);

    @Select("select  bc.*,r.is_show,r.order_weight as org_order_weight,r.is_delete as org_is_delete from periodical_cat bc LEFT JOIN periodical_cat_org_rel r on bc.periodical_cat_id = r.periodical_cat_id " +
            "where r.org_id =#{0}  and (bc.update_time>#{1} or bc.create_time>#{1} or r.create_time>#{1} or r.update_time>#{1}) order by r.order_weight desc " +
            " limit #{2}, #{3}")
    List<PeriodicalCat> getOffLineNumList(Long org_id,String timev, Integer pageNum, Integer pageSize);

    @Select("select count(*) as num from periodical_cat bc left join periodical_cat_org_rel r on bc.periodical_cat_id = r.periodical_cat_id" +
            "  where r.org_id = #{0} bc.update_time > #{1} ")
    Integer checkOffLineNum(Long org_id ,String timev);

    @SelectProvider(type = PeriodicalCatProvider.class, method = "getCatTree")
    List<PeriodicalCatBean> getCatTree(@Param("org_id") Long org_id);

    @Select("select c.* from periodical_cat_org_rel r " +
            "left join periodical_cat c on c.periodical_cat_id = r.periodical_cat_id " +
            "where r.org_id = #{0} and c.periodical_cat_pid = 0 and c.enabled = 1 and r.is_show = 1 " +
            " and c.is_delete = 2 and r.is_delete = 2 ORDER BY r.order_weight desc ")
    public List<PeriodicalCat> selectSiteCatsByOrgId(Long org_id);

    @Select("select c.* from periodical_cat_org_rel r " +
            "left join periodical_cat c on c.periodical_cat_id = r.periodical_cat_id " +
            "where r.org_id = #{0} and c.periodical_cat_pid = 0 and c.enabled = 1 and" +
            " r.is_show = 1 and c.is_delete = 2 and r.is_delete = 2 order by r.order_weight desc  ")
    List<Map<String, Object>> selectFirstCatsByOrgIdAndCount(Long org_id);

    @Select("select r.rel_id, r.org_id, r.periodical_cat_id , r.order_weight, r.is_show, r.create_time, r.update_time, r.is_delete, c.periodical_cat_pid , " +
            "  c.periodical_cat_name , c.periodical_cat_path , c.enabled, c.remark, c.org_count from periodical_cat_org_rel r " +
            "  left join periodical_cat c on r.periodical_cat_id = c.periodical_cat_id where c.is_delete = 2 and r.is_show = 1 and r.is_delete = 2 and c.enabled = 1 and r.org_id = #{0} " +
            " order by length(c.periodical_cat_path)-length(replace(c.periodical_cat_path,'|','')) asc, r.order_weight desc")
    List<PeriodicalCatBean> getAllCats(Long org_id);
}
