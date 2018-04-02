package com.cjsz.tech.media.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.media.beans.FindCatOrgBean;
import com.cjsz.tech.media.beans.VideoCatBean;
import com.cjsz.tech.media.beans.VideoCatOrgBean;
import com.cjsz.tech.media.domain.VideoCat;
import com.cjsz.tech.media.provider.VideoCatProvider;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Li Yi on 2016/12/6.
 */
public interface VideoCatMapper extends BaseMapper<VideoCat> {

    //获取机构的全部资讯分类(分类列表)
    @Select("select r.rel_id,r.org_id,r.video_cat_id,r.order_weight,r.is_show,r.source_id,r.source_type, " +
            "r.create_time,r.update_time,r.is_delete,c.pid,c.video_cat_name,c.video_cat_path, " +
            "c.enabled,c.remark,c.cat_pic,c.org_count  from video_cat_org_rel r " +
            "left join video_cat c on r.video_cat_id = c.video_cat_id " +
            "where c.is_delete = 2 and r.org_id = #{org_id} " +
            "order by length(c.video_cat_path)-length(replace(c.video_cat_path,'|','')) asc, r.order_weight desc")
    public List<VideoCatBean> getCats(@Param("org_id") Long org_id);

    //获取机构的全部资讯分类(分类列表，启用的)
    @Select("select r.rel_id,r.org_id,r.video_cat_id,r.order_weight,r.is_show,r.source_id,r.source_type, " +
            "r.create_time,r.update_time,r.is_delete,c.pid,c.video_cat_name,c.video_cat_path, " +
            "c.enabled,c.remark,c.cat_pic,c.org_count  from video_cat_org_rel r " +
            "left join video_cat c on r.video_cat_id = c.video_cat_id " +
            "where c.is_delete = 2 and c.enabled = 1 and r.org_id = #{org_id} and r.is_delete = 2 " +
            "order by length(c.video_cat_path)-length(replace(c.video_cat_path,'|','')) asc, r.order_weight desc")
    public List<VideoCatBean> getEnabledCats(@Param("org_id") Long org_id);

    //获取机构的全部资讯分类(详情列表)
    @Select("select r.rel_id,r.org_id,r.video_cat_id,r.order_weight,r.is_show,r.source_id,r.source_type, " +
            "r.create_time,r.update_time,r.is_delete,c.pid,c.video_cat_name,c.video_cat_path, " +
            "c.enabled,c.remark,c.cat_pic,c.org_count  from video_cat_org_rel r " +
            "left join video_cat c on r.video_cat_id = c.video_cat_id " +
            "where c.is_delete = 2 and r.org_id = #{org_id} " +
            "order by length(c.video_cat_path)-length(replace(c.video_cat_path,'|','')) asc, r.order_weight desc")
    public List<VideoCatBean> getOrgCats(@Param("org_id") Long org_id);

    //分类名称重复(分类名查找)
    @Select("select * from video_cat where org_id = #{0} and video_cat_name = #{1} and is_delete = 2")
    public List<VideoCat> selectByCatName(Long org_id, String video_cat_name);

    //更新层次路径
    @Update("update video_cat set video_cat_path = #{1}, update_time = now() where video_cat_id = #{0}")
    public void updateCatPathByCatId(Long video_cat_id, String cat_path);

    //分类Id查询
    @Select("select * from video_cat where video_cat_id = #{1}")
    public VideoCat selectByCatId(Long cat_id);

    //分类名称重复(分类名查找不包括本身)
    @Select("select * from video_cat where org_id = #{0} and video_cat_name = #{1} and video_cat_id != #{2}")
    public List<VideoCat> selectOtherByCatName(Long org_id, String video_cat_name, Long video_cat_id);

    //将层次路径中包含old_full_path的，更新为new_full_path
    @Update("update video_cat set video_cat_path = REPLACE(video_cat_path,#{0},#{1}), update_time = now() where video_cat_path like concat(#{0},'%') and org_id = #{2}")
    public void updateFullPath(String old_full_path, String new_full_path, Long org_id);

    //启用
    @Update("update video_cat set enabled = 1,update_time = now() where enabled = 2 and org_id = #{org_id} and video_cat_id in(${cat_ids}) ")
    public void updateEnabledByCatIds(@Param("org_id") Long org_id, @Param("cat_ids") String cat_ids);

    //不启用
    @Update("update video_cat set enabled = 2,update_time = now() where enabled = 1 and org_id = #{org_id} and video_cat_path like concat('${video_cat_path}','%')")
    public void updateEnabledByCatPath(@Param("org_id") Long org_id, @Param("video_cat_path") String video_cat_path);

    //不显示
//	@Update("update video_cat set is_show = 2,update_time = now() where is_show = 1 and org_id = #{root_org_id} and video_cat_path like concat('${video_cat_path}','%')")
    @Update("update video_cat_org_rel r set r.is_show = 2,r.update_time = now() " +
            "where r.is_show = 1 and r.org_id = #{root_org_id} and r.video_cat_id in(select video_cat_id from video_cat where video_cat_path like concat('${video_cat_path}','%'))")
    public void updateShowByCatPath(@Param("root_org_id") Long root_org_id, @Param("video_cat_path") String video_cat_path);

