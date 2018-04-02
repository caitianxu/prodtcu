package com.cjsz.tech.count.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.count.beans.SearchCountOrgBean;
import com.cjsz.tech.count.domain.SearchCount;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Created by LuoLi on 2017/3/23 0023.
 */
public interface SearchCountService {

    /**
     * 项目搜索记录————分页
     * @param sort
     * @param bean
     * @return
     */
    public Object pageQuery(Sort sort, SearchCountOrgBean bean);

    /**
     * 项目搜索记录————新增
     * @param searchCount
     */
    public void saveSearchCount(SearchCount searchCount);

    /**
     * 项目搜索记录————修改
     * @param searchCount
     */
    public void updateSearchCount(SearchCount searchCount);

    /**
     * 项目搜索记录————删除
     * @param search_ids
     */
    public void delSearchCounts(String search_ids);

    /**
     * 项目搜索记录————搜索内容精确查找
     * @param searchText
     * @param org_id
     * @return
     */
    public SearchCount selectByNameAndOrgId(String searchText, Long org_id);

    /**
     * 项目搜索记录————Id查找
     * @param search_id
     * @return
     */
    SearchCount selectById(Long search_id);

    /**
     * 项目首页热门搜索
     * @param org_id
     * @param count
     * @return
     */
    List<SearchCount> selectByOrgIdAndCount(Long org_id, Integer count);

    //机构搜索热词
    List<Map<String, Object>> selectByOrgId(Long org_id,Integer count);
    /**
     * 项目首页热门搜索-v2
     * @param org_id
     * @param display
     * @return
     */
    public List<SearchCount> selectByOrgIdAndCount_v2(Long org_id, Long display);

    //取长江中文网前100条热搜词
    void saveKeyByCJZWW();
}
