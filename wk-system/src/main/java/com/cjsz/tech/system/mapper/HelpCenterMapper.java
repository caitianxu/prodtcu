package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.domain.HelpCenter;
import com.cjsz.tech.system.domain.Role;
import com.cjsz.tech.system.provider.HelpCenterProvider;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by Administrator on 2017/8/7 0007.
 */
public interface HelpCenterMapper extends BaseMapper<HelpCenter>{

    @SelectProvider(type = HelpCenterProvider.class, method = "getList")
    List<HelpCenter> getList(@Param("searchText") String searchText);

    @Select("select user_real_name from sys_user where user_id = #{0} limit 1 ")
    String selectNameById(Long user_id);

    @Select("select * from help_center where id = #{0} limit 1")
    HelpCenter selectById(Long id);

    @Delete("delete from help_center where id in (${ids})")
    void deleteHelp(@Param("ids") String ids);

    @Select("select * from help_center where id < #{0} order by id desc limit 1")
    HelpCenter selectLastById(Long id);

    @Select("select * from help_center where id > #{0} order by id asc limit 1")
    HelpCenter selectNextById(Long id);


}
