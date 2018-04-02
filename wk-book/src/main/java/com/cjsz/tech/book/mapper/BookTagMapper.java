package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.beans.TagBooksCount;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.BookTag;
import com.cjsz.tech.book.provider.BookTagProvider;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface BookTagMapper extends BaseMapper<BookTag>{

    //获取全部图书标签名
    @Select("select tag_name from book_tag where is_delete != 1 ")
    public List<String> getAllTagNames();

    //获取全部图书标签
    @Select("select * from book_tag bt where bt.is_delete != 1 order by length(tag_path)-length(replace(tag_path,'|','')) asc,tag_code asc ")
    public List<BookTag> getAllTags();

    //图书标签列表查询
    @SelectProvider(type = BookTagProvider.class, method = "getBookTagList")
    public List<BookTag> getBookTagList(@Param("searchText") String searchText);

    //通过标签Ids删除标签
    @Update("update  book_tag set is_delete = 1,update_time = now() where tag_id in(${tagIds})")
    public void deleteByTagIds(@Param("tagIds") String tagIds);

    //获取除tag_id外的全部图书标签名
    @Select("select tag_name from book_tag where tag_id !=#{0} and is_delete != 1")
    public List<String> getOtherTagNames(Long tag_id);

    @Select("select * from book_tag where tag_id = #{0} limit 1 ")
    BookTag selectById(Long tag_id);

    @Update("update book_tag set tag_path = replace(tag_path,#{0},#{1}), update_time = now() where tag_path like concat(#{0},'%') ")
    void updatePath(String old_path, String new_path);

    @Select("select * from book_tag where tag_pid = #{0} and is_delete = 2 limit 1 ")
    BookTag selectByPid(Long tag_id);

    @Select("select * from book_tag where tag_name = #{0} and is_delete = 2 limit 1")
    BookTag selectByName(String tag_name);

    @SelectProvider(type = BookTagProvider.class, method = "getNoTagList")
    List<BookRepo> getNoTagList(@Param("searchText") String searchText);

    //根据code查询标签
    @Select("select * from book_tag where tag_code in ( ${tag_code} ) ")
    List<BookTag> selectByCode(@Param("tag_code") String tag_code);

    @Select("select * from book_tag where tag_code  = #{0} and tag_id != #{1} ")
    List<BookTag> selectByCodeAndId(String tag_code, Long tag_id);

    @Select("select * from book_tag where tag_code = #{0}")
    List<BookTag> findByCode(String tag_code);

    @Select("select tag_id,count(*) as book_count from book_tag_rel GROUP BY tag_id ")
    List<TagBooksCount> selectBooksCount();

    @Select("select * from book_tag where tag_pid in (select tag_id from book_tag where tag_code = #{0})")
    List<BookTag> selectTagByCode(String s);


}
