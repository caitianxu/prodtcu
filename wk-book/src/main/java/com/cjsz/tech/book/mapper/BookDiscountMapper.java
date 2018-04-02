package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.beans.AppBooksBean;
import com.cjsz.tech.book.beans.PublicBookBean;
import com.cjsz.tech.book.domain.BookDiscount;
import com.cjsz.tech.book.provider.BookDiscountProvider;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public interface BookDiscountMapper extends BaseMapper<BookDiscount>{

    //获取出版折扣书籍列表
    @SelectProvider(type = BookDiscountProvider.class,method = "getRepoList")
    List<BookDiscount> getRepoList(@Param("searchText") String searchText);

    //获取网文折扣书籍列表
    @SelectProvider(type = BookDiscountProvider.class,method = "getCJZWWList")
    List<BookDiscount> getCJZWWList(@Param("searchText") String searchText,@Param("channel_type") Integer channel_type);

    @Delete("delete from book_discount where id in (${ids})")
    void deleteByIds(@Param("ids") String ids);

    @Select("select * from book_discount where book_id = #{0} and channel_type = #{1} limit 1")
    BookDiscount selectById(Long book_id,Integer channel_type);

    @Update("update book_discount set enabled = #{1} ,update_time = now() where id = #{0}")
    void updateEnabled( Long id, Integer enabled);

    @Update("update book_discount set order_weight = #{1}, update_time = now() where id = #{0}")
    void updateOrder(Long id, Long order_weight);

    @Select("select bd.*,bp.book_name,bp.book_cover,bp.price ,bp.book_author,bp.book_cover_small,book_remark,bp.book_publisher,bc.book_cat_name ,2 as book_type " +
            " from book_discount bd left join book_repo bp on bd.book_id = bp.book_id left join (select book_id,book_cat_id from book_org_rel where org_id = 189) bor on bor.book_id = bp.book_id " +
            " left join (select book_cat_id,book_cat_name from book_cat where org_id = 189 ) bc on bor.book_cat_id = bc.book_cat_id " +
            " where channel_type = 3 and bd.discount_price = 0 and bd.start_time <= now() and bd.end_time >= now() and bd.enabled = 1 group by bd.book_id order by bd.order_weight desc ")
    List<AppBooksBean> getRepoFreeList();

    @Select("select bd.*,bp.book_name,bp.book_cover,bp.book_cover as book_cover_small, bp.book_author,book_remark,bp.book_cat_name,1 as book_type from book_discount bd left join cjzww_books bp on" +
            " bd.book_id = bp.book_id where bd.channel_type = #{0} and bd.discount_price = 0 and bd.start_time <= now() and bd.end_time >= now() and bd.enabled = 1 order by bd.order_weight desc")
    List<AppBooksBean> getCJZWWFreeList(Integer channel_type);

    @Select("select bd.*,bp.book_name,bp.book_cover,bp.price ,bp.book_author,bp.book_cover_small,book_remark,bp.book_publisher,bc.book_cat_name ,2 as book_type " +
            " from book_discount bd left join book_repo bp on bd.book_id = bp.book_id left join (select book_id,book_cat_id from book_org_rel where org_id = 189) bor on bor.book_id = bp.book_id " +
            " left join (select book_cat_id,book_cat_name from book_cat where org_id = 189 ) bc on bor.book_cat_id = bc.book_cat_id " +
            " where channel_type = 3 and bd.discount_price != 0 group by bd.book_id order by bd.order_weight desc")
    List<AppBooksBean> getDiscountList();

    @Select("select * from book_discount where book_id = #{0} and channel_type != 3 and start_time <= now() and end_time >= now() " +
            " and enabled =1 and discount_price =0 limit 1 ")
    BookDiscount selectByIdAndType(Long book_id);

    @Select("select * from book_discount where book_id = #{0} and channel_type = 3 and start_time <= now() and end_time >= now() " +
            " and enabled =1 and discount_price =0 limit 1")
    BookDiscount selectBookIsFree(Long bkid);

    @Select("select br.book_id, br.price,bd.discount_price from book_repo br left join (select * from book_discount where start_time <= now() and end_time >= now() and enabled = 1) bd on bd.book_id = br.book_id where  br.book_id = #{0} limit 1")
    PublicBookBean selectBookPirce(Long book_id);
}
