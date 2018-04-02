package com.cjsz.tech.cms.mapper;

import com.cjsz.tech.cms.domain.ArticleTemp;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
public interface ArticleTempMapper extends BaseMapper<ArticleTemp> {

    @Select("select t.*,u.user_name from article_temp t left join sys_user u on(t.user_id = u.user_id)")
    public List<ArticleTemp> pageQuery();

    @Select("select * from article_temp")
    public List<ArticleTemp> getAll();

    @Select("select * from article_temp where article_temp_id = #{0}")
    public ArticleTemp selectById(Integer article_temp_id);

    @Delete("delete from article_temp where article_temp_id in(${temp_ids})")
    public void deleteByTempIds(@Param("temp_ids") String temp_ids);

    @Select("select * from article_temp where temp_status != 1")
    public List<ArticleTemp> selectUnZip();

    @Select("select * from article_temp where temp_status = 1 and org_id = #{0}")
    public List<ArticleTemp> selectZipByOrgId(Long org_id);

    @Update("update article_temp set temp_status = #{1}, update_time = now() where article_temp_id = #{1}")
    public void updateArticleTempStatus(Integer article_temp_id, Integer temp_status);
}
