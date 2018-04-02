package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
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
public interface AppNavMapper extends BaseMapper<AppNav> {

    @SelectProvider(type = AppNavProvider.class, method = "getAppNavList")
    public List<AppNav> getAppNavList(@Param("searchText") String searchText, @Param("org_id") Long org_id);

    @Select("select * from app_nav where nav_id = #{0}")
    public AppNav selectByNavId(Long nav_id);

    @Delete("delete from app_nav where nav_id in(${nav_ids_str})")
    public void deleteByNavIds(@Param("nav_ids_str") String nav_ids_str);
}
