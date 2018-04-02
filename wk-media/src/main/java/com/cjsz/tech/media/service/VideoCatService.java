package com.cjsz.tech.media.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.media.beans.FindCatOrgBean;
import com.cjsz.tech.media.beans.VideoCatBean;
import com.cjsz.tech.media.beans.VideoCatOrgBean;
import com.cjsz.tech.media.domain.VideoCat;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Li Yi on 2016/12/6.
 */
public interface VideoCatService extends IOfflineService {

    /**
     * 获取视频分类
     * @param org_id
     * @return
     */
    public List<VideoCatBean> getCats(Long org_id);

    /**
     * 视频分类树(本机构)
     * @param org_id
     * @return
     */
    public List<VideoCatBean> getOrgCats(Long org_id);

    /**
     * 将视频分类转化为树形结构
     * @param cats
     * @return
     */
    public List<VideoCatBean> selectTree(List<VideoCatBean> cats);

    /**
     * 分类名称重复(分类名查找)
     * @param root_org_id
     * @param video_cat_name
     * @return
     */
    public List<VideoCat> selectByCatName(Long root_org_id, String video_cat_name);

    /**
     * 保存分类
     * @param bean
     * @param org_id
     */
    public VideoCatBean saveCat(VideoCatBean bean, Long org_id);

    /**
     * 分类名称重复(分类名查找不包括本身)
     * @param root_org_id
     * @param video_cat_name
     * @param video_cat_id
     * @return
     */
    public List<VideoCat> selectOtherByCatName(Long root_org_id, String video_cat_name, Long video_cat_id);

    /**
     * 根据Id查找分类
     * @param video_cat_id
     * @return
     */
    public VideoCat selectByCatId(Long video_cat_id);

    /**
     * 修改分类
     * @param bean
     * @param org_id
     */
    public VideoCatBean updateCat(VideoCatBean bean, Long org_id);

    /**
     * 更新机构视频分类是否启用状态
     * @param bean
     */
    public void updateEnabled(VideoCatBean bean);

    /**
     * 更新机构视频分类是否显示状态
     * @param bean
     */
    public void updateShow(VideoCatBean bean);

    /**
     * 查询机构的视频分类
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
     * 根据cat_ids删除视频和视频分类
     * @param cat_ids
     */
    public void deleteAllByCatIds(String cat_ids);

    /**
     * 获取前端视频分类
     * @param org_id
     * @return
     */
    public List<VideoCat> selectSiteCatsByOrgId(Long org_id);

    /**
     * 视频分类机构列表（添加分类至机构）
     * @return
     */
    public List<VideoCatOrgBean> getAddOrgs();

    /**
     * 视频分类机构列表（添加分类至机构）————分页
     * @param sort
     * @param bean
     * @return
     */
    public Object getAddOrgsPageQuery(Sort sort, PageConditionBean bean);

    /**
     * 视频分类机构列表（机构移除分类）
     * @param video_cat_id
     * @return
     */
    public List<VideoCatOrgBean> getRemoveOrgs(Long video_cat_id);

    /**
     * 视频分类机构列表（机构移除分类）————分页
     * @param sort
     * @param bean
     * @return
     */
    public Object getRemoveOrgsPageQuery(Sort sort, FindCatOrgBean bean);

    /**
     * 视频分类添加机构
     * @param video_cat_id
     * @param org_id
     */
    public void addOrg(Long video_cat_id, Long org_id);

    /**
     * 视频分类移除机构
     * @param video_cat_id
     * @param org_id
     */
    public void removeOrg(Long video_cat_id, Long org_id);

    /**
     * cat_path 查找本机构分类
     * @param org_id
     * @param video_cat_path
     * @return
     */
    public List<VideoCatBean> getOwnerCats(Long org_id, String video_cat_path);

    /**
     * 视频分类机构列表
     * @param sort
     * @param bean
     * @return
     */
    public Object getOrgsPageQuery(Sort sort, FindCatOrgBean bean);

    /**
     * 根据视频ID机构ID查询是否为自建
     * @param video_cat_id
     * @param org_id
     * @return
     */
    Long getCatId(Long video_cat_id,Long org_id);

}
