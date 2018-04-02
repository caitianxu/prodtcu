package com.cjsz.tech.count.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.count.domain.SearchCount;
import com.cjsz.tech.count.provider.SearchCountProvider;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by LuoLi on 2017/3/23 0023.
 */
public interface SearchCountMapper extends BaseMapper<SearchCount> {

    @Select("select * from search_count where org_id = #{0}")
    public List<SearchCount> getList(Long org_id);

    @Select("select * from search_count where org_id = #{1} name like concat('%',#{0},'%') or org_name like concat('%',#{0},'%')")
    public List<SearchCount> getListBySearchText(String searchText,Long org_id);

    @SelectProvider(type = SearchCountProvider.class, method = "searchCountList")
    List<SearchCount> searchCountList(@Param("org_id") Long org_id,@Param("searchText") String searchText);


    @Delete("delete from search_count where search_id in(${search_ids})")
    public void deleteByIds(@Param("search_ids") String search_ids);

    @Select("select * from search_count where name = #{0} and org_id = #{1}  limit 1")
    public SearchCount selectByNameAndOrgId(String searchText, Long org_id);

    @Select("select * from search_count where search_id = #{0}")
    public SearchCount selectById(Long search_id);

    @Select("select * from search_count where org_id = #{0} and status = 1 order by search_count desc limit #{1}")
    List<SearchCount> selectByOrgIdAndCount(Long org_id, Integer count);

    @Select("select * from search_count where org_id = #{0} and status = 1 and search_count >10 order by search_count desc limit #{1}")
    List<Map<String, Object>> selectByOrgId(Long org_id, Integer count);


    @Select("select * from search_count where org_id = #{0} and status = 1 and search_count >10 order by search_count desc limit #{1}")
    List<SearchCount> selectByOrgIdAndCount_v2(Long org_id, Long display);

    @Select("select max(search_count) from search_count where org_id = 189 ")
    Long selectMostCount();
}
