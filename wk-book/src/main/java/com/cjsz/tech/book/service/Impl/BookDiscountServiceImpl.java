package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.AppBooksBean;
import com.cjsz.tech.book.beans.BookTypeBean;
import com.cjsz.tech.book.beans.PublicBookBean;
import com.cjsz.tech.book.domain.BookDiscount;
import com.cjsz.tech.book.mapper.BookDiscountMapper;
import com.cjsz.tech.book.service.BookDiscountService;
import com.cjsz.tech.core.page.PageList;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
@Service
public class BookDiscountServiceImpl implements BookDiscountService {

    @Autowired
    private BookDiscountMapper bookDiscountMapper;


    @Override
    public Object getList(BookTypeBean bean) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        List<BookDiscount> result = new ArrayList<>();
        if (bean.getChannel_type() == 3) {
            result = bookDiscountMapper.getRepoList(bean.getSearchText());
        }else{
            result = bookDiscountMapper.getCJZWWList(bean.getSearchText(),bean.getChannel_type());
        }
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public void addBook(Long book_id, Integer channel_type,Long order_weight) {
        BookDiscount bookDiscount = new BookDiscount();
        bookDiscount.setBook_id(book_id);
        if (order_weight == null){
            order_weight = System.currentTimeMillis();
        }
        bookDiscount.setChannel_type(channel_type);
        bookDiscount.setOrder_weight(order_weight);
        bookDiscount.setCreate_time(new Date());
        bookDiscount.setEnabled(2);
        bookDiscount.setUpdate_time(new Date());
        bookDiscountMapper.insert(bookDiscount);
    }

    @Override
    public BookDiscount update(BookDiscount bean) {
        BookDiscount book = bookDiscountMapper.selectByPrimaryKey(bean);
        book.setStart_time(bean.getStart_time());
        if (bean.getOrder_weight() != null) {
            book.setOrder_weight(bean.getOrder_weight());
        }
        book.setDiscount_price(bean.getDiscount_price());
        book.setEnd_time(bean.getEnd_time());
        book.setUpdate_time(new Date());
        bookDiscountMapper.updateByPrimaryKey(book);
        return book;
    }

    @Override
    public void delete(String ids) {
        bookDiscountMapper.deleteByIds(ids);
    }

    @Override
    public BookDiscount selectById(Long book_id,Integer channel_type) {
        return bookDiscountMapper.selectById(book_id,channel_type);
    }

    @Override
    public void updateEnabled(Long id, Integer enabled) {
        bookDiscountMapper.updateEnabled(id,enabled);
    }

    @Override
    public void updateOrder(Long id, Long order_weight) {
        bookDiscountMapper.updateOrder(id,order_weight);
    }

    @Override
    public Object getFreeList(Integer channel_type, PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        List<AppBooksBean> result = new ArrayList<>();
        if (channel_type == 3) {
            result = bookDiscountMapper.getRepoFreeList();
        }else{
            result = bookDiscountMapper.getCJZWWFreeList(channel_type);
        }
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public Object getDiscountList(PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        List<AppBooksBean> result = bookDiscountMapper.getDiscountList();
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public BookDiscount selectByIdAndType(Long book_id) {
        return bookDiscountMapper.selectByIdAndType(book_id);
    }

    @Override
    public BookDiscount selectBookIsFree(Long bkid) {
        return bookDiscountMapper.selectBookIsFree(bkid);
    }

    @Override
    public PublicBookBean selectBookPrice(Long book_id) {
        return bookDiscountMapper.selectBookPirce(book_id);
    }
}
