package com.cjsz.tech.journal.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.journal.beans.FindCatOrgBean;
import com.cjsz.tech.journal.beans.NewspaperCatBean;
import com.cjsz.tech.journal.beans.NewspaperCatOrgBean;
import com.cjsz.tech.journal.domain.NewspaperCat;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Author:Jason
 * Date:2016/12/2
 */
public interface NewspaperCatService extends IOfflineService{
    /**
     * 获取报纸分类
     * @param org_id
     * @return
     */
    public List<NewspaperCatBean> getCats(Long org_id);

    /**
     * 报纸分类树(本机构)
     * @param org_id
     * @return
     */
    public List<NewspaperCatBean> getOrgCats(Long org_id);

    /**
     * 将报纸分类转化为树形结构
     * @param cats
     * @return
     */
    public List<NewspaperCatBean> selectTree(List<NewspaperCatBean> cats);

    /**
     * 分类名称重复(分类名查找)
     * @param root_org_id
     * @param newspaper_cat_name
     * @return
     */
    public List<NewspaperCat> selectByCatName(Long root_org_id, String newspaper_cat_name);

    /**
     * 保存分类
     * @param cat
     * @param org_id
     */
    public NewspaperCatBean saveCat(NewspaperCatBean cat, Long org_id);

    /**
     * 分类名称重复(分类名查找不包括本身)
     * @param root_org_id
     * @param newspaper_cat_name
     * @param newspaper_cat_id
     * @return
     */
    public List<NewspaperCat> selectOtherByCatName(Long root_org_id, String newspaper_cat_name, Long newspaper_cat_id);

    /**
     * 根据Id查找分类
     * @param newspaper_cat_id
     * @return
     */
    public NewspaperCat selectByCatId(Long newspaper_cat_id);

    /**
     * 修改分类
     * @param cat
     * @param org_id
     */
    public NewspaperCatBean updateCat(NewspaperCatBean cat, Long org_id);

    /**
     * 更新机构报纸分类是否启用状态
     * @param bean
     */
    public void updateEnabled(NewspaperCatBean bean);

    /**
     * 更新机构报纸分类是否显示状态
     * @param bean
     */
    public void updateShow(NewspaperCatBean bean);

    /**
     * 查询机构的报纸分类
     * @param org_id
     * @return
     */
    public List<Long> selectOrgCatIds(Long org_id);

    /**
     * 通过cat_ids找到full_paths
     * @param cat_ids
     * @return
     */
    public List<String> getFullPathsByCatIds(String cat_ids);

    /**
     * 通过full_path获取自身和所有子集的catId合集
     * @param full_path
     * @return
     */
    public List<Long> getCatIdsByFullPath(String full_path);

    /**
     * 根据cat_ids删除报纸和报纸分类
     * @param cat_ids
     */
    public void deleteAllByCatIds(String cat_ids);

    /**
     * 获取前端报纸分类
     * @param org_id
     * @return
     */
    public List<NewspaperCat> selectSiteCatsByOrgId(Long org_id);

    /**
     * 报纸分类机构列表（添加分类至机构）
     * @return
     */
    public List<NewspaperCatOrgBean> getAddOrgs();

    /**
     * 报纸分类机构列表（添加分类至机构）————分页
     * @param sort
     * @param bean
     * @return
     */
    public Object getAddOrgsPageQuery(Sort sort, PageConditionBean bean);

    /**
     * 报纸分类机构列表（机构移除分类）
     * @param newspaper_cat_id
     * @return
     */
    public List<NewspaperCatOrgBean> getRemoveOrgs(Long newspaper_cat_id);

    /**
     * 报纸分类机构列表（机构移除分类）————分页
     * @param sort
     * @param bean
     * @return
     */
    public Object getRemoveOrgsPageQuery(Sort sort, FindCatOrgBean bean);

    /**
     * 报纸分类添加机构
     * @param newspaper_cat_id
     * @param org_id
     */
    public void addOrg(Long newspaper_cat_id, Long org_id);

    /**
     * 报纸分类移除机构
     * @param newspaper_cat_id
     * @param org_id
     */
    public void removeOrg(Long newspaper_cat_id, Long org_id);

    /**
     * cat_path 查找本机构分类
     * @param org_id
     * @param newspaper_cat_path
     * @return
     */
    public List<NewspaperCatBean> getOwnerCats(Long org_id, String newspaper_cat_path);

    /**
     * 报纸分类机构列表
     * @param sort
     * @param bean
     * @return
     */
    public Object getOrgsPageQuery(Sort sort, FindCatOrgBean bean);

    /**
     * 根据机构ID报纸ID查询是否是自建
     * @param cat_id
     * @param org_id
     * @return
     */
    Long getCatId(Long cat_id,Long org_id);

    /**
     * 网站获取所有报纸分类转树形结构
     * @param org_id
     * @return
     */
    List<NewspaperCatBean> getAllCats(Long org_id);

    //修改机构报纸分类排序
    void orderByOrg(Long newspaper_cat_id, Long order_weight, Long org_id);
}
