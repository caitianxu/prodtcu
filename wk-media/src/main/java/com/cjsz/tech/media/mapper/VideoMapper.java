package com.cjsz.tech.media.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.media.domain.Video;
import com.cjsz.tech.media.provider.VideoProvider;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Li Yi on 2016/12/5.
 */
public interface VideoMapper extends BaseMapper<Video> {

    @Select("select * from video where video_id = #{0} ")
    Video selectById(Long video_id);

    @SelectProvider(type = VideoProvider.class, method = "pageQuery")
    List<Video> pageQuery(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText);

    @SelectProvider(type = VideoProvider.class, method = "pageSiteQuery")
    List<Video> pageSiteQuery(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText);

    @Update("update video set is_delete = 1,update_time = now() where video_id in (${video_ids_str})")
    void deleteByVideoIds(@Param("video_ids_str") String video_ids_str);

    @Update("update video set is_delete = 1,update_time = now() where video_cat_id in (${cat_ids})")
    void deleteByCatIds(@Param("cat_ids") String cat_ids);

    @Select("select * from video where is_delete = 2 and video_cat_id in (${cat_ids}) order by order_weight desc, create_time desc")
    List<Video> selectByCatIds(@Param("cat_ids") String cat_ids);

    @Select("select count(*) as num from video where org_id=#{0} and  update_time >#{1}")
    Integer checkOffLineNum(Long org_id, String update_time);

    @Select("select *,GROUP_CONCAT(video_cat_name) \n" +
            " as video_cat_name2 from (select DISTINCT a.*,c.video_cat_name,c.video_cat_path from  video a \n" +
            " LEFT JOIN video_cat c ON a.org_id=c.org_id  and a.video_cat_id=c.video_cat_id \n" +
            " LEFT JOIN video_cat_org_rel r ON a.org_id=r.org_id \n" +
            " WHERE a.video_cat_id in(SELECT video_cat_id FROM video_cat_org_rel \n" +
            " WHERE org_id=#{0} and is_delete=2 ) and c.enabled=1  and(\n" +
            "\t r.update_time >#{1} or\n" +
            "\t c.update_time >#{1} or\n" +
            "\t a.update_time >#{1} \n" +
            ")\n" +
            ") x1  group by video_id,org_id,user_id,video_title,cover_url,cover_url_small,video_url,video_remark,order_weight,create_time,update_time" +
            " limit #{2}, #{3}")
    List<Video> getOffLineNumList(Long org_id, String update_time, Integer pageNum, Integer pageSize);

    @Select("select count(*) from video  where is_delete = 2 and video_cat_id in(\n" +
            "select r.video_cat_id from video_cat_org_rel r \n" +
            "left join video_cat c on r.video_cat_id = c.video_cat_id\n" +
            "where r.org_id = #{0} and r.is_delete = 2 and c.enabled =1 and c.is_delete = 2)")
    Integer getCountByOrgId(Long org_id);

    @Select("select count(*) from video  where is_delete = 2 and video_cat_id in(\n" +
            "select r.video_cat_id from video_cat_org_rel r \n" +
            "left join video_cat c on r.video_cat_id = c.video_cat_id\n" +
            "where r.org_id = #{0} and r.is_delete = 2 and c.is_delete = 2)")
    Integer getCount(Long org_id);

    @Select("select video_cat_id from video where video_id = #{0}")
    Long getVideoCatId(Long video_id);

}
