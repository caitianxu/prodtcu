package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.conditions.AppNavCondition;
import com.cjsz.tech.system.domain.AppKeyword;
import com.cjsz.tech.system.domain.AppNav;
import com.cjsz.tech.system.mapper.AppKeywordMapper;
import com.cjsz.tech.system.mapper.AppNavMapper;
import com.cjsz.tech.system.service.AppKeywordService;
import com.cjsz.tech.system.service.AppNavService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
@Service
public class AppKeywordServiceImpl implements AppKeywordService {

    @Autowired
    private AppKeywordMapper appKeywordMapper;

    @Override
    public List<AppKeyword> getAppKeywordBySearch(String searchText) {
        List<AppKeyword> appKeywordList = new ArrayList<AppKeyword>();
        if(StringUtil.isEmpty(searchText)){
            appKeywordList = appKeywordMapper.getAppKeywordList();
        }else{
            appKeywordList = appKeywordMapper.getAppKeywordBySearch(searchText);
        }
        return appKeywordList;
    }

    @Override
    public void saveAppKeyword(AppKeyword appKeyword) {
        appKeywordMapper.insert(appKeyword);
    }

    @Override
    public void updateAppKeyword(AppKeyword appKeyword) {
        AppKeyword appKeyword1 = appKeywordMapper.selectByKeywordId(appKeyword.getKeyword_id());
        BeanUtils.copyProperties(appKeyword, appKeyword1);
        appKeywordMapper.updateByPrimaryKey(appKeyword);
    }

    @Override
    public void deleteByKeywordIds(String keyword_ids_str) {
        appKeywordMapper.deleteByKeywordIds(keyword_ids_str);
    }

    @Override
    public AppKeyword selectByKeywordId(Long keyword_id) {
        return appKeywordMapper.selectByKeywordId(keyword_id);
    }
}
