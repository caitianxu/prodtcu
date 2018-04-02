package com.cjsz.tech.journal.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.journal.beans.FindCatOrgBean;
import com.cjsz.tech.journal.beans.JournalCatBean;
import com.cjsz.tech.journal.beans.JournalCatOrgBean;
import com.cjsz.tech.journal.domain.JournalCat;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface JournalCatService extends IOfflineService {

	/**
	 * 获取期刊分类
	 * @param org_id
	 * @return
	 */
	public List<JournalCatBean> getCats(Long org_id);

	/**
	 * 期刊分类树(本机构)
	 * @param org_id
	 * @return
	 */
	public List<JournalCatBean> getOrgCats(Long org_id);

	/**
	 * 将期刊分类转化为树形结构
	 * @param cats
	 * @return
	 */
	public List<JournalCatBean> selectTree(List<JournalCatBean> cats);

	/**
	 * 分类名称重复(分类名查找)
	 * @param root_org_id
	 * @param journal_cat_name
	 * @return
	 */
	public List<JournalCat> selectByCatName(Long root_org_id, String journal_cat_name);

	/**
	 * 保存分类
	 * @param cat
	 * @param org_id
	 */
	public JournalCatBean saveCat(JournalCatBean cat, Long org_id);

	/**
	 * 分类名称重复(分类名查找不包括本身)
	 * @param root_org_id
	 * @param journal_cat_name
	 * @param journal_cat_id
	 * @return
	 */
	public List<JournalCat> selectOtherByCatName(Long root_org_id, String journal_cat_name, Long journal_cat_id);

	/**
	 * 根据Id查找分类
	 * @param journal_cat_id
	 * @return
	 */
	public JournalCat selectByCatId(Long journal_cat_id);

	/**
	 * 修改分类
	 * @param cat
	 * @param org_id
	 */
	public JournalCatBean updateCat(JournalCatBean cat, Long org_id);

	/**
	 * 更新机构期刊分类是否启用状态
	 * @param bean
	 */
	public void updateEnabled(JournalCatBean bean);

	/**
	 * 更新机构期刊分类是否显示状态
	 * @param bean
	 */
	public void updateShow(JournalCatBean bean);

	/**
	 * 查询机构的期刊分类
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
	 * 根据cat_ids删除期刊和期刊分类
	 * @param cat_ids
	 */
	public void deleteAllByCatIds(String cat_ids);

	/**
	 * 获取前端期刊分类
	 * @param org_id
	 * @return
	 */
	public List<JournalCat> selectSiteCatsByOrgId(Long org_id);

	/**
	 * 期刊分类机构列表（添加分类至机构）
	 * @return
	 */
	public List<JournalCatOrgBean> getAddOrgs();

	/**
	 * 期刊分类机构列表（添加分类至机构）————分页
	 * @param sort
	 * @param bean
	 * @return
	 */
	public Object getAddOrgsPageQuery(Sort sort, PageConditionBean bean);

	/**
	 * 期刊分类机构列表（机构移除分类）
	 * @param journal_cat_id
	 * @return
	 */
	public List<JournalCatOrgBean> getRemoveOrgs(Long journal_cat_id);

	/**
	 * 期刊分类机构列表（机构移除分类）————分页
	 * @param sort
	 * @param bean
	 * @return
	 */
	public Object getRemoveOrgsPageQuery(Sort sort, FindCatOrgBean bean);

	/**
	 * 期刊分类添加机构
	 * @param journal_cat_id
	 * @param org_id
	 */
	public void addOrg(Long journal_cat_id, Long org_id);

	/**
	 * 期刊分类移除机构
	 * @param journal_cat_id
	 * @param org_id
	 */
	public void removeOrg(Long journal_cat_id, Long org_id);

	/**
	 * cat_path 查找本机构分类
	 * @param org_id
	 * @param journal_cat_path
	 * @return
	 */
	public List<JournalCatBean> getOwnerCats(Long org_id, String journal_cat_path);

	/**
	 * 期刊分类机构列表
	 * @param sort
	 * @param bean
	 * @return
	 */
	public Object getOrgsPageQuery(Sort sort, FindCatOrgBean bean);

	/**
	 * 根据机构ID，期刊分类ID查询是否为自建
	 * @param journal_cat_id
	 * @param org_id
	 * @return
	 */
	Long getCatId(Long journal_cat_id,Long org_id);

}
