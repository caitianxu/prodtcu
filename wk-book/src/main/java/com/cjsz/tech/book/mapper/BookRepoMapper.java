package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.beans.BookBaseInfo;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.UnBookErr;
import com.cjsz.tech.book.provider.BookRepoProvider;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface BookRepoMapper extends BaseMapper<BookRepo> {

    //图书仓库图书查询
    @SelectProvider(type = BookRepoProvider.class, method = "getBookRepoList")
    public List<BookRepo> getBookRepoList(@Param("searchText") String searchText, @Param("book_status") Integer book_status);

    //图书移除标签的查询
    @SelectProvider(type = BookRepoProvider.class, method = "getInBookRepoList")
    public List<BookRepo> getInBookRepoList(@Param("searchText") String searchText, @Param("tag_id") Long tag_id);

    //图书添加标签的查询
    @SelectProvider(type = BookRepoProvider.class, method = "getNotInBookRepoList")
    public List<BookRepo> getNotInBookRepoList(@Param("searchText") String searchText, @Param("tag_id") Long tag_id);

    //通过图书Id查找图书
    @Select("select * from book_repo where book_id = #{0}")
    public BookRepo findByBookId(Long bookId);

    //通过图书Ids查找图书
    @Select("select * from (" +
            " SELECT br.*,GROUP_CONCAT(bt.tag_code) tag_names FROM book_repo br " +
            " LEFT JOIN book_tag_rel btr on(br.book_id = btr.book_id) " +
            " LEFT JOIN book_tag bt on(bt.tag_id = btr.tag_id) GROUP BY br.book_id " +
            " ) " +
            "b where b.book_id in(${bookIds})")
    public List<BookRepo> findByBookIds(@Param("bookIds") String bookIds);

    //图书仓库图书上架下架（1:上架;2:下架）
    @Update("update book_repo set book_status = #{book_status},update_time = now() where book_id in (${bookIds_str})")
    public void updateBookStatusByBookIds(@Param("book_status") Integer book_status, @Param("bookIds_str") String bookIds_str);

    //查询isbn编号在[start_isbn，end_isbn]之间图书isbn
    @Select("select book_isbn from book_repo where book_isbn between #{0} and #{1} ")
    public List<String> selectRange(String start_isbn, String end_isbn);

    //获取全部图书仓库的图书(含标签)
    @Select("SELECT br.*,GROUP_CONCAT(bt.tag_name) tag_names FROM book_repo br " +
            "LEFT JOIN book_tag_rel btr on(br.book_id = btr.book_id) " +
            "LEFT JOIN book_tag bt on(bt.tag_id = btr.tag_id) GROUP BY br.book_id")
    public List<BookRepo> getAllList();

    //通过ISBN编号查找图书
    @Select("select * from book_repo where book_isbn = #{0} order by book_id")
    public List<BookRepo> findByISBN(String book_isbn);

    //书名作者查询书籍
    @Select("select * from book_repo where book_author = #{0} and book_name = #{1} order by book_id")
    public List<BookRepo> selectByTitleAndAuthor(String author, String title);

    //书名查询书籍
    @Select("select * from book_repo where book_name = #{0} order by book_id")
    public List<BookRepo> selectByTitle(String title);

    //根据标签Id查找图书
    @Select("SELECT br.*,GROUP_CONCAT(bt.tag_name) tag_names FROM book_repo br " +
            "LEFT JOIN book_tag_rel btr on(br.book_id = btr.book_id) " +
            "LEFT JOIN book_tag bt on(bt.tag_id = btr.tag_id) GROUP BY br.book_id")
    List<BookRepo> selectBookByTagId(Long tag_id);

    //通过isbn编号查询其他图书
    @Select("select * from book_repo where book_isbn = #{0} and book_id != #{1}")
    public List<BookRepo> selectOtherBooksByIsbn(String book_isbn, Long book_id);

    //通过上传的路径书名找书
    @Select("select * from book_repo where book_url like concat('%/',#{0})  ")
    public BookRepo findByBookUploadBookName(String book_name);

    //通过上传的路径书名找书
    @Select("select * from book_repo where book_url like concat('%/',#{0}) and parse_status = 3")
    public BookRepo findByBookUploadBookNameAndParseStatus(String book_name);

    //通过上传图书文件名找书
    @Select("select * from book_repo where file_name = #{0}  ")
    public BookRepo findByBookUploadFileName(String file_name);

    //到期图书下架
    @Update("update book_repo set book_status = 2,update_time = now() where end_time < now()")
    public void doBookEndTime();

    //通过文件名查找图书
    @Select("select * from book_repo where file_name = #{0}")
    public BookRepo findByFileName(String file_name);

    //通过文件名查找图书(不包括book_id这一本)
    @Select("select * from book_repo where file_name = #{0} and book_id != #{1}")
    BookRepo findByFileNameExceptSelf(String file_name, Long book_id);

    //图书基本信息
    @Select("select book_id, book_name, book_author, book_cover, book_cover_small, book_url from book_repo where book_id = #{0}")
    public BookBaseInfo findBaseInfoByBookId(Long bkid);

    //全部图书基本信息
    @Select("select book_id, book_name, book_author, book_publisher from book_repo")
    public List<Map<String, Object>> getAllBooksBaseInfo();

    //通过图书Id查找图书
    @Select("select * from book_repo where book_id=#{0}")
    public BookRepo findByBookId_v2(Long bookId);

    @Select("select book_author from book_repo where book_id = #{0}")
    String getAuthor(Long book_id);

    //相关推荐图书
    @Select("SELECT br.* FROM book_org_rel bor LEFT JOIN book_repo br ON br.book_id = bor.book_id " +
            "LEFT JOIN book_cat bc ON bc.book_cat_id = bor.book_cat_id " +
            "WHERE br.book_status =1 AND bor.enabled = 1 AND bor.org_id = #{0} AND bc.org_id = #{0} and br.book_id !=#{4}" +
            " and (br.book_author = #{2} or bor.book_cat_id= #{1} ) order by locate(br.book_author,#{2}) desc limit #{3} ")
    List<BookRepo> getSuggestBook(Long org_id, Long cat_id, String book_author, Integer limit, Long book_id);

    @Select("select schedule from book_shelf where bk_id = #{0} and user_id = #{1} and is_delete = 2 limit 1")
    String getSchedule(Long book_id, Long member_id);

    @Update("update book_repo set parse_status = 5  where file_name = #{0}")
    void updateBookStatusByBookName(String book_name);

    @Select("select count(*) from book_repo where book_name is null or book_name = '' ")
    Integer bookNames();

    @Select("select count(*) from book_repo where book_author is null or book_author = '' or book_author = 'unknown' or book_name = '未知'  ")
    Integer bookAuthors();

    @Select("select count(*) from book_repo where book_publisher is null or book_publisher = '' ")
    Integer publishers();

    @Select("select count(*) from book_repo where publish_time is null or publish_time = ''")
    Integer publishertimes();

    @Select("select count(*) from book_repo where end_time is null or end_time = '' ")
    Integer endTimes();

    @Select("select count(*) from book_repo where book_isbn is null or book_isbn = '' or book_isbn regexp  '[^0-9_]' ")
    Integer bookIsbns();

    @Select("select count(*) from book_repo where book_remark is null or book_remark = '' ")
    Integer remarks();

    @Select("select count(*) from ( SELECT br.*,GROUP_CONCAT(bt.tag_name) tag_names FROM book_repo br " +
            " LEFT JOIN book_tag_rel btr on(br.book_id = btr.book_id)  LEFT JOIN book_tag bt on(bt.tag_id = btr.tag_id) " +
            " GROUP BY br.book_id ) b where b.tag_names is null or b.tag_names = '' ")
    Integer tags();

    @Select("select count(*) from book_repo where book_cover is null or book_cover = '' ")
    Integer covers();

    @Select(" select count(*) from book_repo where price is null ")
    Integer price();

    @Update("update pkg_book_rel set order_weight = #{2} where book_id = #{0} and book_cat_id = #{1}")
    void updateOrderById(Long book_id,Long cat_id,Long order_weight);

    @Select("select b.* from un_book_err un left join ( " +
            " SELECT br.*,GROUP_CONCAT(bt.tag_name) tag_names FROM book_repo br " +
            " LEFT JOIN book_tag_rel btr on(br.book_id = btr.book_id) " +
            " LEFT JOIN book_tag bt on(bt.tag_id = btr.tag_id) GROUP BY br.book_id" +
            " ) b on un.book_id = b.book_id where un.task_id = #{0} and un.task_status = 1 and un.book_id is not null ")
    List<BookRepo> exportTaskBook(Long task_id);

    @Select("select * from un_book_err where task_id = #{0} and task_status = 0 ")
    List<UnBookErr> exportTaskErrBook(Long task_id);

    @Select("select br.*,bd.discount_price from book_repo br left join (select * from book_discount where start_time <= now() and end_time >= now() and enabled = 1) bd on bd.book_id = br.book_id where br.book_id = #{0} limit 1")
    BookBaseInfo findBookPrice(Long bkid);

    @Select("select book_id from book_repo where book_status = #{0}")
    List<Long> getBookIdsByStatus(Integer status);
}
