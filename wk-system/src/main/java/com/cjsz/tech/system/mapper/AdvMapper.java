package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.beans.AdvOrgListBean;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.provider.AdvProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/22 0022.
 */
public interface AdvMapper extends BaseMapper<Adv> {

    //广告分类Ids查找广告
    @Select("select * from adv where is_delete = 2 and adv_cat_id in(${catIdsStr})")
    List<Adv> selectAdvByCatIds(@Param("catIdsStr") String catIdsStr);

    //通过分类删除广告
    @Update("update adv set is_delete = 1,update_time = now() where adv_cat_id in(${catIdsStr})")
    void deleteAdvByCats(@Param("catIdsStr") String catIdsStr);

    //根据广告Id删除广告
    @Update("update adv set is_delete = 1,update_time = now() where adv_id in(${advIdsStr})")
    void deleteByIds(@Param("advIdsStr") String advIdsStr);

    //广告列表：org_id = 1 发布了的 ，其他机构也要显示
    @SelectProvider(type = AdvProvider.class, method = "getAdvList")
    List<Adv> getAdvList(@Param("searchText") String searchText, @Param("adv_cat_id") Long adv_cat_id, @Param("org_id") Long org_id);

    //广告Ids查找广告
    @Select("select * from adv where is_delete = 2 and adv_id in(${advIdsStr})")
    List<Adv> selectAdvByIds(@Param("advIdsStr") String advIdsStr);

    //广告Id查找广告
    @Select("select * from adv where adv_id = #{0} and is_delete = 2 ")
    public Adv selectAdvById(Long adv_id);

    @Select("select count(*) as num from adv where (org_id = #{0} or org_id = 1) and update_time >#{1}")
    public Integer checkOffLineNum(Long orgid, String timev);

    @Select("select a.*,r.is_show,r.is_delete as org_is_delete,r.order_weight as org_order_weight from adv_org_rel r left join adv a on a.adv_id = r.adv_id where r.org_id = #{0} and " +
            " ( a.update_time >#{1} or  r.update_time >#{1} or  a.create_time >#{1} or  r.create_time >#{1})" +
            " order by r.org_id asc,r.order_weight desc limit #{2}, #{3}")
    public List<Adv> getOffLineNumList(Long orgid, String timev, Integer pageNum, Integer pageSize);

    //获取分类下有限数量的广告
    @Select("select * from adv where is_delete = 2 and enabled = 1 and adv_cat_id = #{0} order by order_weight desc limit #{1}")
    public List<Adv> selectAdvsByCatIdAndCount(Long adv_cat_id, Integer count);

    //获取长江科技以及本机构下指定分类下的广告和banner(所有，不限数量)
    @Select("select a.* from adv_org_rel r left join adv a on a.adv_id = r.adv_id where a.is_delete = 2 and r.is_delete = 2 and a.enabled = 1 and r.is_show =1 and r.org_id = #{1} and a.adv_cat_id = #{0} order by r.org_id asc,r.order_weight desc")
    List<Adv> selectAdvsByOrgIdAndCatId(Long adv_cat_id, Long org_id);

    @SelectProvider(type = AdvProvider.class, method = "getOrgList")
    List<AdvOrgListBean> getOrgList(@Param("bean") AdvOrgListBean bean);

    @Update("update adv_org_rel set is_show = 2 ,update_time = now() where adv_id = #{0}")
    void updateShow(Long adv_id);

    //获取长江科技以及本机构下指定分类下的广告和banner(所有，不限数量)
    @Select("select a.* from adv_org_rel r left join adv a on a.adv_id = r.adv_id where a.is_delete = 2 and a.enabled = 1 and r.is_show =1 and r.org_id = #{1} and a.adv_cat_id = #{0} order by r.org_id asc,r.order_weight desc limit 6")
    List<Adv> selectAdvsByOrgIdAndCatIdNum(Long adv_cat_id, Long org_id);

}
