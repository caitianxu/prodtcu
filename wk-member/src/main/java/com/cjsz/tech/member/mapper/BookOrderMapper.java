package com.cjsz.tech.member.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.member.beans.OrderListBean;
import com.cjsz.tech.member.domain.BookOrder;
import com.cjsz.tech.member.provider.BookOrderProvoder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by Administrator on 2017/10/10 0010.
 */
public interface BookOrderMapper extends BaseMapper<BookOrder>{

    @Select("select * from book_order where member_id = #{0} and book_id = #{1} limit 1 ")
    BookOrder selectById(Long member_id, Long book_id);

    @SelectProvider(type = BookOrderProvoder.class,method = "getList")
    List<BookOrder> getList(@Param("bean") OrderListBean bean);
}
