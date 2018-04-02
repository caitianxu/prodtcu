package com.cjsz.tech.system.service;

import com.cjsz.tech.system.conditions.AppNavCondition;
import com.cjsz.tech.system.domain.AppNav;
import org.springframework.data.domain.Sort;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public interface AppNavService {

    /**
     * 导航分页搜索查询
     * @param sort
     * @param condition
     * @return
     */
    public Object pageQuery(Sort sort, AppNavCondition condition);

    /**
     * 获取机构导航
     * @param org_id
     * @param searchText
     * @return
     */
    public Object getListByOrgIdAndSearch(Long org_id, String searchText);

    /**
     * 新增导航
     * @param appNav
     */
    public void saveAppNav(AppNav appNav);

    /**
     * 修改导航
     * @param appNav
     */
    public void updateAppNav(AppNav appNav);

    /**
     * 删除导航
     * @param nav_ids_str     1,2,3
     */
    public void deleteByNavIds(String nav_ids_str);

    /**
     * 通过nav_id查询
     * @param nav_id
     * @return
     */
    public AppNav selectByNavId(Long nav_id);

}
