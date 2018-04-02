package com.cjsz.tech.system.service;

import com.cjsz.tech.system.conditions.AppNavCondition;
import com.cjsz.tech.system.domain.AppKeyword;
import com.cjsz.tech.system.domain.AppNav;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public interface AppKeywordService {

    /**
     * 关键词搜索列表
     * @param searchText
     * @return
     */
    public List<AppKeyword> getAppKeywordBySearch(String searchText);

    /**
     * 新增导航
     * @param appKeyword
     */
    public void saveAppKeyword(AppKeyword appKeyword);

    /**
     * 修改导航
     * @param appKeyword
     */
    public void updateAppKeyword(AppKeyword appKeyword);

    /**
     * 删除导航
     * @param keyword_ids_str     1,2,3
     */
    public void deleteByKeywordIds(String keyword_ids_str);

    /**
     * 通过keyword_id查询
     * @param keyword_id
     * @return
     */
    public AppKeyword selectByKeywordId(Long keyword_id);

}
