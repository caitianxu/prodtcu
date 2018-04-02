package com.cjsz.tech.journal.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.journal.domain.PeriodicalChild;
import com.cjsz.tech.journal.domain.PeriodicalRepo;
import com.cjsz.tech.journal.provider.PeriodicalRepoProvider;
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
public interface PeriodicalRepoMapper extends BaseMapper<PeriodicalRepo> {

    @Select("select * from periodical_repo where file_name = #{0} or periodical_url like concat('%/',#{0})")
    PeriodicalRepo findByFileName(String perioical_name);

    @Select("select * from periodical_repo where periodical_cat_id in (${cat_ids})")
    List<PeriodicalRepo> selectByCatIds(@Param("cat_ids") String catIds);

    @Update("update periodical_repo set headline = 2, update_time = now() where periodical_cat_id = #{0} and headline = 1")
    void changeNotHeadLineByCatId(Long periodical_cat_id);

    @SelectProvider(type = PeriodicalRepoProvider.class, method = "getAllPeriodicalList")
    List<PeriodicalRepo> getAllPeriodicalList(@Param("catPath") String catPath, @Param("searchText") String searchText);

    @SelectProvider(type = PeriodicalRepoProvider.class, method = "getOrgPeriodicalList")
    List<PeriodicalRepo> getOrgPeriodicalList(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText);

    @Select("select count(*) as num from periodical_repo p left join periodical_cat_org_rel r on p.periodical_cat_id = r.periodical_cat_id" +
            "  where r.org_id =#{0} and ( p.update_time >#{0} or  r.update_time >#{0} ) ")
    public Integer checkOffLineNum(Long org_id, String timev);

    @Select(" select p.*  from periodical_repo p LEFT JOIN periodical_cat_org_rel r on p.periodical_cat_id = r.periodical_cat_id " +
            " where  r.org_id = #{0}  and p.series_name is not null " +
            " and p.periodical_cat_id is not null and p.periodical_status = 2 and r.is_delete =2 and ( " +
            " p.update_time >#{1} or p.upload_time >#{1} or" +
            " r.create_time >#{1} or r.update_time >#{1}) order by p.order_weight desc" +
            " limit #{2}, #{3}")
    public List<PeriodicalRepo> getOffLineNumList(Long orgid, String timev, Integer pageNum, Integer pageSize);

    @Select("select * from periodical_child where periodical_id = #{0} ")
    List<PeriodicalChild> getImg(Long periodical_id);

    @Select("select periodical_cat_id from periodical_repo where periodical_id = #{0} limit 1")
    Long getCatId(Long periodical_id);

    @Select("select p.* from periodical_cat_org_rel r left join periodical_cat c " +
            "on r.periodical_cat_id = c.periodical_cat_id LEFT JOIN periodical_repo p " +
            "on p.periodical_cat_id=r.periodical_cat_id where r.is_delete =2  and r.org_id = #{1} " +
            " and  c.enabled=1 and p.periodical_id != #{0} and p.periodical_cat_id = #{2} GROUP BY p.periodical_id ORDER BY p.order_weight limit #{3}")
    List<PeriodicalRepo> findSameCatperiodicals(Long periodical_id,Long org_id,Long periodical_cat_id,Integer limit);

    //获取系列期刊（前台）
    @SelectProvider(type = PeriodicalRepoProvider.class, method = "getWebPeriodicalList")
    List<PeriodicalRepo> getWebPeriodicalList(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText);

    //获取系列期刊（后台）
    @SelectProvider(type = PeriodicalRepoProvider.class, method = "getPeriodicalList")
    List<PeriodicalRepo> getPeriodicalList(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText);

    @Select("select series_name from periodical_repo where periodical_id = #{0}")
    String selectSeriesNameById(Long periodical_id);

    //获取同系列期刊列表
    @Select("select DISTINCT pr.*, pc.periodical_cat_name, pc.periodical_cat_path " +
            "from periodical_repo pr " +
            "LEFT JOIN periodical_cat pc ON pr.periodical_cat_id = pc.periodical_cat_id " +
            "LEFT JOIN periodical_cat_org_rel cor ON  cor.periodical_cat_id = pc.periodical_cat_id " +
            "WHERE cor.is_delete=2 and pc.enabled=1 and pr.enabled = 1  and cor.org_id = #{1} and pr.series_name = #{0} and " +
            " pr.periodical_cat_id is not null order by pr.periodical_name desc ")
    List<PeriodicalRepo> getSuggestList(String series_name,Long org_id);


