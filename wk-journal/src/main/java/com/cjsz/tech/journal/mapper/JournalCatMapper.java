package com.cjsz.tech.journal.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.journal.beans.FindCatOrgBean;
import com.cjsz.tech.journal.beans.JournalCatBean;
import com.cjsz.tech.journal.beans.JournalCatOrgBean;
import com.cjsz.tech.journal.domain.JournalCat;
import com.cjsz.tech.journal.provider.JournalCatProvider;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface JournalCatMapper extends BaseMapper<JournalCat> {

	//获取机构的全部期刊分类(分类列表)
	@Select("select r.rel_id,r.org_id,r.journal_cat_id,r.order_weight,r.is_show,r.source_id,r.source_type, " +
			"r.create_time,r.update_time,r.is_delete,c.pid,c.journal_cat_name,c.journal_cat_path, " +
			"c.enabled,c.remark,c.cat_pic,c.org_count  from journal_cat_org_rel r " +
			"left join journal_cat c on r.journal_cat_id = c.journal_cat_id " +
			"where c.is_delete = 2 and r.org_id = #{org_id} " +
			"order by length(c.journal_cat_path)-length(replace(c.journal_cat_path,'|','')) asc, r.order_weight desc")
	public List<JournalCatBean> getCats(@Param("org_id") Long org_id);

	//获取机构的全部期刊分类(分类列表，启用的)
	@Select("select r.rel_id,r.org_id,r.journal_cat_id,r.order_weight,r.is_show,r.source_id,r.source_type, " +
			"r.create_time,r.update_time,r.is_delete,c.pid,c.journal_cat_name,c.journal_cat_path, " +
			"c.enabled,c.remark,c.cat_pic,c.org_count  from journal_cat_org_rel r " +
			"left join journal_cat c on r.journal_cat_id = c.journal_cat_id " +
			"where c.is_delete = 2 and c.enabled = 1 and r.org_id = #{org_id}  and r.is_delete = 2 " +
			"order by length(c.journal_cat_path)-length(replace(c.journal_cat_path,'|','')) asc, r.order_weight desc")
	public List<JournalCatBean> getEnabledCats(@Param("org_id") Long org_id);

	//获取机构的全部期刊分类(详情列表)
	@Select("select r.rel_id,r.org_id,r.journal_cat_id,r.order_weight,r.is_show,r.source_id,r.source_type, " +
			"r.create_time,r.update_time,r.is_delete,c.pid,c.journal_cat_name,c.journal_cat_path, " +
			"c.enabled,c.remark,c.cat_pic,c.org_count  from journal_cat_org_rel r " +
			"left join journal_cat c on r.journal_cat_id = c.journal_cat_id " +
			"where c.is_delete = 2 and r.org_id = #{org_id} and r.source_type = 1 " +
			"order by length(c.journal_cat_path)-length(replace(c.journal_cat_path,'|','')) asc, r.order_weight desc")
	public List<JournalCatBean> getOrgCats(@Param("org_id") Long org_id);

	//分类名称重复(分类名查找)
	@Select("select * from journal_cat where org_id = #{0} and journal_cat_name = #{1}")
	public List<JournalCat> selectByCatName(Long org_id, String journal_cat_name);

	//更新层次路径
	@Update("update journal_cat set journal_cat_path = #{1}, update_time = now() where journal_cat_id = #{0}")
	public void updateCatPathByCatId(Long journal_cat_id, String cat_path);

	//分类Id查询
	@Select("select * from journal_cat where journal_cat_id = #{1}")
	public JournalCat selectByCatId(Long cat_id);

	//分类名称重复(分类名查找不包括本身)
	@Select("select * from journal_cat where org_id = #{0} and journal_cat_name = #{1} and journal_cat_id != #{2} and is_delete = 2")
	public List<JournalCat> selectOtherByCatName(Long org_id, String journal_cat_name, Long journal_cat_id);

	//将层次路径中包含old_full_path的，更新为new_full_path
	@Update("update journal_cat set journal_cat_path = REPLACE(journal_cat_path,#{0},#{1}), update_time = now() where journal_cat_path like concat(#{0},'%') and org_id = #{2}")
	public void updateFullPath(String old_full_path, String new_full_path, Long org_id);

	//启用
	@Update("update journal_cat set enabled = 1,update_time = now() where enabled = 2 and org_id = #{org_id} and journal_cat_id in(${cat_ids}) ")
	public void updateEnabledByCatIds(@Param("org_id") Long org_id, @Param("cat_ids") String cat_ids);

	//不启用
	@Update("update journal_cat set enabled = 2,update_time = now() where enabled = 1 and org_id = #{org_id} and journal_cat_path like concat('${journal_cat_path}','%')")
	public void updateEnabledByCatPath(@Param("org_id") Long org_id, @Param("journal_cat_path") String journal_cat_path);

	//不显示
//	@Update("update journal_cat set is_show = 2,update_time = now() where is_show = 1 and org_id = #{root_org_id} and journal_cat_path like concat('${journal_cat_path}','%')")
	@Update("update journal_cat_org_rel r set r.is_show = 2,r.update_time = now() " +
			"where r.is_show = 1 and r.org_id = #{root_org_id} and r.journal_cat_id in(select journal_cat_id from journal_cat where journal_cat_path like concat('${journal_cat_path}','%'))")
	public void updateShowByCatPath(@Param("root_org_id") Long root_org_id, @Param("journal_cat_path") String journal_cat_path);

	//显示
//	@Update("update journal_cat set is_show = 1,update_time = now() where is_show = 2 and org_id = #{root_org_id} and journal_cat_id in(${cat_ids}) ")
	@Update("update journal_cat_org_rel r set r.is_show = 1,r.update_time = now() " +
			"where r.is_show = 2 and r.org_id = #{root_org_id} and r.journal_cat_id in(${cat_ids})")
	public void updateShowByCatIds(@Param("root_org_id") Long root_org_id, @Param("cat_ids") String cat_ids);

	//查询机构的期刊分类
	@Select("select * from journal_cat where org_id = #{0}")
	public List<Long> selectOrgCatIds(Long org_id);

	//查找分类
	@Select("select * from journal_cat where org_id = #{0} and journal_cat_id = #{1}")
	public JournalCat selectOrgCatByCatId(Long org_id, Long pid);

	//通过cat_ids找到full_paths
	@Select("select journal_cat_path from journal_cat where journal_cat_id in(${cat_ids})")
	public List<String> getFullPathsByCatIds(@Param("cat_ids") String cat_ids);

	//通过full_paths找到journal_cat_ids
	@Select("select journal_cat_id from journal_cat where journal_cat_path like '${full_path}%'")
	public List<Long> getCatIdsByFullPath(@Param("full_path") String full_path);

	//通过cat_ids删除期刊
//	@Delete("delete from journal_cat where journal_cat_id in (${cat_ids}) ")
	@Update("update journal_cat set is_delete = 1, update_time = now() where journal_cat_id in (${cat_ids}) ")
	public void deleteJournalCatsByIds(@Param("cat_ids") String cat_ids);

	@Select("select c.* from journal_cat_org_rel r " +
			"left join journal_cat c on c.journal_cat_id = r.journal_cat_id " +
			"where r.org_id = #{0} and c.pid = 0 and c.enabled = 1 and r.is_show = 1 and c.is_delete = 2 and r.is_delete = 2")
	public List<JournalCat> selectSiteCatsByOrgId(Long org_id);

	@Select("select soe.extend_code org_code,soe.short_name,so.org_name from sys_organization so " +
			" left join sys_org_extend soe on(soe.org_id = so.org_id) " +
			" where so.enabled = 1 and so.is_delete = 2 and soe.is_delete = 2")
	public List<JournalCatOrgBean> getAddOrgs();

	@Select("select soe.extend_code org_code,soe.short_name,so.org_name,r.create_time,r.journal_cat_id,r.org_id from journal_cat_org_rel r " +
			"left join sys_organization so on(so.org_id = r.org_id) " +
			"left join sys_org_extend soe on(soe.org_id = so.org_id) " +
			"where so.enabled = 1 and so.is_delete = 2 and soe.is_delete = 2 and r.journal_cat_id = #{0} and r.is_delete = 2 ")
	public List<JournalCatOrgBean> getRemoveOrgs(Long journal_cat_id);

	@Update("update journal_cat set org_count = (org_count + #{1}), update_time = now() where journal_cat_id = #{0}")
	public void updateOrgCount(Long journal_cat_id, Integer num);

	@Select("select c.* from journal_cat c " +
			"left join journal_cat_org_rel r on c.journal_cat_id = r.journal_cat_id " +
			"where r.org_id = #{0} and c.journal_cat_path like concat(#{1},'%') and c.is_delete = 2 and c.enabled = 1 and r.is_delete = 2 and r.is_show = 1")
	public List<JournalCatBean> getOwnerCats(Long org_id, String journal_cat_path);

	@Select("select c.journal_cat_id, c.journal_cat_name,r.org_id, c.remark, c.pid, c.journal_cat_path, c.enabled, c.is_delete org_is_delete, r.is_show, r.order_weight, r.is_delete from journal_cat_org_rel r\n" +
			"left join journal_cat c on r.journal_cat_id = c.journal_cat_id \n" +
			"where c.is_delete = 2 and r.org_id =#{0} and( r.update_time  >#{1} or" +
			" c.update_time)>#{1} " +
			"order by length(c.journal_cat_path)-length(replace(c.journal_cat_path,'|','')) asc, r.order_weight desc limit #{2}, #{3}")
	public List<JournalCatBean> getOffLineNumList(Long orgid, String timev, Integer pageNum, Integer pageSize);

	@Select("select count(*) as num from " +
			"(select r.* from journal_cat_org_rel r left join journal_cat c on c.journal_cat_id = r.journal_cat_id " +
			"where r.org_id = #{0} and ( c.update_time >#{1} or r.update_time >#{1} )) a")
	public Integer checkOffLineNum(Long orgid, String timev);

	@SelectProvider(type = JournalCatProvider.class, method = "getOrgQuery")
	public List<JournalCatOrgBean> getOrgQuery(@Param("bean") FindCatOrgBean bean);

	@Select("select journal_cat_id from journal_cat where journal_cat_id = #{0} and org_id = #{1}")
	Long getCatId(Long journal_cat_id,Long org_id);

}
