package com.cjsz.tech.journal.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.journal.beans.FindCatOrgBean;
import com.cjsz.tech.journal.beans.NewspaperCatBean;
import com.cjsz.tech.journal.beans.NewspaperCatOrgBean;
import com.cjsz.tech.journal.domain.NewspaperCat;
import com.cjsz.tech.journal.provider.NewspaperCat2Provider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by Jason on 16/3/3.
 */
public interface NewspaperCatMapper extends BaseMapper<NewspaperCat> {

    //获取机构的全部报纸分类(分类列表)
    @Select("select r.rel_id,r.org_id,r.newspaper_cat_id,r.order_weight,r.is_show,r.source_id,r.source_type, " +
            "r.create_time,r.update_time,r.is_delete,c.pid,c.newspaper_cat_name,c.newspaper_cat_path, " +
            "c.enabled,c.remark,c.cat_pic,c.org_count  from newspaper_cat_org_rel r " +
            "left join newspaper_cat c on r.newspaper_cat_id = c.newspaper_cat_id " +
            "where c.is_delete = 2 and r.org_id = #{org_id} " +
            "order by length(c.newspaper_cat_path)-length(replace(c.newspaper_cat_path,'|','')) asc, r.order_weight desc")
    public List<NewspaperCatBean> getCats(@Param("org_id") Long org_id);

    //获取机构的全部报纸分类(分类列表，启用的)
    @Select("select r.rel_id,r.org_id,r.newspaper_cat_id,r.order_weight,r.is_show,r.source_id,r.source_type, " +
            "r.create_time,r.update_time,r.is_delete,c.pid,c.newspaper_cat_name,c.newspaper_cat_path, " +
            "c.enabled,c.remark,c.cat_pic,c.org_count  from newspaper_cat_org_rel r " +
            "left join newspaper_cat c on r.newspaper_cat_id = c.newspaper_cat_id " +
            "where c.is_delete = 2 and c.enabled = 1 and r.org_id = #{org_id}  and r.is_delete = 2 " +
            "order by length(c.newspaper_cat_path)-length(replace(c.newspaper_cat_path,'|','')) asc, r.order_weight desc")
    public List<NewspaperCatBean> getEnabledCats(@Param("org_id") Long org_id);

    //获取机构的全部报纸分类(详情列表)
    @Select("select r.rel_id,r.org_id,r.newspaper_cat_id,r.order_weight,r.is_show,r.source_id,r.source_type, " +
            "r.create_time,r.update_time,r.is_delete,c.pid,c.newspaper_cat_name,c.newspaper_cat_path, " +
            "c.enabled,c.remark,c.cat_pic,c.org_count  from newspaper_cat_org_rel r " +
            "left join newspaper_cat c on r.newspaper_cat_id = c.newspaper_cat_id " +
            "where c.is_delete = 2 and r.org_id = #{org_id} " +
            "order by length(c.newspaper_cat_path)-length(replace(c.newspaper_cat_path,'|','')) asc, r.order_weight desc")
    public List<NewspaperCatBean> getOrgCats(@Param("org_id") Long org_id);

    //分类名称重复(分类名查找)
    @Select("select * from newspaper_cat where org_id = #{0} and newspaper_cat_name = #{1} and is_delete = 2")
    public List<NewspaperCat> selectByCatName(Long org_id, String newspaper_cat_name);

    //更新层次路径
    @Update("update newspaper_cat set newspaper_cat_path = #{1}, update_time = now() where newspaper_cat_id = #{0}")
    public void updateCatPathByCatId(Long newspaper_cat_id, String cat_path);

    //分类Id查询
    @Select("select * from newspaper_cat where newspaper_cat_id = #{0}")
    public NewspaperCat selectByCatId(Long cat_id);

    //分类名称重复(分类名查找不包括本身)
    @Select("select * from newspaper_cat where org_id = #{0} and newspaper_cat_name = #{1} and newspaper_cat_id != #{2} and is_delete = 2")
    public List<NewspaperCat> selectOtherByCatName(Long org_id, String newspaper_cat_name, Long newspaper_cat_id);

