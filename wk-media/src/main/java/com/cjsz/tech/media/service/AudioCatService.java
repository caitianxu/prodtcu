package com.cjsz.tech.media.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.media.beans.FindCatOrgBean;
import com.cjsz.tech.media.beans.AudioCatBean;
import com.cjsz.tech.media.beans.AudioCatOrgBean;
import com.cjsz.tech.media.domain.AudioCat;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Li Yi on 2016/12/7.
 */
public interface AudioCatService extends IOfflineService {

    /**
     * 获取音频分类
     * @param org_id
     * @return
     */
    public List<AudioCatBean> getCats(Long org_id);

    /**
     * 音频分类树(本机构)
     * @param org_id
     * @return
     */
    public List<AudioCatBean> getOrgCats(Long org_id);

    /**
     * 将音频分类转化为树形结构
     * @param cats
     * @return
     */
    public List<AudioCatBean> selectTree(List<AudioCatBean> cats);

    /**
     * 分类名称重复(分类名查找)
     * @param root_org_id
     * @param audio_cat_name
     * @return
     */
    public List<AudioCat> selectByCatName(Long root_org_id, String audio_cat_name);

    /**
     * 保存分类
     * @param bean
     * @param org_id
     */
    public AudioCatBean saveCat(AudioCatBean bean, Long org_id);

    /**
     * 分类名称重复(分类名查找不包括本身)
     * @param root_org_id
     * @param audio_cat_name
     * @param audio_cat_id
     * @return
     */
    public List<AudioCat> selectOtherByCatName(Long root_org_id, String audio_cat_name, Long audio_cat_id);

    /**
     * 根据Id查找分类
     * @param audio_cat_id
     * @return
     */
    public AudioCat selectByCatId(Long audio_cat_id);

    /**
     * 修改分类
     * @param bean
     * @param org_id
     */
    public AudioCatBean updateCat(AudioCatBean bean, Long org_id);

    /**
     * 更新机构音频分类是否启用状态
     * @param bean
     */
    public void updateEnabled(AudioCatBean bean);

    /**
     * 更新机构音频分类是否显示状态
     * @param bean
     */
    public void updateShow(AudioCatBean bean);

    /**
     * 查询机构的音频分类
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
     * 根据cat_ids删除音频和音频分类
     * @param cat_ids
     */
    public void deleteAllByCatIds(String cat_ids);

    /**
     * 获取前端音频分类
     * @param org_id
     * @return
     */
    public List<AudioCat> selectSiteCatsByOrgId(Long org_id);

    /**
     * 音频分类机构列表（添加分类至机构）
     * @return
     */
    public List<AudioCatOrgBean> getAddOrgs();

    /**
     * 音频分类机构列表（添加分类至机构）————分页
     * @param sort
     * @param bean
     * @return
     */
    public Object getAddOrgsPageQuery(Sort sort, PageConditionBean bean);

    /**
     * 音频分类机构列表（机构移除分类）
     * @param audio_cat_id
     * @return
     */
    public List<AudioCatOrgBean> getRemoveOrgs(Long audio_cat_id);

    /**
     * 音频分类机构列表（机构移除分类）————分页
     * @param sort
     * @param bean
     * @return
     */
    public Object getRemoveOrgsPageQuery(Sort sort, FindCatOrgBean bean);

    /**
     * 音频分类添加机构
     * @param audio_cat_id
     * @param org_id
     */
    public void addOrg(Long audio_cat_id, Long org_id);

    /**
     * 音频分类移除机构
     * @param audio_cat_id
     * @param org_id
     */
    public void removeOrg(Long audio_cat_id, Long org_id);

    /**
     * cat_path 查找本机构分类
     * @param org_id
     * @param audio_cat_path
     * @return
     */
    public List<AudioCatBean> getOwnerCats(Long org_id, String audio_cat_path);

    /**
     * 音频分类机构列表
     * @param sort
     * @param bean
     * @return
     */
    public Object getOrgsPageQuery(Sort sort, FindCatOrgBean bean);

    /**
     * 根据机构ID分类ID查询是否为自建分类
     * @param audio_cat_id
     * @param org_id
     * @return
     */
    Long getAudioCatId(Long audio_cat_id,Long org_id);
}
