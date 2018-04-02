package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.beans.BookRecommend;
import com.cjsz.tech.book.beans.RecommendBooksBean;
import com.cjsz.tech.book.domain.RecommendBooks;
import com.cjsz.tech.book.provider.RecommendBooksProvider;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Administrator on 2017/9/5 0005.
 */
public interface RecommendBooksMapper extends BaseMapper<RecommendBooks> {

    @SelectProvider(type = RecommendBooksProvider.class,method = "getBookList")
    List<RecommendBooks> getBookList(@Param("recommend_cat_id") Long recommend_cat_id,@Param("searchText") String searchText);

    @Select("select * from recommend_books where book_id = #{0} and recommend_cat_id = #{1} limit 1")
    RecommendBooks selectByBookId(Long book_id, Long recommend_cat_id);

    @Select("select * from recommend_books where book_id = #{0} limit 1 ")
    RecommendBooks selectById(Long book_id);

    @Select("select * from recommend_books where recommend_cat_id = #{0}")
    List<RecommendBooks> selectByCatId(Long recommend_cat_id);

    @Update("update recommend_books set order_weight = #{1} where id = #{0}")
    void updateOrder(Long id, Long order_weight);

    @Select("select r.*,bc.book_cat_name, r.order_weight as aaa from recommend_books r  " +
            " left join book_org_rel bor on bor.book_id = r.book_id left join book_cat bc on bc.book_cat_id=bor.book_cat_id " +
            " where r.recommend_cat_id = #{0} and bor.org_id = 189 and r.book_type = 2 GROUP BY r.book_id " +
            "UNION " +
            "select r.*,b.book_cat_name,r.order_weight as aaa from recommend_books r LEFT JOIN cjzww_books b " +
            "on b.book_id = r.book_id where r.recommend_cat_id = #{0} and r.book_type = 1 order by aaa desc")
    List<RecommendBooksBean> getRecommendBooks(Long recommend_cat_id);

    @Select("select book_type, book_id, book_cover,book_cover as book_cover_small, book_name from recommend_books " +
            "where recommend_cat_id = (select recommend_cat_id from recommend_cat where recommend_code = #{0} limit 1) " +
            "order by order_weight desc limit  #{1}")
    List<BookRecommend> getRecommendBooksByCode(String code,Integer limit);
}
