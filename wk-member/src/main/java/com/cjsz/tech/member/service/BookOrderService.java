package com.cjsz.tech.member.service;

import com.cjsz.tech.member.beans.OrderListBean;
import com.cjsz.tech.member.domain.BookOrder;

/**
 * Created by Administrator on 2017/10/10 0010.
 */
public interface BookOrderService {

    //出版图书购买记录
    void saveOrder(BookOrder bookOrder);

    //验证出版图书是否支付
    Boolean isBuyById(Long member_id, Long book_id);

    //后台订单查询
    Object getList(OrderListBean bean);
}
