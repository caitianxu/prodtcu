package com.cjsz.tech.dev.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.domain.AppType;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
public interface AppTypeMapper extends BaseMapper<AppType>{

    //获取app类型列表
    @Select("select * from app_type where is_delete = 2")
    List<AppType> getList();

    @Update("update app_type set is_delete = 1 where app_type_id in (${ids})")
    void deleteByIds(@Param("ids") String ids);
}
