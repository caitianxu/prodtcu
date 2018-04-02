package com.cjsz.tech.count.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.count.beans.SearchCountOrgBean;
import com.cjsz.tech.count.beans.SearchKeyBean;
import com.cjsz.tech.count.domain.SearchCount;
import com.cjsz.tech.count.mapper.SearchCountMapper;
import com.cjsz.tech.count.service.SearchCountService;
import com.cjsz.tech.utils.HttpClientUtil;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by LuoLi on 2017/3/23 0023.
 */
@Service
public class SearchCountServiceImpl implements SearchCountService {

    @Autowired
    private SearchCountMapper searchCountMapper;

    @Override
    public Object pageQuery(Sort sort, SearchCountOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<SearchCount> searchCounts = new ArrayList<SearchCount>();
        searchCounts = searchCountMapper.searchCountList(bean.getOrg_id(),bean.getSearchText());
        PageList result = new PageList(searchCounts, null);
        return result;
    }

    @Override
    public synchronized void saveSearchCount(SearchCount searchCount) {
        searchCount.setCreate_time(new Date());
        searchCount.setUpdate_time(new Date());
        searchCount.setOrder_weight(System.currentTimeMillis());
        searchCountMapper.insert(searchCount);
    }

    @Override
    public void updateSearchCount(SearchCount searchCount) {
        searchCount.setUpdate_time(new Date());
        searchCountMapper.updateByPrimaryKey(searchCount);
    }

    @Override
    public void delSearchCounts(String search_ids) {
        searchCountMapper.deleteByIds(search_ids);
    }

    @Override
    public SearchCount selectByNameAndOrgId(String searchText, Long org_id) {
        return searchCountMapper.selectByNameAndOrgId(searchText, org_id);
    }

    @Override
    public SearchCount selectById(Long search_id) {
        return searchCountMapper.selectById(search_id);
    }

    @Override
    public List<SearchCount> selectByOrgIdAndCount(Long org_id, Integer count) {
        return searchCountMapper.selectByOrgIdAndCount(org_id, count);
    }

    @Override
    public List<Map<String, Object>> selectByOrgId(Long org_id,Integer count){
        return searchCountMapper.selectByOrgId(org_id,count);
    }

    @Override
    public List<SearchCount> selectByOrgIdAndCount_v2(Long org_id, Long display) {
        return searchCountMapper.selectByOrgIdAndCount_v2(org_id,display);
    }

    //长江中文网关键词同步
    @Override
    public void saveKeyByCJZWW() {
        String result = HttpClientUtil.httpPostRequest("http://www.cjzww.com/interface/MobInterface/AppContent.php?act=hotSearch");
        SearchKeyBean keys = JSONObject.parseObject(result,SearchKeyBean.class);
        String[] key = keys.getKeys();
        Long count = searchCountMapper.selectMostCount();
        List<SearchCount> list = new ArrayList<>();
        for (int i = key.length-1;i>=0;i--){
            count+=1L;
            SearchCount name = searchCountMapper.selectByNameAndOrgId(key[i],189L);
            if (name != null){
                name.setSearch_count(count);
                searchCountMapper.updateByPrimaryKey(name);
            }else{
                SearchCount searchCount = new SearchCount();
                searchCount.setCreate_time(new Date());
                searchCount.setName(key[i]);
                searchCount.setOrg_id(189L);
                searchCount.setOrg_name("长江中文网");
                searchCount.setSearch_count(count);
                searchCount.setOrder_weight(System.currentTimeMillis());
                searchCount.setStatus(1);
                searchCount.setUpdate_time(new Date());
                list.add(searchCount);
            }
        }
        if(list != null && list.size() > 0) {
            searchCountMapper.insertList(list);
        }
    }
}