    //获取同系列期刊列表(后台)
    @Select("select DISTINCT pr.*, pc.periodical_cat_name, pc.periodical_cat_path " +
            "from periodical_repo pr " +
            "LEFT JOIN periodical_cat pc ON pr.periodical_cat_id = pc.periodical_cat_id " +
            "LEFT JOIN periodical_cat_org_rel cor ON  cor.periodical_cat_id = pc.periodical_cat_id " +
            "WHERE cor.is_delete=2 and pc.enabled=1  and cor.org_id = #{1} and pr.series_name = #{0}  " +
            "order by pr.periodical_name desc ")
    List<PeriodicalRepo> getSameList(String series_name,Long org_id);

    @Select("SELECT pr.*, pc.periodical_cat_name, pc.periodical_cat_path from periodical_repo pr LEFT JOIN periodical_cat pc ON pr.periodical_cat_id = pc.periodical_cat_id " +
            " where pr.series_name = #{0} ORDER BY pr.periodical_name desc")
    List<PeriodicalRepo> getSameBySNameAll(String series_name);

    @Select("select * from ( select p.* from periodical_repo p left join periodical_cat c on p.periodical_cat_id=c.periodical_cat_id " +
            "where p.periodical_cat_id in(select periodical_cat_id from periodical_cat_org_rel where org_id = #{0} " +
            " and is_delete = 2 and is_show = 1) and c.enabled = 1 and p.enabled = 1 and c.periodical_cat_path like concat(#{1},'%')" +
            " order by p.periodical_name desc ) aaa" +
            " GROUP BY series_name order by order_weight desc limit #{2}")
    public List<Map<String,Object>> selectRecommendListByCount(Long org_id,String periodical_cat_path,Integer limit);



    @Select("select count(*) from periodical_repo where periodical_cat_id in( " +
            "select r.periodical_cat_id from periodical_cat_org_rel r " +
            "left join periodical_cat c on r.periodical_cat_id = c.periodical_cat_id " +
            "where r.org_id = #{0} and r.is_delete = 2 and  c.is_delete = 2)")
    Integer getCount(long org_id);

    @Select("select count(*) from periodical_repo where periodical_cat_id in( " +
            "select r.periodical_cat_id from periodical_cat_org_rel r " +
            "left join periodical_cat c on r.periodical_cat_id = c.periodical_cat_id " +
            "where r.org_id = #{0} and r.is_delete = 2 and c.enabled =1 and c.is_delete = 2)")
    Integer getCountByOrgId(long org_id);

    @Update("update periodical_repo set periodical_status = #{0}, total_page = #{1}, update_time = now() where periodical_id = #{2}")
    void updateRepoStatusAndPages(Integer status, Integer page, Long periodical_id);

    @Update("update periodical_repo set periodical_cat_id = #{0}, update_time = now() where series_name = #{1}")
    void setPeriodicalAllToCat(Long periodical_cat_id, String serises_name);

    @Update("update periodical_repo set periodical_remark = #{0}, update_time = now() where series_name = #{1}")
    void setPeriodicalAllToRemark(String periodical_remark, String serises_name);

    @Update("update periodical_repo set enabled = #{0},publish_time = now(),update_time = now() where series_name  = #{1} ")
    void updateEnabledAndTime(Integer enabled,String series_name);

    @Update("update periodical_repo set enabled = #{0},update_time = now() where series_name  = #{1}")
    void updateEnabled(Integer enabled,String series_name);

    @Select("select * from periodical_repo where series_name = #{0} limit 1")
    PeriodicalRepo selectBySeriesName(String series_name);

    @Select("select * from ( select p.* from periodical_repo p where p.periodical_cat_id in(select periodical_cat_id from" +
            " periodical_cat_org_rel where org_id = #{0}  and is_delete = 2 and is_show = 1)  and p.enabled = 1 " +
            " order by p.periodical_name desc ) aaa GROUP BY series_name order by create_time desc limit #{1}")
    List<Map<String,Object>> selectNewCatPerList(Long org_id, Integer count);
}
