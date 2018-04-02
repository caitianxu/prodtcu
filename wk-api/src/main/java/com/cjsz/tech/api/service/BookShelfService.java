package com.cjsz.tech.api.service;

import com.cjsz.tech.api.beans.ShelfBook;
import com.cjsz.tech.api.mapper.BookShelfMapper;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.domain.BookIndexRecord;
import com.cjsz.tech.book.mapper.BookIndexRecordMapper;
import com.cjsz.tech.book.service.BookIndexRecordService;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.meb.service.MemberGradePointService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 书架
 * Created by shiaihua on 16/12/29.
 */
@Service
public class BookShelfService {

    @Autowired
    BookShelfMapper bookShelfMapper;

    @Autowired
    MemberGradePointService memberGradePointService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BookIndexRecordService bookIndexRecordService;

    public Object sitePageQuery(Sort sort, PageConditionBean bean, Long member_id) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<ShelfBook> result = findShelfBookList(member_id);
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    public List<ShelfBook> findShelfBookList(Long user_id) {
        return bookShelfMapper.findShelfBookList(user_id);
    }

    public void deleteShelfBooks(Long user_id,String bkids) {
        String delsql = "update book_shelf set is_delete = 1 where user_id="+user_id+" and bk_id in ("+bkids+")";
        jdbcTemplate.update(delsql);
    }

    @Transactional
    public void saveShelfBook(ShelfBook book, Long org_id, String device_type) {

        if(book!=null) {
            bookShelfMapper.insert(book);//书加入书架
            memberGradePointService.saveMemberGradePoint(book.getUser_id(), book.getBk_id(), "000001");//收藏(000001)图书，积分变化
            //收藏图书，图书指数记录
            //记录数据中（用户有机构，就填用户的机构，没有就填1）
            if(org_id == null || org_id == -1){
                org_id = 1L;
            }
            //record_type(1:点击详情;2:收藏;3:分享;4:评论;5:阅读)
            //000004:web
            BookIndexRecord bookIndexRecord = new BookIndexRecord(org_id, book.getBk_id(),2, book.getUser_id(), 2, new Date(), device_type);
            bookIndexRecordService.addRecord(bookIndexRecord);
        }
    }

    public ShelfBook findShelfBookByUser(Long user_id, Long bkid) {
        List<ShelfBook> list = bookShelfMapper.findShelfBookByUser(user_id,bkid);
        if(list!=null && list.size()>0) {
            return list.get(0);
        }
        return null;
    }

    public void updateBookShelf(ShelfBook book) {
        if(book!=null) {
            bookShelfMapper.updateByPrimaryKey(book);
        }
    }

    public List<ShelfBook> findShelfBookList_v2(Long user_id) {
        return bookShelfMapper.findShelfBookList_v2(user_id);
    }

    public ShelfBook findShelfBookByUser_v2(Long user_id, Long bkid) {
        List<ShelfBook> list = bookShelfMapper.findShelfBookByUser_v2(user_id,bkid);
        if (list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    public ShelfBook findShelfBookByUser_v3(Long user_id, Long bkid,Integer book_type) {
        return bookShelfMapper.findShelfBookByUser_v3(user_id,bkid,book_type);
    }

    public void updateBookShelf_v2(ShelfBook book) {
        if (book!=null){
            bookShelfMapper.updateByPrimaryKey(book);
        }
    }

    @Transactional
    public void saveShelfBook_v2(ShelfBook book) {
        if (book!=null){
            bookShelfMapper.insert(book);
        }
    }

    public void deleteShelfBooks_v2(Long user_id, String bkids) {
        String delsql = "update book_shelf set is_delete = 1 where user_id="+user_id+" and bk_id in ("+bkids+")";
        jdbcTemplate.update(delsql);
    }

    public List<ShelfBook> findShelfBookList_v3(Long member_id) {
        return bookShelfMapper.findShelfBookList_v3(member_id);
    }

    public void deleteShelfBooks_v3(String shelf_ids) {
        bookShelfMapper.deleteShelfBooks_v3(shelf_ids);
    }

    //查询会员是否添加图书到书架
    public ShelfBook findBookInShelf(Long member_id, Long book_id, Integer book_type) {
        return bookShelfMapper.findBookInShelf(member_id,book_id,book_type);
    }
}
