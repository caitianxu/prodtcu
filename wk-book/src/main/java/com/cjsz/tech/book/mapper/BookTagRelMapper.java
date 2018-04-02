package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.domain.BookTagRel;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface BookTagRelMapper extends BaseMapper<BookTagRel>{

    //根据tagIds删除图书标签关系数据
    @Delete("delete from book_tag_rel where tag_id in(${tagIds})")
    public void deleteByTagIds(@Param("tagIds") String tagIds);

    //图书Id、标签Id查询关系数据
    @Select("select * from book_tag_rel where tag_id = #{0} and book_id = #{1}")
    public BookTagRel selectByTagIdAndBookId(Long tag_id, Long book_id);

    //根据tag_id，book_id删除图书标签关系数据
    @Delete("delete from book_tag_rel where book_id = #{0} and tag_id = #{1}")
    public void deleteBookTagRel(Long book_id, Long tag_id);

    @Delete("delete from book_tag_rel where book_id = #{0}")
    public void deleteByBookId(Long book_id);

    //根据tagIds查询图书Ids
    @Select("select distinct r.book_id from book_tag_rel r left join book_repo b on b.book_id = r.book_id where b.book_status = 1 and tag_id in (${tagIds})")
    public List<Long> getBookIdsByTagIds(@Param("tagIds") String tagIds);

    @Select("select btr.*,bt.tag_name from book_tag_rel btr left join book_tag bt on btr.tag_id = bt.tag_id where btr.tag_id = #{0} ")
    List<BookTagRel> selectById(Long tag_id);
}
