package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.domain.BookIndex;
import com.cjsz.tech.book.domain.BookIndexRecord;
import com.cjsz.tech.book.domain.BookOrgRel;
import com.cjsz.tech.book.mapper.BookIndexMapper;
import com.cjsz.tech.book.mapper.BookOrgRelMapper;
import com.cjsz.tech.book.service.BookIndexService;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.beans.SearchBean;
import com.cjsz.tech.utils.DateUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by LuoLi on 2017/4/15 0015.
 */
@Service
public class BookIndexServiceImpl implements BookIndexService {

    @Autowired
    private BookIndexMapper bookIndexMapper;

    @Autowired
    private BookOrgRelMapper bookOrgRelMapper;

    @Override
    public void addBookIndex() {
        //每天0点更新数据：查询前一天图书记录，根据（机构、图书）统计————点击 收藏 分享 评论 阅读 综合指数：点击*1+收藏*10+分享*20+评论*30+阅读*1
        //1:点击详情;2:收藏;3:分享;4:评论;5:阅读
        List<BookIndex> bookIndexList = new ArrayList<BookIndex>();
        List<Map<String, Object>> dataList = bookIndexMapper.selectYesterdayData();

        for(Map<String, Object> data : dataList){
            BookIndex bookIndex = new BookIndex(Long.valueOf(data.get("org_id").toString()), Long.valueOf(data.get("book_id").toString()),Integer.valueOf(data.get("book_type").toString()),
                    Long.valueOf(data.get("click_count").toString()), Long.valueOf(data.get("collect_count").toString()), Long.valueOf(data.get("share_count").toString()),
                    Long.valueOf(data.get("review_count").toString()), Long.valueOf(data.get("read_count").toString()), Long.valueOf(data.get("unite_index").toString()),
                    DateUtils.getDaysTime(new Date(), -1));
            bookIndexList.add(bookIndex);
        }
        if(bookIndexList.size()>0){
            //0点前一日图书指数更新
            bookIndexMapper.insertList(bookIndexList);
            //0点前一日图书图书机构关系的统计数据更新更新
            for(BookIndex bookIndex : bookIndexList){
                bookOrgRelMapper.updateBookCountInfo(bookIndex.getOrg_id(), bookIndex.getBook_id(), bookIndex.getClick_count(), bookIndex.getCollect_count(),
                        bookIndex.getShare_count(), bookIndex.getReview_count(), bookIndex.getRead_count(), bookIndex.getUnite_index());
            }
        }
    }

    @Override
    public Object sitePageQuery(Sort sort, PageConditionBean bean, String type,Long org_id) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<Map<String,Object>> bookIndexList = new ArrayList<Map<String,Object>>();
        if("week".equals(type)){
            bookIndexList = bookIndexMapper.getListByWeek(org_id);
        }else if("month".equals(type)){
            bookIndexList = bookIndexMapper.getListByMonth(org_id);
        }else if("year".equals(type)){
            bookIndexList = bookIndexMapper.getListByYear(org_id);
        }
        PageList pageList = new PageList(bookIndexList, null);
        return pageList;
    }

    @Override
    public Object pageQuery(SearchBean searchBean) {
        PageHelper.startPage(searchBean.getPageNum(), searchBean.getPageSize());
        List<BookIndex> list = bookIndexMapper.pageQuery(searchBean);
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public List<BookIndex> selectCount1(Long bookid,Long org_id) {
        return bookIndexMapper.selectCount1(bookid,org_id);
    }

    @Override
    public void addCount1(BookIndex bookIndex) {
        bookIndexMapper.insert(bookIndex);
    }

    @Override
    public void updateCount(Long bookid,Long org_id) {
        bookIndexMapper.updateCount(bookid,org_id);
    }
}
