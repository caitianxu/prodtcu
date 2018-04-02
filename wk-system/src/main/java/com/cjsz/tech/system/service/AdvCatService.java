package com.cjsz.tech.system.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.domain.AdvCat;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2017/1/22 0022.
 */
public interface AdvCatService extends IOfflineService {

    /**
     * 广告分类列表————分页
     * @param sort
     * @param org_id
     * @param bean
     * @return
     */
    Object pageQuery(Sort sort, Long org_id, PageConditionBean bean);

    /**
     * 广告分类列表
     * @param org_id
     * @return
     */
    List<AdvCat> getAllAdvCat(Long org_id);

    /**
     * 广告分类添加
     * @param advCat
     */
    void saveAdvCat(AdvCat advCat);

    /**
     * 广告分类修改
     * @param advCat
     */
    void updateAdvCat(AdvCat advCat);

    /**
     * 删除同时删除分类下广告
     * @param catIdsStr
     */
    void deleteAdvCats(String catIdsStr);

    /**
     * 分类名查找
     * @param adv_cat_name
     * @return
     */
    List<AdvCat> selectByCatName(String adv_cat_name);

    /**
     * 分类名查找不包括本身
     * @param adv_cat_name
     * @param adv_cat_id
     * @return
     */
    List<AdvCat> selectOtherByCatName(String adv_cat_name, Long adv_cat_id);

    /**
     * 分类编号查找分类
     * @param adv_cat_code
     * @return
     */
    AdvCat selectByCatCode(String adv_cat_code);

    /**
     * 分类编号查找不包括本身
     * @param adv_cat_code
     * @param adv_cat_id
     * @return
     */
    AdvCat selectOtherByCatCode(String adv_cat_code, Long adv_cat_id);

    /**
     * 根据项目code查询广告分类
     * @param project_code
     * @return
     */
    List<AdvCat> getAdvCatByProjectCode(String project_code);
}
