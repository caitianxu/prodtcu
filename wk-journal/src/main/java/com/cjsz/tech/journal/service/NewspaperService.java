package com.cjsz.tech.journal.service;

import com.cjsz.tech.journal.beans.FindNewspaperBean;
import com.cjsz.tech.journal.beans.NewspaperBean;
import com.cjsz.tech.journal.domain.Newspaper;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Author:Jason
 * Date:2016/12/1
 */
public interface NewspaperService extends IOfflineService {


    /**
     * 分页查询本机构的报纸
     *
     * @param sort
     * @param bean
     * @return
     */
    Object pageQuery(Sort sort, FindNewspaperBean bean);

    /**
     * 分页查询本机构的报纸__前端
     *
     * @param sort
     * @param bean
     * @return
     */
    Object pageSiteQuery(Sort sort, FindNewspaperBean bean);

    /**
     * 添加报纸
     *
     * @param newspaper
     */
    Newspaper saveNewspaper(Newspaper newspaper);

    /**
     * 修改报纸
     *
     * @param newspaper
     */
    Newspaper updateNewspaper(Newspaper newspaper);

    /**
     * 通过报纸ID查找报纸
     *
     * @param newspaper_id
     * @return
     */
    Newspaper selectById(Long newspaper_id);

    /**
     * 通过分类IDs查找报纸
     *
     * @param cat_ids
     * @return
     */
    List<Newspaper> selectByCatIds(String cat_ids);

    /**
     * 通过IDs删除报纸
     *
     * @param newspaper_ids
     */
    void deleteByIds(String newspaper_ids);

    /**
     * 查询报纸的数量
     * @param org_id
     * @return
     */
    Integer getCountByOrgId(Long org_id);

    /**
     * 推荐
     * @param is_recommend
     * @param newspaper_id
     */
    void updateStatus(Integer is_recommend, Long newspaper_id);

    /**
     * 根据报纸ID查找报纸分类ID
     * @param newspaper_id
     * @return
     */
    Long getNewsCatId(Long newspaper_id);

    //修改报纸排序
    void updateOrder(Long newspaper_id, Long order_weight);
}
