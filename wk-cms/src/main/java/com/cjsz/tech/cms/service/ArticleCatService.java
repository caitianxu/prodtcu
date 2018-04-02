package com.cjsz.tech.cms.service;

import java.util.List;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.cms.beans.ArticleCatBean;
import com.cjsz.tech.cms.beans.ArticleCatOrgBean;
import com.cjsz.tech.cms.beans.FindCatOrgBean;
import com.cjsz.tech.cms.domain.ArticleCat;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface ArticleCatService extends IOfflineService {

	/**
	 * 获取资讯分类
	 * @param org_id
	 * @return
	 */
	public List<ArticleCatBean> getCats(Long org_id);

	/**
	 * 资讯分类树(本机构)
	 * @param org_id
	 * @return
	 */
	public List<ArticleCatBean> getOrgCats(Long org_id);

	/**
	 * 将资讯分类转化为树形结构
	 * @param cats
	 * @return
	 */
	public List<ArticleCatBean> selectTree(List<ArticleCatBean> cats);

	/**
	 * 分类名称重复(分类名查找)
	 * @param root_org_id
	 * @param article_cat_name
	 * @return
	 */
	public List<ArticleCat> selectByCatName(Long root_org_id, String article_cat_name);

	/**
	 * 保存分类
	 * @param cat
	 * @param org_id
	 */
	public ArticleCatBean saveCat(ArticleCatBean cat, Long org_id);

	/**
	 * 分类名称重复(分类名查找不包括本身)
	 * @param root_org_id
	 * @param article_cat_name
	 * @param article_cat_id
	 * @return
	 */
	public List<ArticleCat> selectOtherByCatName(Long root_org_id, String article_cat_name, Long article_cat_id);

	/**
	 * 根据Id查找分类
	 * @param article_cat_id
	 * @return
	 */
	public ArticleCat selectByCatId(Long article_cat_id);

	/**
	 * 修改分类
	 * @param cat
	 * @param org_id
	 */
	public ArticleCatBean updateCat(ArticleCatBean cat, Long org_id);

	/**
	 * 更新机构资讯分类是否启用状态
	 * @param bean
	 */
	public void updateEnabled(ArticleCatBean bean);

	/**
	 * 更新机构资讯分类是否显示状态
	 * @param bean
	 */
	public void updateShow(ArticleCatBean bean);

	/**
	 * 查询机构的资讯分类
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
	 * 根据cat_ids删除资讯和资讯分类
	 * @param cat_ids
	 */
	public void deleteAllByCatIds(String cat_ids);

	/**
	 * 获取前端资讯分类
	 * @param org_id
	 * @return
	 */
	public List<ArticleCat> selectSiteCatsByOrgId(Long org_id);

	/**
	 * 资讯分类机构列表（添加分类至机构）
	 * @return
	 */
	public List<ArticleCatOrgBean> getAddOrgs();

	/**
	 * 资讯分类机构列表（添加分类至机构）————分页
	 * @param sort
	 * @param bean
	 * @return
	 */
	public Object getAddOrgsPageQuery(Sort sort, PageConditionBean bean);

	/**
	 * 资讯分类机构列表（机构移除分类）
	 * @param article_cat_id
	 * @return
	 */
	public List<ArticleCatOrgBean> getRemoveOrgs(Long article_cat_id);

	/**
	 * 资讯分类机构列表（机构移除分类）————分页
	 * @param sort
	 * @param bean
	 * @return
	 */
	public Object getRemoveOrgsPageQuery(Sort sort, FindCatOrgBean bean);

	/**
	 * 资讯分类添加机构
	 * @param article_cat_id
	 * @param org_id
	 */
	public void addOrg(Long article_cat_id, Long org_id);

	/**
	 * 资讯分类移除机构
	 * @param article_cat_id
	 * @param org_id
	 */
	public void removeOrg(Long article_cat_id, Long org_id);

	/**
	 * cat_path 查找本机构分类
	 * @param org_id
	 * @param article_cat_path
	 * @return
	 */
	public List<ArticleCatBean> getOwnerCats(Long org_id, String article_cat_path);

	/**
	 * 资讯分类机构列表
	 * @param sort
	 * @param bean
	 * @return
	 */
	public Object getOrgsPageQuery(Sort sort, FindCatOrgBean bean);

	/**
	 * 根据机构ID，分类ID查找分类是否是本机构创建
	 * @param cat_id
	 * @param org_id
	 * @return
	 */
	ArticleCat selectSourceByCatId(Long cat_id,Long org_id);

	//获取机构所有分类转为树形结构
    List<ArticleCatBean> getAllCats(Long org_id);

    //修改机构资讯分类排序
    void orderByOrg(Long article_cat_id, Long order_weight, Long org_id);
}
