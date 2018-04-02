package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.domain.RecommendBooks;
import com.cjsz.tech.book.domain.RecommendCat;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Administrator on 2017/9/5 0005.
 */
public interface RecommendCatMapper extends BaseMapper<RecommendCat> {

    @Update("update recommend_cat set is_delete = 1 where recommend_cat_id in (${ids})")
    void deleteCat(@Param("ids") String ids);

    @Select("select * from recommend_cat where is_delete = 2 ")
    List<RecommendCat> getList();

    @Select("select * from recommend_cat where recommend_code = #{0} limit 1 ")
    RecommendCat selectByCode(Long recommend_code);

    @Select("select * from recommend_cat where recommend_type_name = #{0} and is_delete = 2 limit 1 ")
    RecommendCat selectByName(String recommend_type_name);

    @Select("select * from recommend_cat where recommend_code = #{0} and recommend_cat_id != #{1} limit 1 ")
    RecommendCat selectByCodeAndId(Long recommend_code, Long cat_id);

    @Select("select * from recommend_cat where recommend_type_name = #{0} and recommend_cat_id != #{1} limit 1 ")
    RecommendCat selectByNameAndId(String recommend_type_name, Long recommend_cat_id);
}