    //将层次路径中包含old_full_path的，更新为new_full_path
    @Update("update newspaper_cat set newspaper_cat_path = REPLACE(newspaper_cat_path,#{0},#{1}), update_time = now() where newspaper_cat_path like concat(#{0},'%') and org_id = #{2}")
    public void updateFullPath(String old_full_path, String new_full_path, Long org_id);

    //启用
    @Update("update newspaper_cat set enabled = 1,update_time = now() where enabled = 2 and org_id = #{org_id} and newspaper_cat_id in(${cat_ids}) ")
    public void updateEnabledByCatIds(@Param("org_id") Long org_id, @Param("cat_ids") String cat_ids);

    //不启用
    @Update("update newspaper_cat set enabled = 2,update_time = now() where enabled = 1 and org_id = #{org_id} and newspaper_cat_path like concat('${newspaper_cat_path}','%')")
    public void updateEnabledByCatPath(@Param("org_id") Long org_id, @Param("newspaper_cat_path") String newspaper_cat_path);

    //不显示
    @Update("update newspaper_cat_org_rel r set r.is_show = 2,r.update_time = now() " +
            "where r.is_show = 1 and r.org_id = #{root_org_id} and r.newspaper_cat_id in(select newspaper_cat_id from newspaper_cat where newspaper_cat_path like concat('${newspaper_cat_path}','%'))")
    public void updateShowByCatPath(@Param("root_org_id") Long root_org_id, @Param("newspaper_cat_path") String newspaper_cat_path);

    //显示
    @Update("update newspaper_cat_org_rel r set r.is_show = 1,r.update_time = now() " +
            "where r.is_show = 2 and r.org_id = #{root_org_id} and r.newspaper_cat_id in(${cat_ids})")
    public void updateShowByCatIds(@Param("root_org_id") Long root_org_id, @Param("cat_ids") String cat_ids);

    //查询机构的报纸分类
    @Select("select * from newspaper_cat where org_id = #{0}")
    public List<Long> selectOrgCatIds(Long org_id);

    //查找分类
    @Select("select * from newspaper_cat where org_id = #{0} and newspaper_cat_id = #{1}")
    public NewspaperCat selectOrgCatByCatId(Long org_id, Long pid);

    //通过cat_ids找到full_paths
    @Select("select newspaper_cat_path from newspaper_cat where newspaper_cat_id in(${cat_ids})")
    public List<String> getFullPathsByCatIds(@Param("cat_ids") String cat_ids);

    //通过full_paths找到newspaper_cat_ids
    @Select("select newspaper_cat_id from newspaper_cat where newspaper_cat_path like '${full_path}%'")
    public List<Long> getCatIdsByFullPath(@Param("full_path") String full_path);

    //通过cat_ids删除报纸
//	@Delete("delete from newspaper_cat where newspaper_cat_id in (${cat_ids}) ")
    @Update("update newspaper_cat set is_delete = 1, update_time = now() where newspaper_cat_id in (${cat_ids}) ")
    public void deleteNewspaperCatsByIds(@Param("cat_ids") String cat_ids);

    @Select("select c.* from newspaper_cat_org_rel r " +
            "left join newspaper_cat c on c.newspaper_cat_id = r.newspaper_cat_id " +
            "where r.org_id = #{0} and c.pid = 0 and c.enabled = 1 and r.is_show = 1 and c.is_delete = 2 and r.is_delete = 2")
    public List<NewspaperCat> selectSiteCatsByOrgId(Long org_id);

    @Select("select soe.extend_code org_code,soe.short_name,so.org_name from sys_organization so " +
            " left join sys_org_extend soe on(soe.org_id = so.org_id) " +
            " where so.enabled = 1 and so.is_delete = 2 and soe.is_delete = 2")
    public List<NewspaperCatOrgBean> getAddOrgs();

