package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.domain.CJZWWBooks;
import com.cjsz.tech.book.provider.RecommendBooksProvider;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by Administrator on 2017/9/6 0006.
 */
public interface CJZWWBooksMapper extends BaseMapper<CJZWWBooks>{

    @Select("select * from cjzww_books ")
    List<CJZWWBooks> selectAllList();

    @SelectProvider(type = RecommendBooksProvider.class,method = "getAllBooks")
    List<CJZWWBooks> getAllBooks(@Param("searchText") String searchText);

    @Select("select * from cjzww_books where book_id = #{0} limit 1 ")
    CJZWWBooks selectById(Long book_id);
}
