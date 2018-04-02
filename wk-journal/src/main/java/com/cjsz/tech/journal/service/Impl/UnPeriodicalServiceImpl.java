package com.cjsz.tech.journal.service.Impl;


import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.journal.domain.PeriodicalRepo;
import com.cjsz.tech.journal.domain.UnPeriodical;
import com.cjsz.tech.journal.mapper.UnPeriodicalMapper;
import com.cjsz.tech.journal.service.UnPeriodicalService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
@Service
public class UnPeriodicalServiceImpl implements UnPeriodicalService {

    @Autowired
    private UnPeriodicalMapper unPeriodicalMapper;

    @Override
    public Object getUnPeriodicalAll(PageConditionBean ben, Sort sort) {
        PageHelper.startPage(ben.getPageNum(), ben.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<UnPeriodical> list = null;
        if (StringUtils.isNotEmpty(ben.getSearchText())) {
            list = unPeriodicalMapper.pageQuery(ben.getSearchText());
        } else {
            list = unPeriodicalMapper.pageQuery1();
        }
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public void insertUnPeriodical(UnPeriodical bean) {
        bean.setCreate_time(new Date());
        //读取文件数量
        bean.setS_num(0);
        //已经解析
        bean.setUn_success(0);
        //解析错误
        bean.setUn_error(0);
        //状态
        bean.setS_status(0);
        unPeriodicalMapper.insert(bean);
    }

    @Override
    public void saveUnPeriodical(UnPeriodical bean) {
        unPeriodicalMapper.updateByPrimaryKey(bean);
    }

    @Override
    public PeriodicalRepo getPeriodicalByFileName(String file_name){
        return  unPeriodicalMapper.getPeriodicalByFileName(file_name);
    }
    @Override
    public void updateUnStatus(String title){
        unPeriodicalMapper.updateUnStatus(title);
    }
    @Override
    public PeriodicalRepo getParentRepo(String series_name){
        return  unPeriodicalMapper.getParentRepo(series_name);
    }

    @Override
    public UnPeriodical getUnPeriodicalTop(){
        return  unPeriodicalMapper.getUnPeriodicalTop();
    }
}
