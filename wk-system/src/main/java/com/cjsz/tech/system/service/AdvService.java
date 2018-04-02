package com.cjsz.tech.system.service;

import com.cjsz.tech.system.beans.AdvOrgListBean;
import com.cjsz.tech.system.conditions.AdvCondition;
import com.cjsz.tech.system.domain.Adv;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2017/1/22 0022.
 */
public interface AdvService extends IOfflineService {
    /**
     * 分页列表：org_id = 1 发布了的 ，其他机构也要显示
     * @param sort
     * @param advCondition
     * @return
     */
    Object pageQuery(Sort sort,  AdvCondition advCondition);

    /**
     * 广告分类Ids查找广告
     * @param catIdsStr
     * @return
     */
    List<Adv> selectAdvByCatIds(String catIdsStr);

    /**
     * 添加广告
     * @param adv
     */
    void saveAdv(Adv adv);

    /**
     * 修改广告
     * @param adv
     */
    void updateAdv(Adv adv);

    /**
     * 根据广告Id删除广告
     * @param advIdsStr
     */
    void deleteByIds(String advIdsStr);

    /**
     * 根据广告分类Id删除广告
     * @param catIdsStr
     */
    void deleteAdvByCats(String catIdsStr);

    /**
     * 广告Ids查找广告
     * @param advIdsStr
     * @return
     */
    List<Adv> selectAdvByIds(String advIdsStr);

    /**
     * 广告Id查找广告
     * @param adv_id
     * @return
     */
    Adv selectAdvById(Long adv_id);

    /**
     * 获取分类下有限数量的广告
     * @param adv_cat_id
     * @param count
     * @return
     */
    List<Adv> selectAdvsByCatIdAndCount(Long adv_cat_id, Integer count);

    //获取广告分配机构列表
    Object getOrgList(AdvOrgListBean bean, Sort sort);

    //广告添加机构
    void addOrg(Long adv_id, Long org_id);

    //广告移除机构
    void removeOrg(Long adv_id, Long org_id);

    //更新广告是否显示
    void updateShow(Adv bean,Long org_id);

    //新增广告默认分配给所有机构
    void saveRelByAllOrg(Adv adv);

    //各机构修改广告排序
    void updateOrder(Long adv_id, Long order_weight, Long org_id);

    List<Adv> selectAdvsByOrgIdAndCatIdNum(Long adv_cat_id, Long org_id);
}
