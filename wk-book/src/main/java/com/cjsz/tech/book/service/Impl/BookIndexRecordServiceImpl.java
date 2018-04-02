package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.book.domain.BookIndexRecord;
import com.cjsz.tech.book.mapper.BookIndexRecordMapper;
import com.cjsz.tech.book.service.BookIndexRecordService;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.beans.SearchBean;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LuoLi on 2017/4/15 0015.
 */
@Service
public class BookIndexRecordServiceImpl implements BookIndexRecordService {

    @Autowired
    private BookIndexRecordMapper bookIndexRecordMapper;

    @Override
    public void addRecord(BookIndexRecord bookIndexRecord) {
        bookIndexRecord.setCreate_time(new Date());
        bookIndexRecordMapper.insert(bookIndexRecord);
    }

    @Override
    public Object pageQuery( SearchBean searchBean) {
        PageHelper.startPage(searchBean.getPageNum(), searchBean.getPageSize());
        List<BookIndexRecord> list = bookIndexRecordMapper.pageQuery(searchBean);
        PageList pageList = new PageList(list, null);
        return pageList;
    }


}
