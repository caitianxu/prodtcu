package com.cjsz.tech.journal.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.journal.domain.Newspaper;
import com.cjsz.tech.journal.provider.NewspaperProvider;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Jason on 16/3/3.
 */
public interface NewspaperMapper extends BaseMapper<Newspaper> {

    @Select("select * from newspaper where newspaper_id = #{0} ")
    Newspaper selectById(Long newspaper_id);

    @SelectProvider(type = NewspaperProvider.class, method = "pageQuery")
    List<Newspaper> pageQuery(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText);

    @SelectProvider(type = NewspaperProvider.class, method = "pageSiteQuery")
    List<Newspaper> pageSiteQuery(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText);

    @Update("update  newspaper set is_delete = 1,update_time = now() where newspaper_id in (${newspaper_ids_str})")
    void deleteByNewspaperIds(@Param("newspaper_ids_str") String newspaper_ids_str);

    @Update("update  newspaper set is_delete = 1,update_time = now() where newspaper_cat_id in (${cat_ids})")
    void deleteByCatIds(@Param("cat_ids") String cat_ids);

    @Select("select * from newspaper where is_delete = 2 and newspaper_cat_id in (${cat_ids}) order by order_weight desc, create_time desc")
    List<Newspaper> selectByCatIds(@Param("cat_ids") String cat_ids);

    @Update("update newspaper set is_recommend = #{0}, update_time = now() where newspaper_id = #{1}")
    void updateStatus(Integer is_recommend, Long newspaper_id);

    @Select("select count(*) as num from newspaper where (org_id=#{0} or org_id = 1) and  update_time >#{1}")
    Integer checkOffLineNum(Long org_id, String update_time);

    @Select("select *,GROUP_CONCAT(newspaper_cat_name) \n" +
            "as newspaper_cat_name2 from (select DISTINCT a.*,c.newspaper_cat_name,c.newspaper_cat_path from  newspaper a \n" +
            "LEFT JOIN newspaper_cat c ON a.org_id=c.org_id and a.newspaper_cat_id=c.newspaper_cat_id \n" +
            "LEFT JOIN newspaper_cat_org_rel r ON a.org_id=r.org_id \n" +
            "WHERE a.newspaper_cat_id in(SELECT newspaper_cat_id FROM newspaper_cat_org_rel \n" +
            "WHERE org_id=#{0} and is_delete=2  ) and c.enabled=1 and(\n" +
            " r.update_time >#{1} or\n" +
            " a.update_time >#{1} or\n" +
            " c.update_time >#{1} )\n" +
            ") x1 group by newspaper_id,org_id,user_id,paper_name,paper_url,paper_img_small,paper_img,order_weight,create_time,update_time order by order_weight desc" +
            " limit #{2}, #{3}")
    List<Newspaper> getOffLineNumList(Long org_id, String update_time, Integer pageNum, Integer pageSize);

    @Select("select count(*) from newspaper  where is_delete = 2 and newspaper_cat_id in(\n" +
            "select r.newspaper_cat_id from newspaper_cat_org_rel r \n" +
            "left join newspaper_cat c on r.newspaper_cat_id = c.newspaper_cat_id\n" +
            "where r.org_id = #{0} and r.is_delete = 2 and c.is_delete = 2)")
    Integer getCount(Long org_id);

    @Select("select count(*) from newspaper  where is_delete = 2 and newspaper_cat_id in(\n" +
            "select r.newspaper_cat_id from newspaper_cat_org_rel r \n" +
            "left join newspaper_cat c on r.newspaper_cat_id = c.newspaper_cat_id\n" +
            "where r.org_id = #{0} and r.is_delete = 2 and c.enabled = 1 and c.is_delete = 2)")
    Integer getCountByOrgId(Long org_id);


    @Select("select * from newspaper n left join newspaper_cat c on n.newspaper_cat_id = c.newspaper_cat_id where n.is_delete = 2 and n.newspaper_cat_id in(select newspaper_cat_id from newspaper_cat_org_rel " +
            " where org_id = #{0} and is_delete = 2 and is_show = 1) and c.newspaper_cat_path like concat(#{1} , '%') " +
            " order by  n.order_weight desc limit #{2}")
    List<Map<String,Object>> selectRecommendListByCount(Long org_id,String newspaper_cat_path,Integer limit);

    @Select("select newspaper_cat_id from newspaper where newspaper_id = #{0}")
    Long getNewsCatId(Long newspaper_id);

    @Update("update newspaper set order_weight = #{1} , update_time = now() where newspaper_id = #{0}")
    void updateOrder(Long newspaper_id, Long order_weight);
}
