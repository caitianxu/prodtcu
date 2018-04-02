package com.cjsz.tech.book.service.Impl;


import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.UnBook;
import com.cjsz.tech.book.mapper.UnBookMapper;
import com.cjsz.tech.book.service.UnBookService;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
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
public class UnBookServiceImpl implements UnBookService {

    @Autowired
    private UnBookMapper unBookMapper;

    @Override
    public void insertUnBook(UnBook bean) {
        bean.setCreate_time(new Date());
        //读取文件数量
        bean.setS_num(0);
        //已经解析
        bean.setUn_success(0);
        //解析错误
        bean.setUn_error(0);
        //状态
        bean.setS_status(0);
        unBookMapper.insert(bean);
    }

    @Override
    public void saveUnBook(UnBook bean) {
        unBookMapper.updateByPrimaryKey(bean);
    }

    @Override
    public Object getUnBookAll(PageConditionBean ben, Sort sort) {
        PageHelper.startPage(ben.getPageNum(), ben.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<UnBook> list = null;
        if (StringUtils.isNotEmpty(ben.getSearchText())) {
            list = unBookMapper.pageQuery(ben.getSearchText());
        } else {
            list = unBookMapper.pageQuery1();
        }
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public BookRepo getBookByFileName(String file_name){
        return unBookMapper.getBookByFileName(file_name);
    }
}
