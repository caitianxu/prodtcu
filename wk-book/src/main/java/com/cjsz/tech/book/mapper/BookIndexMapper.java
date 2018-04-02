package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.domain.BookIndex;
import com.cjsz.tech.book.provider.BookIndexProvider;
import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.beans.SearchBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by LuoLi on 2017/4/15 0015.
 */
public interface BookIndexMapper extends BaseMapper<BookIndex> {

    @Select("select *, (click_count + collect_count*10 + share_count*20 + review_count*30 + read_count) unite_index from " +
            "(select org_id, book_id,book_type, " +
                "sum(if(record_type = '1',1,0)) click_count, " +
                "sum(if(record_type = '2',1,0)) collect_count, " +
                "sum(if(record_type = '3',1,0)) share_count, " +
                "sum(if(record_type = '4',1,0)) review_count, " +
                "sum(if(record_type = '5',1,0)) read_count " +
                "from book_index_record " +
                "where to_days(create_time) = (to_days(now()) - 1) " +
                "group by org_id,book_id " +
            ") a")
    List<Map<String,Object>> selectYesterdayData();

    @Select("select bi.book_id, sum(bi.unite_index) unite_index, br.book_name,br.book_publisher, br.book_author, br.book_remark, br.book_cover_small from " +
            "(select * from book_org_rel where org_id = #{0}) bor " +
            " left join book_index bi on bi.book_id = bor.book_id left join book_repo br on br.book_id = bi.book_id " +
            "where bi.org_id = #{0} and bi.book_type = 2 and  WEEK(bi.create_time)=WEEK(NOW()) and YEAR(bi.create_time)=YEAR(NOW()) " +
            "group by bi.book_id order by unite_index desc")
    List<Map<String,Object>> getListByWeek(Long org_id);

    @Select("select bi.book_id, sum(bi.unite_index) unite_index, br.book_name,br.book_publisher, br.book_author, br.book_remark, br.book_cover_small from " +
            "(select * from book_org_rel where org_id = #{0}) bor " +
            " left join book_index bi on bi.book_id = bor.book_id left join book_repo br on br.book_id = bi.book_id " +
            "where bi.org_id = #{0} and bi.book_type = 2 and  WEEK(bi.create_time)=WEEK(NOW()) and YEAR(bi.create_time)=YEAR(NOW()) " +
            "group by bi.book_id order by unite_index desc limit 10")
    List<Map<String,Object>> getIndexListByWeek(Long org_id);

    @Select("select bi.book_id, sum(bi.unite_index) unite_index, br.book_name, br.book_author,br.book_publisher, br.book_remark, br.book_cover_small from" +
            "(select * from book_org_rel where org_id = #{0}) bor left join book_index bi on bi.book_id = bor.book_id " +
            "left join book_repo br on br.book_id = bi.book_id " +
            "where bi.org_id = #{0} and bi.book_type = 2 and  MONTH(bi.create_time)=MONTH(NOW()) and YEAR(bi.create_time)=YEAR(NOW()) " +
            "group by bi.book_id order by unite_index desc")
    List<Map<String,Object>> getListByMonth(Long org_id);

    @Select("select bi.book_id, sum(bi.unite_index) unite_index, br.book_name, br.book_author,br.book_publisher, br.book_remark, br.book_cover_small from" +
            "(select * from book_org_rel where org_id = #{0}) bor left join book_index bi on bi.book_id = bor.book_id " +
            "left join book_repo br on br.book_id = bi.book_id " +
            "where bi.org_id = #{0} and bi.book_type = 2 and  MONTH(bi.create_time)=MONTH(NOW()) and YEAR(bi.create_time)=YEAR(NOW()) " +
            "group by bi.book_id order by unite_index desc limit 10")
    List<Map<String,Object>> getIndexListByMonth(Long org_id);

    @Select("select bi.book_id, sum(bi.unite_index) unite_index, br.book_name, br.book_author,br.book_publisher, br.book_remark, br.book_cover_small from " +
            "(select * from book_org_rel where org_id = #{0})bor left join book_index bi on bor.book_id =bi.book_id " +
            "left join book_repo br on br.book_id = bi.book_id " +
            "where bi.org_id = #{0} and bi.book_type = 2 and  YEAR(bi.create_time)=YEAR(NOW()) " +
            "group by bi.book_id order by unite_index desc")
    List<Map<String,Object>> getListByYear(Long org_id);

    @Select("select bi.book_id, sum(bi.unite_index) unite_index, br.book_name, br.book_author,br.book_publisher, br.book_remark, br.book_cover_small from " +
            "(select * from book_org_rel where org_id = #{0})bor left join book_index bi on bor.book_id =bi.book_id " +
            "left join book_repo br on br.book_id = bi.book_id " +
            "where bi.org_id = #{0} and bi.book_type = 2 and  YEAR(bi.create_time)=YEAR(NOW()) " +
            "group by bi.book_id order by unite_index desc limit 10 ")
    List<Map<String,Object>> getIndexListByYear(Long org_id);

    @SelectProvider(type = BookIndexProvider.class, method = "pageQuery")
    List<BookIndex> pageQuery(@Param("searchBean") SearchBean searchBean);

    @Select("select * from book_index where book_id=#{0} and org_id =#{1}")
    List<BookIndex> selectCount1(Long bookid,Long org_id);

    @Update("update book_index set click_count=(click_count+1) where book_id = #{0} and org_id=#{1}")
    void updateCount(Long bookid,Long org_id);
}
