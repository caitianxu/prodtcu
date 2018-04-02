package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.beans.BookMarkBean;
import com.cjsz.tech.book.domain.BookMark;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by LuoLi on 2017/4/10.
 */
public interface BookMarkMapper extends BaseMapper<BookMark> {

    @Select("select * from book_mark where member_id = #{0} and book_id = #{1}")
    List<BookMark> getListByMemberId(Long memberId, Long book_id);

    @Delete("delete from book_mark where mark_id in(${markIds})")
    void deleteBookMarkByIds(@Param("markIds") String markIds);

    @Select("select * from book_mark where chapter_id = #{0} and member_id = #{1} and book_id = #{2} limit 1")
    BookMark getBookMark(Long chapter_id,Long member_id,Long book_id);

    @Select("select bm.* , bc.name from (select * from book_mark where member_id = #{0} and book_id = #{1} and book_type = #{2} ) bm " +
            "left join book_chapter bc on bm.chapter_id = bc.id order by bm.create_time desc ")
    List<BookMarkBean> getMarkList(Long member_id,Long book_id,Integer book_type);

    @Select("select book_id from book_chapter where id = #{0} limit 1")
    Long getBookId(Long chapter_id);

}
