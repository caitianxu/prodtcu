package com.cjsz.tech.dev.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.domain.ConfContent;
import com.cjsz.tech.dev.provider.ConfContentProvider;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface ConfContentMapper extends BaseMapper<ConfContent> {

    @SelectProvider(type = ConfContentProvider.class, method = "pageQuery")
    List<ConfContent> pageQuery(@Param("conf_id") Long conf_id, @Param("searchText") String searchText);

    @Delete("delete from conf_content where conf_content_id in (${content_ids})")
    void deleteByContentIds(@Param("content_ids") String content_ids_str);

    @Select("select content.* from conf_content content where content.conf_id = #{0} and UNIX_TIMESTAMP(content.update_time)>${1}")
    List<ConfContent> selectConfContents(Long conf_id, Long update_time);

    @Select("select * from conf_content where conf_id=#{0}")
    List<ConfContent> getConfContentsByConfid(Long confid);

    @Select("select count(*) as num from conf_content where   update_time >#{1}   and conf_id in (select conf_id from device_conf_rel where  device_id = #{2} )  ")
    public Integer checkOffLineNum(Long orgid, String timev, Long devid);

    @Select("select * from conf_content where   update_time >#{1}  and conf_id in (select conf_id from device_conf_rel where  device_id = #{2} ) limit #{3}, #{4} ")
    public List<ConfContent> getOffLineNumList(Long orgid, String timev, Long devid, Integer pageNum, Integer pageSize);

    @Select("select * from conf_content where conf_id = #{0}")
    List<ConfContent> getList(Long conf_id);

    @Update("update conf_content set update_time = now() where conf_id= #{0}")
    void updateContentTime(Long conf_id);
}
