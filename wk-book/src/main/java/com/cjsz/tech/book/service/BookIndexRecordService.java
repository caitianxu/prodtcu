package com.cjsz.tech.book.service;

import com.cjsz.tech.book.domain.BookIndexRecord;
import com.cjsz.tech.system.beans.SearchBean;
import org.springframework.data.domain.Sort;

/**
 * Created by LuoLi on 2017/4/15 0015.
 */
public interface BookIndexRecordService {

    public void addRecord(BookIndexRecord bookIndexRecord);

    Object pageQuery( SearchBean searchBean);
}
