package com.cjsz.tech.book.service;

import com.cjsz.tech.beans.FileForm;
import com.cjsz.tech.book.beans.*;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.UnBookErr;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface BookRepoService {

    /**
     * 图书仓库图书查询
     * @param bean
     * @return
     */
    public Object pageQuery(Sort sort, FindBookRepoBean bean);

    /**
     * 图书移除标签的查询
     * @param bean
     * @return
     */
    public Object pageQueryIn(Sort sort, FindBookRepoBean bean);

    /**
     * 图书添加标签的查询
     * @param bean
     * @return
     */
    public Object pageQueryNotIn(Sort sort, FindBookRepoBean bean);

    /**
     * 修改图书仓库的图书信息
     * @param repo
     */
    public void updateBookRepo(BookRepo repo);

    /**
     * 保存图书仓库书图书信息
     * @param repo
     */
    public void saveBookRepo(BookRepo repo);

    /**
     * 通过图书Id查找图书
     * @param bookId
     * @return
     */
    public BookRepo findByBookId(Long bookId);

    /**
     * 通过图书Ids查找图书
     * @param bookIds
     * @return
     */
    public List<BookRepo> findByBookIds(String bookIds);

    /**
     * 图书仓库图书上架下架
     * @param bean
     * @param book_status
     */
    public void updateBookStatus(BookReposBean bean, Integer book_status);

    /**
     * 图书仓库图书信息根据上传Excel文件保存修改
     * @param content
     */
    public void batchImport(Map<Integer, List<String>> content);

    /**
     * 图书仓库图书信息根据上传Excel文件保存修改
     * @param content
     */
    public String batchImportExcel(Map<Integer, List<String>> content);

    /**
     * 获取全部图书仓库的图书
     * @return
     */
    public List<BookRepo> getAllList();

    /**
     * 图书上传
     * @param bookRepos
     */
    public void uploadBookRepo(List<BookRepo> bookRepos);

    /**
     * 导出图书
     * @param ids
     */
    public List<BookRepo> exportBook(Integer[] ids);

    /**
     * 根据标签查找图书
     * @param tag_id
     * @return
     */
    public List<BookRepo> selectBookByTagId(Long tag_id);

    /**
     * 通过isbn编号查询其他图书
     * @param book_isbn
     * @param book_id
     * @return
     */
    public List<BookRepo> selectOtherBooksByIsbn(String book_isbn, Long book_id);

    /**
     * 解析图书
     * @param bookRepo
     */
    public boolean autoParseForAll(BookRepo bookRepo);

    /**
     * 解析图书
     * @param bookRepo
     */
    public void autoParseForAllBook(BookRepo bookRepo);

    /**
     * 图书基本信息
     * @param bkid
     * @return
     */
    public BookBaseInfo findBaseInfoByBookId(Long bkid);

    /**
     * 全部图书基本信息
     * @return
     */
    public List<Map<String,Object>> getAllBooksBaseInfo();

    /**
     * 通过图书Id查找图书
     * @param bookID
     * @return
     */
    public BookRepo findByBookId_v2(Long bookID);

    /**
     * 查找作者，分类相关推荐图书
     * @param org_id
     * @param cat_id
     * @param book_id
     * @param limit
     * @return
     */
    List<BookRepo> getSuggestBook(Long org_id,Long cat_id,Long book_id,Integer limit);


    /**
     * 检测输入是否含有emoji表情字符
     * @param source
     * @return
     */
    Boolean containsEmoji(String source);

    /**
     * 获取会员阅读进度
     * @param book_id
     * @param member_id
     * @return
     */
    String getSchedule(Long book_id,Long member_id);

    void uploadBooks(List<FileForm> fileForms);

    void updateBookChapterPurl(Long book_id, String purl);

    //查询图书仓库导出excel数据过滤各个数量
    BookExportNums getNums();


    //数据包图书批量置顶排序
    void updateOrderById(Long book_id,Long cat_id,Long order_weight);

    //判断字符串是否为整数组成
    boolean isInteger(String str);


    //图书解析数据下载
    List<BookRepo> exportTaskBook(Long task_id);

    //图书解析失败数据下载
    List<UnBookErr> exportTaskErrBook(Long task_id);

    //查询出版图书价格
    BookBaseInfo findBookPrice(Long bkid);

    BookResult searchDirectoryIndex(String text, Integer pageSize, Integer pageNum, String book_id);

    boolean autoIndexForEpubByDBChapter(Long bookId);
}
