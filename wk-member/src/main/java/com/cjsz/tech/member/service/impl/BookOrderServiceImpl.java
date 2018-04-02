package com.cjsz.tech.member.service.impl;

import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.mapper.BookRepoMapper;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.member.beans.OrderListBean;
import com.cjsz.tech.member.domain.BookOrder;
import com.cjsz.tech.member.mapper.BookOrderMapper;
import com.cjsz.tech.member.service.BookOrderService;
import com.cjsz.tech.utils.IDUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2017/10/10 0010.
 */
@Service
public class BookOrderServiceImpl implements BookOrderService{
    @Autowired
    private BookOrderMapper bookOrderMapper;
    @Autowired
    private BookRepoMapper bookRepoMapper;


    @Override
    public void saveOrder(BookOrder bookOrder) {
        BookRepo book = bookRepoMapper.findByBookId(bookOrder.getBook_id());
        if (book != null) {
            String remark = "购买" + book.getBook_name() + "共花费" + bookOrder.getPay_cost() + "长江币";
            bookOrder.setPay_remark(remark);
            bookOrder.setCreate_time(new Date());
            bookOrder.setIs_delete(2);
            bookOrderMapper.insert(bookOrder);
        }
    }

    @Override
    public Boolean isBuyById(Long member_id, Long book_id) {
        BookOrder order = bookOrderMapper.selectById(member_id,book_id);
        if (order == null){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public Object getList(OrderListBean bean) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        List<BookOrder> result = bookOrderMapper.getList(bean);
        PageList pageList = new  PageList(result,null);
        return pageList;
    }
}
