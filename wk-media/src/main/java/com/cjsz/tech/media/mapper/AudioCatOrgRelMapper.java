package com.cjsz.tech.media.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.media.beans.AudioCatBean;
import com.cjsz.tech.media.domain.AudioCatOrgRel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by LuoLi on 2017/4/21 0021.
 */
public interface AudioCatOrgRelMapper extends BaseMapper<AudioCatOrgRel> {

    //关系表分类表联合详细分类信息
    @Select("select r.*, c.pid, c.audio_cat_name, c.audio_cat_path, c.enabled, c.remark, c.is_delete, c.org_count from audio_cat_org_rel r " +
            "left join audio_cat c on r.audio_cat_id = c.audio_cat_id " +
            "where r.org_id = #{0} and r.audio_cat_id = #{1}")
    public AudioCatBean seletInfoByOrgIdAndCatId(Long org_id, Long audio_cat_id);

    //关系Id查询关系
    @Select("select * from audio_cat_org_rel where rel_id = #{0}")
    public AudioCatOrgRel selectById(Long rel_id);

    //更新排序
    @Update("update audio_cat_org_rel set order_weight = #{0},update_time = now() where rel_id = #{1}")
    public void updateOrderById(Long order_weight, Long rel_id);

    //机构Id分类Id查询关系
    @Select("select * from audio_cat_org_rel where org_id = #{0} and audio_cat_id = #{1}")
    public AudioCatOrgRel selectByOrgIdAndCatId(Long org_id, Long audio_cat_id);

    @Update("update audio_cat_org_rel set is_delete = #{1}, update_time = now() where rel_id = #{0} and is_delete != #{1} ")
    public void updateIsDelete(Long rel_id, Integer is_delete);

    @Update("update audio_cat_org_rel set is_delete = 1, update_time = now() where audio_cat_id in (${cat_ids}) ")
    public void deleteRelsByIds(@Param("cat_ids") String cat_ids);

    @Select("select * from audio_cat_org_rel where audio_cat_id = #{0}")
    public List<AudioCatOrgRel> selectListByCatId(Long cat_id);
}
