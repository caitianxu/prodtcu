package com.cjsz.tech.book.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.domain.BookIndex;
import com.cjsz.tech.system.beans.SearchBean;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by LuoLi on 2017/4/15 0015.
 */
public interface BookIndexService {

    void addBookIndex();

    Object sitePageQuery(Sort sort, PageConditionBean bean, String type,Long org_id);

    Object pageQuery( SearchBean searchBean);

    List<BookIndex> selectCount1(Long bookid,Long org_id);

    void addCount1(BookIndex bookIndex);

    void updateCount(Long bookid,Long org_id);
}
