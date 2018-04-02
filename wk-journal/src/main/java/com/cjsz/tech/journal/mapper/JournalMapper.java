package com.cjsz.tech.journal.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.journal.domain.Journal;
import com.cjsz.tech.journal.provider.JournalProvider;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
public interface JournalMapper extends BaseMapper<Journal> {

    //分页查询本机构的期刊
    @SelectProvider(type = JournalProvider.class, method = "getJournalList")
    List<Journal> getJournalList(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText);

    @SelectProvider(type = JournalProvider.class, method = "sitePageQuery")
    List<Journal> sitePageQuery(@Param("org_id") Long org_id, @Param("catPath") String catPath);

    @Select("select * from journal where journal_id = #{0} ")
    public Journal selectById(Long journal_id);

    @Delete("delete from journal where journal_id in (${journal_ids_str})")
    public void deleteByJournalIds(@Param("journal_ids_str") String journal_ids_str);

    @Delete("delete from journal where journal_cat_id in (${cat_ids})")
    public void deleteByCatIds(@Param("cat_ids") String cat_ids);

    @Select("select * from journal where journal_cat_id in (${cat_ids})")
    public List<Journal> selectByCatIds(@Param("cat_ids") String cat_ids);

    @Select("select count(*) as num from journal where org_id=#{0} and  update_time >#{1} ")
    public Integer checkOffLineNum(Long orgid, String timev);

    @Select("select *,GROUP_CONCAT(journal_cat_name) \n" +
            "as journal_cat_name2 from (select DISTINCT a.*,c.journal_cat_name,c.journal_cat_path from  journal a \n" +
            "LEFT JOIN journal_cat c ON a.org_id=c.org_id and a.journal_cat_id=c.journal_cat_id \n" +
            "LEFT JOIN journal_cat_org_rel r ON a.org_id=r.org_id \n" +
            "WHERE a.journal_cat_id in(SELECT journal_cat_id FROM journal_cat_org_rel \n" +
            "WHERE org_id=#{0} and is_delete=2 ) and c.enabled=1 and(\n" +
            " r.update_time >#{1} or\n" +
            " a.update_time >#{1} or\n" +
            " c.update_time >#{1} )\n" +
            ") x1 group by journal_id,org_id,user_id,journal_name,journal_cover,journal_cover_small,order_weight,create_time,update_time order by order_weight desc" +
            " limit #{2}, #{3}")
    public List<Journal> getOffLineNumList(Long orgid, String timev, Integer pageNum, Integer pageSize);

    @Update("update journal set headline = 2, update_time = now() where journal_cat_id = #{0} and headline = 1")
    public void setNotHeadLineByCatId(Long journal_cat_id);

    @Select("select * from journal " +
            "where journal_cat_id in(select journal_cat_id from journal_cat_org_rel where org_id = #{0} and is_delete = 2 and is_show = 1) and journal_status = 1 " +
            "order by recommend asc, order_weight desc limit #{1}")
    public List<Map<String,Object>> selectRecommendListByCount(Long org_id, Integer count);

    @Select("select count(*) from journal where journal_cat_id in( " +
            "select r.journal_cat_id from journal_cat_org_rel r " +
            "left join journal_cat c on r.journal_cat_id = c.journal_cat_id " +
            "where r.org_id = #{0} and r.is_delete = 2 and c.enabled =1 and c.is_delete = 2)")
    Integer getCountByOrgId(long org_id);

    @Select("select journal_cat_id from journal where journal_id =#{0}")
    Long getCatId(Long journal_id);

}
