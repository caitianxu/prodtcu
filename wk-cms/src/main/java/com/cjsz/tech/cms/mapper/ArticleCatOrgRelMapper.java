package com.cjsz.tech.cms.mapper;

import com.cjsz.tech.cms.beans.ArticleCatBean;
import com.cjsz.tech.cms.domain.ArticleCatOrgRel;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by LuoLi on 2017/4/24 0024.
 */
public interface ArticleCatOrgRelMapper extends BaseMapper<ArticleCatOrgRel> {

    //关系表分类表联合详细分类信息
    @Select("select r.*, c.pid, c.article_cat_name, c.article_cat_path, c.enabled, c.remark, c.is_delete, c.org_count from article_cat_org_rel r " +
            "left join article_cat c on r.article_cat_id = c.article_cat_id " +
            "where r.org_id = #{0} and r.article_cat_id = #{1}")
    public ArticleCatBean seletInfoByOrgIdAndCatId(Long org_id, Long article_cat_id);

    //关系Id查询关系
    @Select("select * from article_cat_org_rel where rel_id = #{0}")
    public ArticleCatOrgRel selectById(Long rel_id);

    //更新排序
    @Update("update article_cat_org_rel set order_weight = #{0},update_time = now() where rel_id = #{1}")
    public void updateOrderById(Long order_weight, Long rel_id);

    //机构Id分类Id查询关系
    @Select("select * from article_cat_org_rel where org_id = #{0} and article_cat_id = #{1}")
    public ArticleCatOrgRel selectByOrgIdAndCatId(Long org_id, Long article_cat_id);

    @Update("update article_cat_org_rel set is_delete = #{1}, update_time = now() where rel_id = #{0} and is_delete != #{1} ")
    public void updateIsDelete(Long rel_id, Integer is_delete);

    @Update("update article_cat_org_rel set is_delete = 1, update_time = now() where article_cat_id in (${cat_ids}) ")
    public void deleteRelsByIds(@Param("cat_ids") String cat_ids);

    @Select("select * from article_cat_org_rel where article_cat_id = #{0}")
    public List<ArticleCatOrgRel> selectListByCatId(Long cat_id);

    @Update("update article_cat_org_rel set order_weight = #{1},update_time = now() where article_cat_id = #{0} and org_id = #{2}")
    void orderByOrg(Long article_cat_id, Long order_weight, Long org_id);
}
