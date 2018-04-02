package com.cjsz.tech.media.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.media.domain.Audio;
import com.cjsz.tech.media.provider.AudioProvider;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Li Yi on 2016/12/7.
 */
public interface AudioMapper extends BaseMapper<Audio>{

    @Select("select * from audio where audio_id = #{0} ")
    Audio selectById(Long audio_id);

    @SelectProvider(type = AudioProvider.class, method = "pageQuery")
    List<Audio> pageQuery(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText);

    @Update("update  audio set is_delete = 1,update_time = now() where audio_id in (${audio_ids_str})")
    void  deleteByAudioIds(@Param("audio_ids_str") String audio_ids_str);

    @Update("update  audio set is_delete = 1,update_time = now() where audio_cat_id in (${cat_ids})")
    void deleteByCatIds(@Param("cat_ids") String cat_ids);

    @Select("select * from audio where is_delete = 2 and  audio_cat_id in (${cat_ids}) order by order_weight desc, create_time desc")
    List<Audio> selectByCatIds(@Param("cat_ids") String cat_ids);

    @Select("select count(*) as num from audio where org_id=#{0}  and update_time >#{1}")
    Integer checkOffLineNum(Long org_id, String update_time);

    @Select("select *,GROUP_CONCAT(audio_cat_name) \n" +
            "as audio_cat_name2 from (select DISTINCT a.*,c.audio_cat_name ,c.audio_cat_path from  audio a \n" +
            "LEFT JOIN audio_cat c ON a.org_id=c.org_id  and a.audio_cat_id=c.audio_cat_id\n" +
            "LEFT JOIN audio_cat_org_rel r ON a.org_id=r.org_id \n" +
            "WHERE a.audio_cat_id in(SELECT audio_cat_id FROM audio_cat_org_rel \n" +
            "WHERE org_id=#{0} and is_delete=2 ) and c.enabled=1 and(\n" +
            " r.update_time >#{1} or\n" +
            " a.update_time >#{1} or\n" +
            " c.update_time >#{1} )\n" +
            ") x1 group by audio_id,org_id,user_id,audio_title,cover_url,cover_url_small,audio_url,audio_remark,order_weight,create_time,update_time" +
            " limit #{2}, #{3}")
    List<Audio> getOffLineNumList(Long org_id, String update_time, Integer pageNum, Integer pageSize);

    @Select("select count(*) from audio where is_delete = 2 and  audio_cat_id in( " +
            "select r.audio_cat_id from audio_cat_org_rel r " +
            "left join audio_cat c on r.audio_cat_id = c.audio_cat_id " +
            "where r.org_id = #{0} and r.is_delete = 2 and c.is_delete = 2)")
    Integer getCount(Long org_id);

    @Select("select count(*) from audio where is_delete = 2 and  audio_cat_id in( " +
            "select r.audio_cat_id from audio_cat_org_rel r " +
            "left join audio_cat c on r.audio_cat_id = c.audio_cat_id " +
            "where r.org_id = #{0} and r.is_delete = 2 and c.enabled =1 and c.is_delete = 2)")
    Integer getCountByOrgId(Long org_id);

    @Select("select audio_cat_id from audio where audio_id = #{0}")
    Long getCatId(Long audio_id);

}
