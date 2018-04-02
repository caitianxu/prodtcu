package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.beans.ArticleBean;
import com.cjsz.tech.system.beans.SubjectArticleBean;
import com.cjsz.tech.system.domain.SubjectArticleRel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Administrator on 2017/9/25 0025.
 */
public interface SubjectArticleRelMapper extends BaseMapper<SubjectArticleRel> {

    //查询专题是否存在资讯
    @Select("select * from subject_article_rel where subject_id = #{0} and article_id = #{1} limit 1")
    SubjectArticleRel selectById(Long subject_id,Long article_id);

    //获取专题新闻
    @Select("select a.article_id,a.article_content,a.publish_time, a.article_type, a.article_title,a.article_cat_id, r.order_weight,a.cover_url_small,r.subject_id,r.rel_id from subject_article_rel r left join article a on a.article_id = r.article_id where subject_id = #{0} order by r.order_weight desc")
    List<SubjectArticleBean> getArticle(Long subject_id);

    @Delete("delete from subject_article_rel where article_id = #{0} and subject_id = #{1}")
    void deleteArticle(Long article_id, Long subject_id);

    @Update("update subject_article_rel set article_id = #{1} where subject_id = #{0}")
    void updateArticle(Long subject_id, Long article_id);

    @Update("update subject_article_rel set order_weight = #{1} where rel_id = #{0}")
    void orderArticle(Long rel_id, Long order_weight);
}
