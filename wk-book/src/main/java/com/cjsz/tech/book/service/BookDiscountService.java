package com.cjsz.tech.book.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.BookTypeBean;
import com.cjsz.tech.book.beans.PublicBookBean;
import com.cjsz.tech.book.domain.BookDiscount;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public interface BookDiscountService {

    //获取折扣书籍列表
    Object getList(BookTypeBean bean);

    //添加折扣书籍
    void addBook(Long book_id,Integer channel_type, Long order_weight);

    //修改折扣书籍信息
    BookDiscount update(BookDiscount bean);

    //删除折扣书籍
    void delete(String ids);

    //查询是否图书已存在
    BookDiscount selectById(Long book_id,Integer channel_type);

    //更新折扣书籍启用状态
    void updateEnabled(Long id, Integer enabled);

    //更新排序
    void updateOrder(Long id, Long order_weight);

    //app获取各频道限时免费分页列表
    Object getFreeList(Integer channel_type, PageConditionBean bean);

    //获取出版图书显示特价列表
    Object getDiscountList(PageConditionBean bean);

    //查询非出版图书是否是限免
    BookDiscount selectByIdAndType(Long book_id);

    //查询出版图书是否限免
    BookDiscount selectBookIsFree(Long bkid);

    //查询出版图书
    PublicBookBean selectBookPrice(Long book_id);
}