    @Select("select soe.extend_code org_code,soe.short_name,so.org_name,r.create_time,r.newspaper_cat_id,r.org_id from newspaper_cat_org_rel r " +
            "left join sys_organization so on(so.org_id = r.org_id) " +
            "left join sys_org_extend soe on(soe.org_id = so.org_id) " +
            "where so.enabled = 1 and so.is_delete = 2 and soe.is_delete = 2 and r.newspaper_cat_id = #{0} and r.is_delete = 2 ")
    public List<NewspaperCatOrgBean> getRemoveOrgs(Long newspaper_cat_id);

    @Update("update newspaper_cat set org_count = (org_count + #{1}), update_time = now() where newspaper_cat_id = #{0}")
    public void updateOrgCount(Long newspaper_cat_id, Integer num);

    @Select("select c.* from newspaper_cat c " +
            "left join newspaper_cat_org_rel r on c.newspaper_cat_id = r.newspaper_cat_id " +
            "where r.org_id = #{0} and c.newspaper_cat_path like concat(#{1},'%') and c.is_delete = 2 and c.enabled = 1 and r.is_delete = 2 and r.is_show = 1")
    public List<NewspaperCatBean> getOwnerCats(Long org_id, String newspaper_cat_path);

    @Select("select c.newspaper_cat_id, c.newspaper_cat_name,r.org_id, c.remark, c.pid, c.newspaper_cat_path, c.enabled, c.is_delete org_is_delete, r.is_show, r.order_weight, r.is_delete from newspaper_cat_org_rel r\n" +
            "left join newspaper_cat c on r.newspaper_cat_id = c.newspaper_cat_id \n" +
            "where c.is_delete = 2 and r.org_id = #{0} and (\n" +
            " r.update_time > #{1} or\n" +
            " c.update_time > #{1} )\n" +
            "order by length(c.newspaper_cat_path)-length(replace(c.newspaper_cat_path,'|','')) asc, r.order_weight desc" +
            " limit #{2}, #{3}")
    public List<NewspaperCatBean> getOffLineNumList(Long orgid, String oldtimestamp, Integer pageNum, Integer pageSize);

    @Select("select count(*) as num from " +
            "(select r.* from newspaper_cat_org_rel r left join newspaper_cat c on c.newspaper_cat_id = r.newspaper_cat_id " +
            "where r.org_id = #{0} and (c.update_time >#{1} or  r.update_time >#{1} )) a")
    public Integer checkOffLineNum(Long orgid, String timev);

    @SelectProvider(type = NewspaperCat2Provider.class, method = "getOrgQuery")
    public List<NewspaperCatOrgBean> getOrgQuery(@Param("bean") FindCatOrgBean bean);

    @Select("select newspaper_cat_id from newspaper_cat where newspaper_cat_id = #{0} and org_id = #{1}")
    Long getCatId(Long newspaper_cat_id,Long org_id);

    @Select("select c.* from newspaper_cat_org_rel r " +
            "   left join newspaper_cat c on c.newspaper_cat_id = r.newspaper_cat_id " +
            "    where r.org_id = #{0} and c.pid = 0 and c.enabled = 1 and" +
            "      r.is_show = 1 and c.is_delete = 2 and r.is_delete = 2 order by r.order_weight desc")
    List<Map<String, Object>> selectFirstCatsByOrgIdAndCount(Long org_id);

    @Select("select r.rel_id,r.org_id,r.newspaper_cat_id,r.order_weight,r.is_show, " +
            "r.create_time,r.update_time,r.is_delete,c.pid,c.newspaper_cat_name,c.newspaper_cat_path, " +
            "c.enabled,c.remark,c.cat_pic,c.org_count  from newspaper_cat_org_rel r " +
            "left join newspaper_cat c on r.newspaper_cat_id = c.newspaper_cat_id " +
            "where c.is_delete = 2 and c.enabled = 1 and r.org_id = #{0} and r.is_show = 1 and r.is_delete = 2 " +
            "order by length(c.newspaper_cat_path)-length(replace(c.newspaper_cat_path,'|','')) asc, r.order_weight desc")
    List<NewspaperCatBean> selectAllCats(Long org_id);
}