    //显示
//	@Update("update video_cat set is_show = 1,update_time = now() where is_show = 2 and org_id = #{root_org_id} and video_cat_id in(${cat_ids}) ")
    @Update("update video_cat_org_rel r set r.is_show = 1,r.update_time = now() " +
            "where r.is_show = 2 and r.org_id = #{root_org_id} and r.video_cat_id in(${cat_ids})")
    public void updateShowByCatIds(@Param("root_org_id") Long root_org_id, @Param("cat_ids") String cat_ids);

    //查询机构的资讯分类
    @Select("select * from video_cat where org_id = #{0}")
    public List<Long> selectOrgCatIds(Long org_id);

    //查找分类
    @Select("select * from video_cat where org_id = #{0} and video_cat_id = #{1}")
    public VideoCat selectOrgCatByCatId(Long org_id, Long pid);

    //通过cat_ids找到full_paths
    @Select("select video_cat_path from video_cat where video_cat_id in(${cat_ids})")
    public List<String> getFullPathsByCatIds(@Param("cat_ids") String cat_ids);

    //通过full_paths找到video_cat_ids
    @Select("select video_cat_id from video_cat where video_cat_path like '${full_path}%'")
    public List<Long> getCatIdsByFullPath(@Param("full_path") String full_path);

    //通过cat_ids删除资讯
//	@Delete("delete from video_cat where video_cat_id in (${cat_ids}) ")
    @Update("update video_cat set is_delete = 1, update_time = now() where video_cat_id in (${cat_ids}) ")
    public void deleteVideoCatsByIds(@Param("cat_ids") String cat_ids);

    @Select("select c.* from video_cat_org_rel r " +
            "left join video_cat c on c.video_cat_id = r.video_cat_id " +
            "where r.org_id = #{0} and c.pid = 0 and c.enabled = 1 and r.is_show = 1 and c.is_delete = 2 and r.is_delete = 2")
    public List<VideoCat> selectSiteCatsByOrgId(Long org_id);

    @Select("select soe.extend_code org_code,soe.short_name,so.org_name from sys_organization so " +
            " left join sys_org_extend soe on(soe.org_id = so.org_id) " +
            " where so.enabled = 1 and so.is_delete = 2 and soe.is_delete = 2")
    public List<VideoCatOrgBean> getAddOrgs();

    @Select("select soe.extend_code org_code,soe.short_name,so.org_name,r.create_time,r.video_cat_id,r.org_id from video_cat_org_rel r " +
            "left join sys_organization so on(so.org_id = r.org_id) " +
            "left join sys_org_extend soe on(soe.org_id = so.org_id) " +
            "where so.enabled = 1 and so.is_delete = 2 and soe.is_delete = 2 and r.video_cat_id = #{0} and r.is_delete = 2 ")
    public List<VideoCatOrgBean> getRemoveOrgs(Long video_cat_id);

    @Update("update video_cat set org_count = (org_count + #{1}), update_time = now() where video_cat_id = #{0}")
    public void updateOrgCount(Long video_cat_id, Integer num);

    @Select("select c.* from video_cat c " +
            "left join video_cat_org_rel r on c.video_cat_id = r.video_cat_id " +
            "where r.org_id = #{0} and c.video_cat_path like concat(#{1},'%') and c.is_delete = 2 and c.enabled = 1 and r.is_delete = 2 and r.is_show = 1")
    public List<VideoCatBean> getOwnerCats(Long org_id, String video_cat_path);

    @Select("select c.video_cat_id, c.video_cat_name, c.remark,r.org_id, c.pid, c.video_cat_path, c.enabled, c.is_delete org_is_delete, r.is_show, r.order_weight, r.is_delete from video_cat_org_rel r\n" +
            "            left join video_cat c on r.video_cat_id = c.video_cat_id \n" +
            "            where c.is_delete = 2 and r.org_id = #{0} and(\n" +
            " r.update_time >#{1} or\n" +
            " c.update_time >#{1} \n" +
            ")\n" +
            "order by length(c.video_cat_path)-length(replace(c.video_cat_path,'|','')) asc, r.order_weight desc" +
            " limit #{2}, #{3}")
    public List<VideoCatBean> getOffLineNumList(Long orgid, String oldtimestamp, Integer pageNum, Integer pageSize);

    @Select("select count(*) as num from " +
            "(select r.* from video_cat_org_rel r left join video_cat c on c.video_cat_id = r.video_cat_id " +
            "where r.org_id = #{0} and (c.update_time >#{1} or r.update_time >#{1})) a")
    public Integer checkOffLineNum(Long orgid, String timev);

    @SelectProvider(type = VideoCatProvider.class, method = "getOrgQuery")
    public List<VideoCatOrgBean> getOrgQuery(@Param("bean") FindCatOrgBean bean);

    @Select("select video_cat_id from video_cat where video_cat_id = #{0} and org_id = #{1}")
    Long getCatId(Long video_cat_id,Long org_id);
}
