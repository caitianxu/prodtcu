package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.domain.AdvCat;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/22 0022.
 */
public interface AdvCatMapper extends BaseMapper<AdvCat> {

    //广告分类列表
    @Select("select ac.*,p.project_name from adv_cat ac left join sys_project p on ac.project_code = p.project_code where is_delete = 2  order by order_weight desc")
    List<AdvCat> getAdvCatList(Long org_id);

    //删除分类
    @Update("update adv_cat set is_delete = 1,update_time = now() where adv_cat_id in(${catIdsStr})")
    void deleteAdvCats(@Param("catIdsStr") String catIdsStr);

    @Select("select * from adv_cat where (org_id=#{0} or org_id = 1)  and  update_time >#{1} limit #{2}, #{3} ")
    List<AdvCat> getOffLineNumList(Long orgid, String oldtimestamp, Integer pageNum, Integer pageSize);

    @Select("select count(*) as num from adv_cat where (org_id=#{0} or org_id = 1) and  update_time >#{1}")
    public Integer checkOffLineNum(Long orgid, String timev);

    //分类名查找
    @Select("select * from adv_cat where adv_cat_name = #{0} and is_delete = 2 ")
    List<AdvCat> selectByCatName(String adv_cat_name);

    //分类名查找不包括本身
    @Select("select * from adv_cat where adv_cat_name = #{0} and is_delete = 2 and adv_cat_id != #{1}")
    List<AdvCat> selectOtherByCatName(String adv_cat_name, Long adv_cat_id);

    //分类编号查找分类
    @Select("select * from adv_cat where adv_cat_code = #{0} and is_delete = 2 ")
    AdvCat selectByCatCode(String adv_cat_code);

    //分类编号查找不包括本身
    @Select("select * from adv_cat where adv_cat_code = #{0} and adv_cat_id != #{1} and is_delete = 2 ")
    AdvCat selectOtherByCatCode(String adv_cat_code, Long adv_cat_id);

    //根据项目code查询广告分类
    @Select("select * from adv_cat where project_code = #{0}")
    List<AdvCat> getAdvCatByProjectCode(String project_code);
}
