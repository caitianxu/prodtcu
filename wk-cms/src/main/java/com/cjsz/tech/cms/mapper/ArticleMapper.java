package com.cjsz.tech.cms.mapper;

import com.cjsz.tech.cms.domain.Article;
import com.cjsz.tech.cms.provider.ArticleProvider;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
public interface ArticleMapper extends BaseMapper<Article> {

    //分页查询本机构的新闻
    @SelectProvider(type = ArticleProvider.class, method = "getArticleList")
    List<Article> getArticleList(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText);

    @SelectProvider(type = ArticleProvider.class, method = "sitePageQuery")
    List<Article> sitePageQuery(@Param("org_id") Long org_id, @Param("catPath") String catPath);

    @Select("select * from article where article_id = #{0} ")
    public Article selectById(Long article_id);

    @Update("update  article set is_delete = 1,update_time = now() where article_id in (${article_ids_str})")
    public void deleteByArticleIds(@Param("article_ids_str") String article_ids_str);

    @Update("update  article set is_delete = 1,update_time = now() where article_cat_id in (${cat_ids})")
    public void deleteByCatIds(@Param("cat_ids") String cat_ids);

    @Select("select * from article where is_delete = 2 and article_cat_id in (${cat_ids})")
    public List<Article> selectByCatIds(@Param("cat_ids") String cat_ids);

    //查询未解析成功的资讯
    @Select("select * from article where parse_status != 1 and is_delete = 2 ")
    public List<Article> selectUnParse();

    @Update("update article set parse_status = #{1}, update_time = now()  where org_id = #{0} ")
    public void updateParseStatusByOrgId(Long org_id, Integer parse_status);

    @Update("update article set parse_status = #{1}, update_time = now()  where article_id = #{0} ")
    public void updateParseStatusByArticleId(Long article_id, Integer parse_status);

    @Select("select count(*) as num from article where (org_id=#{0} or org_id = 1) and  update_time >#{1}  ")
    public Integer checkOffLineNum(Long orgid, String timev);

    @Select("select *,GROUP_CONCAT(article_cat_name) \n" +
            "                as article_cat_name2 from (select DISTINCT a.*,c.article_cat_name,c.article_cat_path from  article a \n" +
            "                LEFT JOIN article_cat c ON a.org_id=c.org_id and a.article_cat_id=c.article_cat_id \n" +
            "                LEFT JOIN article_cat_org_rel r ON a.org_id=r.org_id \n" +
            "                WHERE  a.article_cat_id in(SELECT article_cat_id FROM article_cat_org_rel \n" +
            "                WHERE org_id= #{0} and is_delete=2) and c.enabled=1 and (\n" +
            "\t(r.update_time is not null and r.update_time >#{1} ) OR\n" +
            "\t(c.update_time is not null and c.update_time >#{1} ) OR\n" +
            "\t(a.update_time is not null and a.update_time >#{1} ) \n" +
            ")) x1 group by article_id,org_id,user_id,article_title,cover_url,cover_url_small,article_remark,order_weight,create_time,update_time order by order_weight desc" +
            " limit #{2}, #{3}")
    public List<Article> getOffLineNumList(Long orgid, String time, Integer pageNum, Integer pageSize);

    @Update("update article set headline = 2, update_time = now() where article_cat_id = #{0} and headline = 1")
    public void setNotHeadLineByCatId(Long article_cat_id);

    @Select("select * from article where org_id = #{org_id} and article_id in(${article_ids})")
    List<Article> selectByOrgAndArticleIds(@Param("org_id") Long org_id, @Param("article_ids") String article_ids_str);

    @Select("select count(*) from article where is_delete = 2 and article_cat_id in(\n" +
            "select r.article_cat_id from article_cat_org_rel r \n" +
            "left join article_cat c on r.article_cat_id = c.article_cat_id\n" +
            "where r.org_id = #{0} and r.is_delete = 2  and c.is_delete = 2) and article_status =1")
    Integer getCountOrg(Long org_id);

    @Select("select count(*) from article where is_delete = 2 and article_cat_id in(\n" +
            "select r.article_cat_id from article_cat_org_rel r \n" +
            "left join article_cat c on r.article_cat_id = c.article_cat_id\n" +
            "where r.org_id = #{0} and r.is_delete = 2 and c.enabled =1 and c.is_delete = 2) and article_status =1")
    Integer getCountByOrgId(Long org_id);

    @Select("select count(*) from article where is_delete = 2 and article_cat_id in(\n" +
            "select r.article_cat_id from article_cat_org_rel r \n" +
            "left join article_cat c on r.article_cat_id = c.article_cat_id\n" +
            "where r.org_id = #{0} and r.is_delete = 2 and c.enabled =1 and c.is_delete = 2)")
    Integer getCount();

    @Select("select article_cat_id from article where article_id = #{0} limit 1 ")
    Long getCatId(Long article_id);

    @Select("select a.article_id, a.article_title, a.publish_time, a.article_remark, a.article_type, a.article_content, " +
            " r.article_cat_id from article_cat_org_rel r left join article a on r.article_cat_id=a.article_cat_id " +
            " where a.recommend = 1 and r.org_id = #{0} and r.is_delete =2 and a.is_delete =2 and " +
            " r.is_show = 1 and a.article_status = 1 order by a.update_time desc limit #{1}")
    List<Map<String,Object>> getRecommendArtListByOrgIdAndCount(Long org_id, Integer limit );
}
