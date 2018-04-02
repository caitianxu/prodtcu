package com.cjsz.tech.book.mapper;

import com.cjsz.tech.beans.ProxyBookChapter;
import com.cjsz.tech.book.domain.BookChapter;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface BookChapterMapper extends BaseMapper<BookChapter> {

    @Select("select * from book_chapter where id=#{0}")
    public BookChapter findById(Long id) ;

    @Select("select * from book_chapter")
    public List<BookChapter> findList() ;

    @Delete("delete  from book_chapter where id=#{0}")
    public void deleteById(Long id) ;

    @Delete("delete  from book_chapter where book_id=#{0}")
    public void deleteByBookId(Long bookId) ;

    @Select("select  a.* ,b2.start_page as page  from \n" +
            "(select * from book_chapter  where book_id=#{0} and format='.epub') a\n" +
            " , (select * from book_chapter where book_id=#{0} and format='.pdf')  b2\n" +
            "where  a.book_id=b2.book_id and a.name=b2.name\n")
    public List<BookChapter> findPdfChapterByBook_id(Long book_id) ;

    @Update("update book_chapter set purl = #{1} where book_id = #{0} and purl is null")
    void updateBookChapterPurl(Long book_id, String purl);

    @Select("select id from book_chapter where book_id = #{0}")
    List<Long> findChapterIdsByBKId(Long bkid);

    @Select("select count(id) from book_chapter where book_id = #{0}")
    Integer selectCountChapter(Long book_id);

    @Select("select id, name as title, start_page as inx, " +
            "pid, path ,concat(purl, url) as url from " +
            "book_chapter where book_id = #{0} GROUP BY url order by id asc")
    List<ProxyBookChapter> findEpubChapter(Long book_id);
}
