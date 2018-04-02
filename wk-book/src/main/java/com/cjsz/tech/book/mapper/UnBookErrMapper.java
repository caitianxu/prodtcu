package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.UnBook;
import com.cjsz.tech.book.domain.UnBookErr;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public interface UnBookErrMapper extends BaseMapper<UnBookErr> {

    //查询所有
    @Select("DELETE FROM un_book_err where file_name = #{0} and task_id = #{1}")
    void deleteByBookUrl(String file_name, Long task_id);

}
