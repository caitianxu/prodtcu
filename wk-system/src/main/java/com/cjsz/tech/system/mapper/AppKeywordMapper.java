package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.domain.AppKeyword;
import com.cjsz.tech.system.domain.AppNav;
import com.cjsz.tech.system.provider.AppNavProvider;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public interface AppKeywordMapper extends BaseMapper<AppKeyword> {

    @Select("select * from app_keyword")
    public List<AppKeyword> getAppKeywordList();

    @Select("select * from app_keyword where keyword_name like concat('%',#{0},'%') ")
    public List<AppKeyword> getAppKeywordBySearch(String searchText);

    @Select("select * from app_keyword where keyword_id = #{0}")
    public AppKeyword selectByKeywordId(Long keyword_id);

    @Delete("delete from app_keyword where keyword_id in(${keyword_ids_str})")
    public void deleteByKeywordIds(@Param("keyword_ids_str") String keyword_ids_str);
}
