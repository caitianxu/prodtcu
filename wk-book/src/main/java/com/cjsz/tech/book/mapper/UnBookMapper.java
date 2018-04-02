package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.UnBook;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public interface UnBookMapper extends BaseMapper<UnBook> {

    //查询所有
    @Select("SELECT * FROM un_book where s_title like CONCAT('%','${searchText}','%')")
    List<UnBook> pageQuery(@Param("searchText") String searchText);
    //查询所有
    @Select("SELECT * FROM un_book")
    List<UnBook> pageQuery1();
    //通过文件名查询
    @Select("SELECT * from book_repo where file_name = #{0} LIMIT 1")
    BookRepo getBookByFileName(String file_name);
}
