package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.beans.FileForm;
import com.cjsz.tech.beans.ProxyBookChapter;
import com.cjsz.tech.beans.ProxyEpubInfo;
import com.cjsz.tech.book.beans.*;
import com.cjsz.tech.book.domain.*;
import com.cjsz.tech.book.mapper.BookChapterMapper;
import com.cjsz.tech.book.mapper.BookRepoMapper;
import com.cjsz.tech.book.mapper.BookTagMapper;
import com.cjsz.tech.book.mapper.BookTagRelMapper;
import com.cjsz.tech.book.service.BookChapterService;
import com.cjsz.tech.book.service.BookRepoService;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.domain.SysLog;
import com.cjsz.tech.system.mapper.SysLogMapper;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.epub.EpubFormatEngine;
import com.cjsz.tech.utils.epub.EpubKernel;
import com.cjsz.tech.utils.epub.EpubUtil;
import com.cjsz.tech.utils.images.ImageHelper;
import com.github.pagehelper.PageHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.htmlparser.Parser;
import org.htmlparser.visitors.TextExtractingVisitor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
@Service
public class BookRepoServiceImpl implements BookRepoService {

    @Autowired
    private BookRepoMapper bookRepoMapper;

    @Autowired
    private BookTagMapper bookTagMapper;

    @Autowired
    private BookTagRelMapper bookTagRelMapper;

    @Autowired
    private BookChapterMapper bookChapterMapper;

    @Autowired
    private BookChapterService bookChapterService;

    @Autowired
    private SysLogMapper sysLogMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");

    private static Analyzer analyzer = null;
    private static Directory directory = null;
    private static IndexWriter indexWriter = null;

    private static String indexPath = "szyun/books/luceneindex";

    private static String unfile = "/unfile/";

    private String prefixHTML = "<font color='red'>";
    private String suffixHTML = "</font>";

    //图书仓库图书查询
    @Override
    public Object pageQuery(Sort sort, FindBookRepoBean bean) {
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        List<BookRepo> result = bookRepoMapper.getBookRepoList(bean.getSearchText(), bean.getBook_status());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    //图书仓库图书查询，以及图书移除标签的查询
    @Override
    public Object pageQueryIn(Sort sort, FindBookRepoBean bean) {
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        List<BookRepo> result = bookRepoMapper.getInBookRepoList(bean.getSearchText(), bean.getTag_id());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    //图书添加标签的查询
    @Override
    public Object pageQueryNotIn(Sort sort, FindBookRepoBean bean) {
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        List<BookRepo> result = bookRepoMapper.getNotInBookRepoList(bean.getSearchText(), bean.getTag_id());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    //修改图书仓库图书信息
    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书仓库")
    public void updateBookRepo(BookRepo repo) {
        repo.setUpdate_time(new Date());
        BookRepo bookRepo = bookRepoMapper.findByBookId(repo.getBook_id());

        String coverPath = repo.getBook_cover();
        if (StringUtils.isNotEmpty(coverPath) && (!coverPath.equals(bookRepo.getBook_cover()))) {
            try {
                String app_path = SpringContextUtil.getApplicationContext().getResource("").getFile().getPath();
                ImageHelper.getTransferImageByScale(app_path + coverPath, "small", 0.5);
                int i = coverPath.lastIndexOf(".");

                String book_cover_small = coverPath.substring(0, i) + "_small" + coverPath.substring(i);
                repo.setBook_cover_small(book_cover_small);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BeanUtils.copyProperties(repo, bookRepo);
        if (StringUtils.isNotEmpty(repo.getBook_remark()) && repo.getBook_remark().length() > 300) {
            repo.setBook_remark(repo.getBook_remark().substring(0, 300));
        }
        bookRepoMapper.updateByPrimaryKey(repo);
    }

    //保存图书仓库书图书信息
    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "图书仓库")
    public void saveBookRepo(BookRepo repo) {
        bookRepoMapper.insert(repo);
    }

    //通过图书Id查找图书
    @Override
    public BookRepo findByBookId(Long bookId) {
        return bookRepoMapper.findByBookId(bookId);
    }

    //通过图书Ids查找图书
    @Override
    public List<BookRepo> findByBookIds(String bookIds) {
        return bookRepoMapper.findByBookIds(bookIds);
    }

    //图书上架下架
    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书仓库")
    public void updateBookStatus(BookReposBean bean, Integer book_status) {
        //1:上架;2:下架
        String bookIds_str = StringUtils.join(bean.getIds(), ",");
        //更改图书仓库图书上架下架状态
        bookRepoMapper.updateBookStatusByBookIds(book_status, bookIds_str);
//        for (Long bookId : bean.getIds()) {
//            if (book_status == 2) {
//                //图书仓库图书下架删除索引
//                deleteChapterIndexForEpub(bookId);
//            } else {
//                //图书上架创建索引
//                autoIndexForEpubByDBChapter(bookId);
//            }
//        }

    }

    //图书仓库图书信息根据上传Excel文件保存修改
    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书仓库")
    public void batchImport(Map<Integer, List<String>> content) {
        //获取导入Excel的序号与isbn对应的对象集合
        List<BookRangeBean> bookRangeBeans = new ArrayList<BookRangeBean>();
        //获取导入Excel的标签集合
        Set<String> tag_names = new HashSet<String>();
        for (Map.Entry<Integer, List<String>> entry : content.entrySet()) {
            List<String> values = entry.getValue();
            String book_num = values.get(0);//序号:0
            if (StringUtils.isEmpty(book_num)) {
                continue;
            }
            String book_isbn = values.get(3);//ISBN:3
            String book_tags = values.get(6);//标签:6
            BookRangeBean bookRangeBean = new BookRangeBean(book_num, book_isbn);
            bookRangeBeans.add(bookRangeBean);
            if (StringUtils.isNotEmpty(book_tags)) {
                String[] book_tags1 = book_tags.split("/");
                for (String book_tag : book_tags1) {
                    tag_names.add(book_tag);
                }
            }
        }
        //获取全部图书标签名称
        List<String> tagNameList = bookTagMapper.getAllTagNames();
        //标签不重复添加
        for (String tagName : tag_names) {
            if (!tagNameList.contains(tagName)) {
                BookTag bookTag = new BookTag();
                bookTag.setTag_name(tagName);
                bookTag.setCreate_time(new Date());
                bookTag.setUpdate_time(new Date());
                bookTagMapper.insert(bookTag);
            }
        }
        //更新后的全部标签
        List<BookTag> tagList = bookTagMapper.getAllTags();

        //按isbn编号排序
        Collections.sort(bookRangeBeans, new Comparator<BookRangeBean>() {
            /*
             * int compare(BookRangeBean o1, BookRangeBean o2) 返回一个基本类型的整型，
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和o2相等，
             * 返回正数表示：o1大于o2。
             */
            @Override
            public int compare(BookRangeBean o1, BookRangeBean o2) {
                if (o1.getBook_isbn().compareTo(o2.getBook_isbn()) > 0) {
                    return 1;
                }
                if (o1.getBook_isbn().compareTo(o2.getBook_isbn()) == 0) {
                    //TODO
                    //导入的Excel图书中isbn编号相等做判断


                    return 0;
                }
                return -1;
            }
        });
        String start_isbn = bookRangeBeans.get(0).getBook_isbn();
        String end_isbn = bookRangeBeans.get(bookRangeBeans.size() - 1).getBook_isbn();
        //查询isbn编号在[start_isbn，end_isbn]之间图书isbn编号
        List<String> isbnList = bookRepoMapper.selectRange(start_isbn, end_isbn);

        List<BookRepo> bookRepoList = new ArrayList<BookRepo>();
        List<BookTagRel> bookTagRels = new ArrayList<BookTagRel>();
        for (Map.Entry<Integer, List<String>> entry : content.entrySet()) {
            BookRepo bookRepo = null;
            List<String> values = entry.getValue();
            String book_num = values.get(0);//序号:0
            if (StringUtils.isEmpty(book_num)) {
                continue;
            }
            String book_name = values.get(1);//书名:1
            String book_author = values.get(2);//作者:2
            String book_isbn = values.get(3);//ISBN:3
            String book_publisher = values.get(4);//出版社:4
            String book_tags = values.get(6);//标签:6
            String[] book_tags1 = book_tags.split("/");
            if (isbnList.contains(book_isbn)) {
                //isbn编号存在，修改信息
                //isbn编号查询书库图书
                List<BookRepo> bookRepos = bookRepoMapper.findByISBN(book_isbn);
                if (bookRepos.size() == 1) {
                    bookRepo = bookRepos.get(0);
                } else if (bookRepos.size() > 1) {
                    bookRepo = bookRepos.get(0);
                    //TODO
                    //搜索图书数量超过1，提示

                }
                bookRepo.setBook_name(book_name);
                bookRepo.setBook_author(book_author);
                bookRepo.setBook_isbn(book_isbn);
                bookRepo.setBook_publisher(book_publisher);
                bookRepoMapper.updateByPrimaryKey(bookRepo);

                //本书的tag_ids
                List<Long> tag_ids = new ArrayList<Long>();
                for (String book_tag : book_tags1) {
                    for (BookTag bookTag : tagList) {
                        if (bookTag.getTag_name().equals(book_tag)) {
                            tag_ids.add(bookTag.getTag_id());
                            break;
                        }
                    }
                }
                //删除图书标签关系
                bookTagRelMapper.deleteByBookId(bookRepo.getBook_id());
                //书籍标签关系
                for (Long tag_id : tag_ids) {
                    BookTagRel bookTagRel = new BookTagRel(bookRepo.getBook_id(), tag_id, new Date());
                    bookTagRels.add(bookTagRel);
                }
            } else {
                //TODO
                //isbn编号不存在，提示信息


            }
        }
        if (bookTagRels.size() > 0) {
            bookTagRelMapper.insertList(bookTagRels);
        }
    }

    //图书仓库图书信息根据上传Excel文件保存修改(使用中)
    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书仓库")
    public String batchImportExcel(Map<Integer, List<String>> content) {
        for (Map.Entry<Integer, List<String>> entry : content.entrySet()) {
            List<String> values = entry.getValue();
            String book_num = values.get(0);//序号:0
            if (StringUtils.isEmpty(book_num)) {
                continue;
            }
        }
        //图书标签关系（新增用）
        List<BookTagRel> bookTagRels = new ArrayList<BookTagRel>();
        //系统日志————记录导入Excel中的图书在数据库中找不到的数据
        List<SysLog> sysLogs = new ArrayList<SysLog>();
        List<String> messages = new ArrayList<String>();
        //图书信息修改————根据图书名称查询图书修改
        for (Map.Entry<Integer, List<String>> entry : content.entrySet()) {
            BookRepo bookRepo = null;
            List<String> values = entry.getValue();
            //序号0	文件名1	书名2	作者3	ISBN4	出版社5  出版日期6	标签7	版权日期8	简介9 价格12
            String book_num = values.get(0);//序号:0
            if (StringUtils.isEmpty(book_num)) {
                continue;
            }
            String file_name = values.get(1);//文件名:1
            String book_name = values.get(2);//书名:2
            String book_author = values.get(3);//作者:3
            String book_isbn = values.get(4);//ISBN:4
            String book_publisher = values.get(5);//出版社:5
            String publish_time = values.get(6);//出版日期:6
            String tag_code = values.get(7);//标签:7
            String end_time = values.get(8);//版权日期:8
            Date publish_time_date = null;
            Date end_time_date = null;
            if (StringUtils.isNotEmpty(publish_time)) {
                int n = publish_time.length() - publish_time.replaceAll("/", "").length();
                if (n > 0 && n < 3) {
                    publish_time = publish_time.replace("/", "-").replaceAll(" ", "").trim();
                    if (n == 1) {
                        publish_time = publish_time + "-01";
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        publish_time_date = sdf.parse(publish_time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        publish_time_date = HSSFDateUtil.getJavaDate(Double.valueOf(publish_time));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "出版日期时间格式错误(序号：" + book_num + ")";
                    }
                }
            }
            if (StringUtils.isNotEmpty(end_time)) {
                int n = end_time.length() - end_time.replaceAll("/", "").length();
                if (n > 0 && n < 3) {
                    end_time = end_time.replace("/", "-").replaceAll(" ", "").trim();
                    if (n == 1) {
                        end_time = end_time + "-01";
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        end_time_date = sdf.parse(end_time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        end_time_date = HSSFDateUtil.getJavaDate(Double.valueOf(end_time));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "版权日期时间格式错误(序号：" + book_num + ")";
                    }
                }
            }
            String book_remark = values.get(9);//简介:9
            if (book_remark.length() > 300) {
                book_remark = book_remark.substring(0, 300);
            }
            String price = values.get(12).toString();//价格：  12
            Double price_ = null;
            if (StringUtils.isNotEmpty(price)) {
                try {
                    price_ = Double.parseDouble(price);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //文件名查找图书：不为空修改图书
            bookRepo = bookRepoMapper.findByFileName(file_name);

            if (bookRepo != null) {
                //图书信息修改
                if (StringUtils.isNotEmpty(book_name)) {
                    bookRepo.setBook_name(book_name);
                }
                if (StringUtils.isNotEmpty(book_author)) {
                    bookRepo.setBook_author(book_author);
                }
                if (StringUtils.isNotEmpty(book_isbn)) {
                    bookRepo.setBook_isbn(book_isbn);
                }
                if (StringUtils.isNotEmpty(book_publisher)) {
                    bookRepo.setBook_publisher(book_publisher);
                }
                if (StringUtils.isNotEmpty(end_time)) {
                    bookRepo.setEnd_time(end_time_date);
                }
                if (StringUtils.isNotEmpty(publish_time)) {
                    bookRepo.setPublish_time(publish_time_date);
                }
                if (StringUtils.isNotEmpty(book_remark)) {
                    bookRepo.setBook_remark(book_remark);
                }
                bookRepo.setPrice(price_);
                bookRepo.setUpdate_time(new Date());
                bookRepoMapper.updateByPrimaryKey(bookRepo);
                System.out.println("******序号：" + book_num + "*******文件名：" + file_name);


                if (StringUtils.isNotEmpty(tag_code)) {
                    String[] code = tag_code.split("/");
                    for (int i = 0; i < code.length; i++) {
                        List<BookTag> tags = bookTagMapper.selectTagByCode(code[i]);
                        if (tags != null && tags.size() > 0) {
                            if (i != code.length - 1) {
                                tag_code = tag_code.replace(code[i] + "/", "");
                            } else {
                                tag_code = tag_code.replace(code[i], "");
                            }
                        }

                    }
                    tag_code = "'" + tag_code + "'";
                    tag_code = tag_code.replace("/", "','");
                    List<BookTag> listByCode = bookTagMapper.selectByCode(tag_code);
                    bookTagRelMapper.deleteByBookId(bookRepo.getBook_id());
                    for (BookTag tag : listByCode) {
                        BookTagRel bookTagRel = new BookTagRel(bookRepo.getBook_id(), tag.getTag_id(), new Date());
                        bookTagRels.add(bookTagRel);
                    }
                }
            } else {
                //Excel导入的图书信息，在书库找不到，则提示这本书的信息修改失败
                //TODO
                //book_num记录
                messages.add(book_num);
                System.out.println("**********************************************");
                System.out.println("file_name:" + file_name);
                System.out.println("----------------------------------------------");
                SysLog sysLog = new SysLog();
                sysLog.setSys_log_content("{序号：" + book_num + "，文件名：" + file_name + "}");
                sysLog.setOrg_id(1L);
                sysLog.setSys_log_code(1L);
                sysLog.setCreate_time(new Date());
                sysLogs.add(sysLog);
            }
        }
        if (bookTagRels.size() > 0) {
            List<BookTagRel> bookTagRelList = new ArrayList<>();
            Iterator it = bookTagRels.iterator();
            while (it.hasNext()) {
                BookTagRel tagRel = (BookTagRel) it.next();
                if (!bookTagRelList.contains(tagRel)) {
                    bookTagRelList.add(tagRel);
                }
            }
            bookTagRelMapper.insertList(bookTagRelList);
        }
        if (sysLogs.size() > 0) {
            sysLogMapper.insertList(sysLogs);
        }
        if (messages.size() > 0) {
            return "未找到序号为：" + StringUtils.join(messages, ",") + "的图书";
        } else {
            return null;
        }
    }

    //图书仓库图书信息根据上传Excel文件保存修改
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书仓库")
    public void batchImportExcel1(Map<Integer, List<String>> content) {
        //获取导入Excel的标签集合
        Set<String> tag_names = new HashSet<String>();
        for (Map.Entry<Integer, List<String>> entry : content.entrySet()) {
            List<String> values = entry.getValue();
            String book_num = values.get(0);//序号:0
            if (StringUtils.isEmpty(book_num)) {
                continue;
            }
            String book_tags = values.get(6);//标签:6
            if (StringUtils.isNotEmpty(book_tags)) {
                String[] book_tags_arr = book_tags.split("/");
                for (String book_tag : book_tags_arr) {
                    tag_names.add(book_tag);
                }
            }
        }
        //获取全部图书标签名称
        List<String> tagNameList = bookTagMapper.getAllTagNames();
        //标签不重复添加
        List<BookTag> add_bookTags = new ArrayList<BookTag>();
        for (String tagName : tag_names) {
            if (!tagNameList.contains(tagName)) {
                BookTag bookTag = new BookTag();
                bookTag.setTag_name(tagName);
                bookTag.setCreate_time(new Date());
                bookTag.setUpdate_time(new Date());
                add_bookTags.add(bookTag);
            }
        }
        if (add_bookTags.size() > 0) {
            bookTagMapper.insertList(add_bookTags);
        }
        //更新后的全部标签
        List<BookTag> tagList = bookTagMapper.getAllTags();
        //图书标签关系（新增用）
        List<BookTagRel> bookTagRels = new ArrayList<BookTagRel>();
        //图书信息修改
        for (Map.Entry<Integer, List<String>> entry : content.entrySet()) {
            BookRepo bookRepo = null;
            List<String> values = entry.getValue();
            String book_num = values.get(0);//序号:0
            if (StringUtils.isEmpty(book_num)) {
                continue;
            }
            String book_name = values.get(1);//书名:1
            String book_author = values.get(2);//作者:2
            String book_isbn = values.get(3);//ISBN:3
            String book_publisher = values.get(4);//出版社:4
            String book_tags = values.get(6);//标签:6

            //isbn编号查询书库图书
            List<BookRepo> bookRepos = bookRepoMapper.findByISBN(book_isbn);
            if (bookRepos.size() == 1) {
                bookRepo = bookRepos.get(0);
            } else if (bookRepos.size() > 1) {
                bookRepo = bookRepos.get(0);
                //TODO
                //搜索图书数量超过1，提示

            }
            if (bookRepo == null) {
                //isbn编号查询书库图书为空，则通过作者、书名查询
                bookRepos = bookRepoMapper.selectByTitleAndAuthor(book_author, book_name);
                if (bookRepos.size() == 1) {
                    bookRepo = bookRepos.get(0);
                } else if (bookRepos.size() > 1) {
                    bookRepo = bookRepos.get(0);
                    //TODO
                    //搜索图书数量超过1，提示

                }
            }
            if (bookRepo == null) {
                //isbn编号查询书库图书为空，通过作者、书名查询为空，则通过书名查询
                bookRepos = bookRepoMapper.selectByTitle(book_name);
                if (bookRepos.size() == 1) {
                    bookRepo = bookRepos.get(0);
                } else if (bookRepos.size() > 1) {
                    bookRepo = bookRepos.get(0);
                    //TODO
                    //搜索图书数量超过1，提示

                }
            }
            if (bookRepo != null) {
                //图书信息修改
                bookRepo.setBook_name(book_name);
                bookRepo.setBook_author(book_author);
                bookRepo.setBook_isbn(book_isbn);
                bookRepo.setBook_publisher(book_publisher);
                bookRepo.setUpdate_time(new Date());
                bookRepoMapper.updateByPrimaryKey(bookRepo);

                //查找本书标签对应的的tag_ids
                List<Long> tag_ids = new ArrayList<Long>();
                if (StringUtils.isNotEmpty(book_tags)) {
                    String[] book_tags_arr = book_tags.split("/");
                    for (String book_tag : book_tags_arr) {
                        for (BookTag bookTag : tagList) {
                            if (bookTag.getTag_name().equals(book_tag)) {
                                tag_ids.add(bookTag.getTag_id());
                                break;
                            }
                        }
                    }
                }
                //删除图书标签关系
                bookTagRelMapper.deleteByBookId(bookRepo.getBook_id());
                //书籍标签关系
                for (Long tag_id : tag_ids) {
                    BookTagRel bookTagRel = new BookTagRel(bookRepo.getBook_id(), tag_id, new Date());
                    bookTagRels.add(bookTagRel);
                }
            } else {
                //Excel导入的图书信息，在书库找不到，则提示这本书的信息修改失败
                //TODO
                //book_num记录
            }
        }
        if (bookTagRels.size() > 0) {
            bookTagRelMapper.insertList(bookTagRels);
        }
    }

    //获取全部图书
    @Override
    public List<BookRepo> getAllList() {
        return bookRepoMapper.getAllList();
    }

    //图书上传
    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书仓库")
    public void uploadBookRepo(List<BookRepo> bookRepos) {
        for (BookRepo bookRepo : bookRepos) {
            bookRepo.setBook_status(1);//1:上架;2:下架
            bookRepo.setParse_status(1);//1:未解析;2:解析中;3:已解析
            bookRepo.setCreate_time(new Date());
            bookRepo.setUpdate_time(new Date());
            bookRepoMapper.insert(bookRepo);
            autoParseForAll(bookRepo);
        }
    }

    //导出图书
    @Override
    public List<BookRepo> exportBook(Integer[] ids) {
        StringBuffer qsql = new StringBuffer();
        qsql.append("select * from ( " +
                " SELECT br.*,GROUP_CONCAT(bt.tag_code) tag_names FROM book_repo br " +
                " LEFT JOIN book_tag_rel btr on(br.book_id = btr.book_id) " +
                " LEFT JOIN book_tag bt on(bt.tag_id = btr.tag_id) GROUP BY br.book_id" +
                " ) b where 1=1 ");
        List<Integer> idList = Arrays.asList(ids);
        if (idList.size() > 0) {
            qsql.append(" and ( ");
            int ccount = 0;
            if (idList.contains(1)) {
                qsql.append(" (b.book_name is null or b.book_name = '')");
                ccount += 1;
            }
            if (idList.contains(2)) {
                if (ccount > 0) {
                    qsql.append(" or (b.book_author is null or b.book_author = '' or b.book_author = 'unknown' or b.book_name = '未知' )");
                } else {
                    qsql.append(" (b.book_author is null or b.book_author = '' or b.book_author = 'unknown' or b.book_name = '未知' )");
                }
                ccount += 1;
            }
            if (idList.contains(3)) {
                if (ccount > 0) {
                    qsql.append(" or (b.book_publisher is null or b.book_publisher = '')");
                } else {
                    qsql.append(" (b.book_publisher is null or b.book_publisher = '')");
                }
                ccount += 1;
            }
            if (idList.contains(4)) {
                if (ccount > 0) {
                    qsql.append(" or (b.publish_time is null or b.publish_time = '')");
                } else {
                    qsql.append(" (b.publish_time is null or b.publish_time = '')");
                }
                ccount += 1;
            }
            if (idList.contains(5)) {
                if (ccount > 0) {
                    qsql.append(" or (b.end_time is null or b.end_time = '')");
                } else {
                    qsql.append(" (b.end_time is null or b.end_time = '')");
                }
                ccount += 1;
            }
            if (idList.contains(6)) {
                if (ccount > 0) {
                    qsql.append(" or (b.book_isbn is null or b.book_isbn = '' or b.book_isbn regexp  '[^0-9_]' )");
                } else {
                    qsql.append(" (b.book_isbn is null or b.book_isbn = ''  or b.book_isbn regexp  '[^0-9_]' )");
                }
                ccount += 1;
            }
            if (idList.contains(7)) {
                if (ccount > 0) {
                    qsql.append(" or (b.book_remark is null or b.book_remark = '')");
                } else {
                    qsql.append(" (b.book_remark is null or b.book_remark = '')");
                }
                ccount += 1;
            }
            if (idList.contains(8)) {
                if (ccount > 0) {
                    qsql.append(" or (b.tag_names is null or b.tag_names = '')");
                } else {
                    qsql.append(" (b.tag_names is null or b.tag_names = '')");
                }
                ccount += 1;
            }
            if (idList.contains(9)) {
                if (ccount > 0) {
                    qsql.append(" or (b.book_cover is null or b.book_cover = '')");
                } else {
                    qsql.append(" (b.book_cover is null or b.book_cover = '')");
                }
                ccount += 1;
            }
            if (idList.contains(10)) {
                if (ccount > 0) {
                    qsql.append(" or (b.price is null  )");
                } else {
                    qsql.append(" ( b.price is null  )");
                }
                ccount += 1;
            }
            qsql.append(" )");
        }
        List<BookRepo> bookRepos = jdbcTemplate.query(qsql.toString(), BeanPropertyRowMapper.newInstance(BookRepo.class));
        return bookRepos;
    }

    //根据标签查询图书
    @Override
    public List<BookRepo> selectBookByTagId(Long tag_id) {
        return bookRepoMapper.getInBookRepoList(null, tag_id);
    }

    //通过isbn编号查询其他图书
    @Override
    public List<BookRepo> selectOtherBooksByIsbn(String book_isbn, Long book_id) {
        return bookRepoMapper.selectOtherBooksByIsbn(book_isbn, book_id);
    }

    //解析图书：通过文件名判断是否重复
    @Override
    public synchronized boolean autoParseForAll(BookRepo bookRepo) {
        try {
            File file = SpringContextUtil.getApplicationContext().getResource("").getFile();
            String filepath = file.getPath() + bookRepo.getBook_url();
            File newfile = new File(filepath);
            if (!newfile.exists()) {
                return false;
            }
            String saveroot = file.getPath() + "/attachment/outdistr";
            EpubFormatEngine engine = new EpubFormatEngine();
            engine.setBookfile(filepath);
            engine.setSaveroot(saveroot);
            engine.setLimitDirectoryLevel(2);
            engine.addExcludeContentKey("封面");
            engine.addExcludeContentKey("目录");
            engine.addExcludeContentKey("出版说明");
//            bookRepo = bookRepoMapper.selectByTitle(bookRepo.getBook_name());
            //只调用解析，不做排版
            Boolean falg = engine.parseEpubBook();
            if (!falg) {
                return false;
            }
            //获取书籍信息
            ProxyEpubInfo proxyEpubInfo = engine.getOriginEpubInfo();
            if (null == proxyEpubInfo) {
                return false;
            }
            List<EpubKernel.EpubCatalog> catlist = proxyEpubInfo.getList();
            if (null == proxyEpubInfo.getChapterroot() || null == catlist || catlist.isEmpty()) {
                return false;
            }

            String chapter_file = proxyEpubInfo.getChapterroot() + "/";
            BookRepo repo = bookRepoMapper.findByFileName(bookRepo.getFile_name());
            if (repo != null) {
                //查询书籍不为空，则修改数据
                bookChapterService.deleteChapterByBookAndType(repo.getBook_id(), ".epub");
                this.outChapterForEpub(catlist, repo.getBook_id(), 0L, "", "0|", chapter_file);
                repo.setBook_url(bookRepo.getBook_url());
                //更新图书信息
                if (StringUtils.isNotEmpty(proxyEpubInfo.getAuthor())) {
                    repo.setBook_author(proxyEpubInfo.getAuthor());
                }
                if (StringUtils.isNotEmpty(proxyEpubInfo.getTitle())) {
                    repo.setBook_name(proxyEpubInfo.getTitle());
                }
                if (StringUtils.isNotEmpty(proxyEpubInfo.getDescription())) {
                    repo.setBook_remark(proxyEpubInfo.getDescription());
                }
                if (StringUtils.isNotEmpty(proxyEpubInfo.getPublisher())) {
                    repo.setBook_publisher(proxyEpubInfo.getPublisher());
                }
                if (StringUtils.isNotEmpty(proxyEpubInfo.getIdentifier())) {
                    repo.setBook_isbn(proxyEpubInfo.getIdentifier());
                } else {
                    //isbn编号为空时，以文件名为isbn编号
                    String file_name = bookRepo.getFile_name();
                    file_name = file_name.replace(".epub", "").replace(".EPUB", "").trim();//图书文件名
                    repo.setBook_isbn(file_name);
                }
                //更新封面路径
                String coverPath = proxyEpubInfo.getCover();
                if (StringUtils.isNotEmpty(coverPath)) {
                    Map<String, Object> saveWebPath = copyCoverToCoverDir(repo, coverPath);
                    if (null != saveWebPath) {
                        if (saveWebPath.containsKey("book_cover")) {
                            repo.setBook_cover(saveWebPath.get("book_cover").toString());
                        }
                        if (saveWebPath.containsKey("book_cover_small")) {
                            repo.setBook_cover_small(saveWebPath.get("book_cover_small").toString());
                        }
                    }
                }

                repo.setParse_status(3);//1:未解析;2:解析中;3:已解析
                //更新查询到的图书
                repo.setUpdate_time(new Date());
                bookRepoMapper.updateByPrimaryKey(repo);
            } else {
                //新增图书信息
                if (StringUtils.isNotEmpty(proxyEpubInfo.getAuthor())) {
                    bookRepo.setBook_author(proxyEpubInfo.getAuthor());
                }
                if (StringUtils.isNotEmpty(proxyEpubInfo.getTitle())) {
                    bookRepo.setBook_name(proxyEpubInfo.getTitle());
                }
                if (StringUtils.isNotEmpty(proxyEpubInfo.getDescription())) {
                    bookRepo.setBook_remark(proxyEpubInfo.getDescription());
                }
                if (StringUtils.isNotEmpty(proxyEpubInfo.getPublisher())) {
                    bookRepo.setBook_publisher(proxyEpubInfo.getPublisher());
                }
                if (StringUtils.isNotEmpty(proxyEpubInfo.getIdentifier())) {
                    bookRepo.setBook_isbn(proxyEpubInfo.getIdentifier());
                } else {
                    //isbn编号为空时，以文件名为isbn编号
                    String file_name = bookRepo.getFile_name();
                    file_name = file_name.replace(".epub", "").replace(".EPUB", "").trim();//图书文件名
                    bookRepo.setBook_isbn(file_name);
                }
                //更新封面路径
                String coverPath = proxyEpubInfo.getCover();
                if (StringUtils.isNotEmpty(coverPath)) {
                    Map<String, Object> saveWebPath = copyCoverToCoverDir(bookRepo, coverPath);
                    if (saveWebPath.containsKey("book_cover")) {
                        bookRepo.setBook_cover(saveWebPath.get("book_cover").toString());
                    }
                    if (saveWebPath.containsKey("book_cover_small")) {
                        bookRepo.setBook_cover_small(saveWebPath.get("book_cover_small").toString());
                    }
                }
                bookRepo.setParse_status(3);//1:未解析;2:解析中;3:已解析
                bookRepo.setBook_status(1);//1:上架;2:下架
                bookRepo.setCreate_time(new Date());
                bookRepo.setUpdate_time(new Date());
                bookRepoMapper.insert(bookRepo);
                this.outChapterForEpub(catlist, bookRepo.getBook_id(), 0L, "", "0|", chapter_file);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    //解析图书(文件名不重复)
    @Override
    public synchronized void autoParseForAllBook(BookRepo bookRepo) {
        try {
            File file = null;
            file = SpringContextUtil.getApplicationContext().getResource("").getFile();
            String filepath = file.getPath() + bookRepo.getBook_url();
            String saveroot = file.getPath() + "/attachment/outdistr";
//            try{
//                File saverootDir = new File(saveroot);
//                if(saverootDir.exists()) {
//                    File[] listFiles = saverootDir.listFiles();
//                    for(File curFile:listFiles) {
//                        if(curFile.isDirectory()) {
//                            FileUtil.del(curFile);
//                        }
//                    }
//                }
//            }catch (Exception e) {
//                e.printStackTrace();
//            }

            EpubFormatEngine engine = new EpubFormatEngine();
            engine.setBookfile(filepath);
            engine.setSaveroot(saveroot);
            engine.setLimitDirectoryLevel(2);
            engine.addExcludeContentKey("封面");
            engine.addExcludeContentKey("目录");
            engine.addExcludeContentKey("出版说明");
            //只调用解析，不做排版
            engine.parseEpubBook();
            //获取书籍信息
            ProxyEpubInfo proxyEpubInfo = engine.getOriginEpubInfo();
            if (null != proxyEpubInfo) {
                List<EpubKernel.EpubCatalog> catlist = proxyEpubInfo.getList();
                String chapter_file = proxyEpubInfo.getChapterroot() + "/";
                if (catlist.size() > 0) {
                    BookRepo repo = bookRepoMapper.findByFileName(bookRepo.getBook_name());
                    if (repo != null) {
                        //查询书籍不为空，则修改数据
                        bookChapterService.deleteChapterByBookAndType(repo.getBook_id(), ".epub");
                        this.outChapterForEpub(catlist, repo.getBook_id(), 0L, "", "0|", chapter_file);
                        repo.setBook_url(bookRepo.getBook_url());
                        //更新图书信息
                        if (StringUtils.isNotEmpty(proxyEpubInfo.getAuthor())) {
                            repo.setBook_author(proxyEpubInfo.getAuthor());
                        }
                        if (StringUtils.isNotEmpty(proxyEpubInfo.getTitle())) {
                            repo.setBook_name(proxyEpubInfo.getTitle());
                        }
                        if (StringUtils.isNotEmpty(proxyEpubInfo.getDescription())) {
                            repo.setBook_remark(proxyEpubInfo.getDescription());
                        }
                        if (StringUtils.isNotEmpty(proxyEpubInfo.getPublisher())) {
                            repo.setBook_publisher(proxyEpubInfo.getPublisher());
                        }
                        if (StringUtils.isNotEmpty(proxyEpubInfo.getIdentifier())) {
                            repo.setBook_isbn(proxyEpubInfo.getIdentifier());
                        } else {
                            //isbn编号为空时，以文件名为isbn编号
                            String file_name = bookRepo.getFile_name();
                            file_name = file_name.replace(".epub", "").replace(".EPUB", "").trim();//图书文件名
                            repo.setBook_isbn(file_name);
                        }
                        //更新封面路径
                        String coverPath = proxyEpubInfo.getCover();
                        if (StringUtils.isNotEmpty(coverPath)) {
                            Map<String, Object> saveWebPath = copyCoverToCoverDir(repo, coverPath);
                            if (null != saveWebPath) {
                                if (saveWebPath.containsKey("book_cover")) {
                                    repo.setBook_cover(saveWebPath.get("book_cover").toString());
                                }
                                if (saveWebPath.containsKey("book_cover_small")) {
                                    repo.setBook_cover_small(saveWebPath.get("book_cover_small").toString());
                                }
                            }
                        }

                        repo.setParse_status(3);//1:未解析;2:解析中;3:已解析
                        //更新查询到的图书
                        repo.setUpdate_time(new Date());
                        bookRepoMapper.updateByPrimaryKey(repo);
                    } else {
                        //新增图书信息
                        if (StringUtils.isNotEmpty(proxyEpubInfo.getAuthor())) {
                            bookRepo.setBook_author(proxyEpubInfo.getAuthor());
                        }
                        if (StringUtils.isNotEmpty(proxyEpubInfo.getTitle())) {
                            bookRepo.setBook_name(proxyEpubInfo.getTitle());
                        }
                        if (StringUtils.isNotEmpty(proxyEpubInfo.getDescription())) {
                            bookRepo.setBook_remark(proxyEpubInfo.getDescription());
                        }
                        if (StringUtils.isNotEmpty(proxyEpubInfo.getPublisher())) {
                            bookRepo.setBook_publisher(proxyEpubInfo.getPublisher());
                        }
                        if (StringUtils.isNotEmpty(proxyEpubInfo.getIdentifier())) {
                            bookRepo.setBook_isbn(proxyEpubInfo.getIdentifier());
                        } else {
                            //isbn编号为空时，以文件名为isbn编号
                            String file_name = bookRepo.getFile_name();
                            file_name = file_name.replace(".epub", "").replace(".EPUB", "").trim();//图书文件名
                            bookRepo.setBook_isbn(file_name);
                        }
                        //更新封面路径
                        String coverPath = proxyEpubInfo.getCover();
                        if (StringUtils.isNotEmpty(coverPath)) {
                            Map<String, Object> saveWebPath = copyCoverToCoverDir(bookRepo, coverPath);
                            if (saveWebPath.containsKey("book_cover")) {
                                bookRepo.setBook_cover(saveWebPath.get("book_cover").toString());
                            }
                            if (saveWebPath.containsKey("book_cover_small")) {
                                bookRepo.setBook_cover_small(saveWebPath.get("book_cover_small").toString());
                            }
                        }
                        bookRepo.setParse_status(3);//1:未解析;2:解析中;3:已解析
                        bookRepo.setBook_status(1);//1:上架;2:下架
                        bookRepo.setCreate_time(new Date());
                        bookRepo.setUpdate_time(new Date());
                        bookRepoMapper.insert(bookRepo);
                        this.outChapterForEpub(catlist, bookRepo.getBook_id(), 0L, "", "0|", chapter_file);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BookBaseInfo findBaseInfoByBookId(Long bkid) {
        return bookRepoMapper.findBaseInfoByBookId(bkid);
    }

    @Override
    public List<Map<String, Object>> getAllBooksBaseInfo() {
        return bookRepoMapper.getAllBooksBaseInfo();
    }

    @Override
    public BookRepo findByBookId_v2(Long bookId) {
        return bookRepoMapper.findByBookId_v2(bookId);
    }


    private Map<String, Object> copyCoverToCoverDir(BookRepo repo, String coverPath) {
        try {
            File srcFile = new File(coverPath);
            if (!srcFile.exists()) {
                return null;
            }
            String app_path = SpringContextUtil.getApplicationContext().getResource("").getFile() + File.separator;
            String saveroot = app_path + EpubUtil.DEFAULT_BASE + File.separator + "cover";
            File saverootFile = new File(saveroot);
            if (!saverootFile.exists()) {
                saverootFile.mkdirs();
            }
            String bookurl = repo.getBook_url();
            int inx = bookurl.lastIndexOf("/");
            String bookfilename = bookurl.substring(inx + 1).replace(".epub", "").replace(".EPUB", "");
            inx = srcFile.getName().lastIndexOf(".");
            String extname = srcFile.getName().substring(inx);

            File dstFile = new File(saveroot + File.separator + bookfilename + extname);
            FileUtils.copyFile(srcFile, dstFile);


            Map<String, Object> result = new HashMap<String, Object>();
            String book_cover = "/" + EpubUtil.DEFAULT_BASE + "cover" + "/" + bookfilename + extname;
            result.put("book_cover", book_cover);

            try {
                ImageHelper.getTransferImageByScale(dstFile.getPath(), "small", 0.5);
                String book_cover_small = "/" + EpubUtil.DEFAULT_BASE + "cover" + "/" + bookfilename + "_small" + extname;
                result.put("book_cover_small", book_cover_small);
            } catch (Exception e) {
                //TODO
                //图片为打印格式
                result.put("book_cover_small", book_cover);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void outChapterForEpub(List<EpubKernel.EpubCatalog> catlist, Long book_id, Long pid, String pname, String ppath, String purl) {
        for (int j = 0; j < catlist.size(); j++) {
            String title = catlist.get(j).title;
            if ("封面".equals(title) || "目录".equals(title) || "出版说明".equals(title)) {
                continue;
            }
            BookChapter book_chapter = new BookChapter();
            book_chapter.setBook_id(book_id);
            book_chapter.setName(catlist.get(j).title);
            book_chapter.setUrl(StringUtils.substringAfterLast(purl + catlist.get(j).href, "/"));
            book_chapter.setPurl(StringUtils.substringBeforeLast(purl + catlist.get(j).href, "/") + "/");
            book_chapter.setPname(pname);
            book_chapter.setFormat(".epub");
            book_chapter.setPid(pid);
            book_chapter.setPath(ppath);
            try {
                book_chapter.setStart_page(Long.valueOf(catlist.get(j).order));
            } catch (Exception e) {
                book_chapter.setStart_page(0L);
            }
            book_chapter.setCode(catlist.get(j).tag);
            Long chapterid = bookChapterService.saveChapter(book_chapter);

            String curpath = ppath + chapterid + "|";

            if (catlist.get(j).subitem.size() > 0) {
                outChapterForEpub(catlist.get(j).subitem, book_id, chapterid, book_chapter.getName(), curpath, purl);
            }
        }
    }

    /**
     * 相关推荐
     *
     * @param org_id
     * @param cat_id
     * @param book_id
     * @param limit
     * @return
     */
    @Override
    public List<BookRepo> getSuggestBook(Long org_id, Long cat_id, Long book_id, Integer limit) {
        String book_author = bookRepoMapper.getAuthor(book_id);
        return bookRepoMapper.getSuggestBook(org_id, cat_id, book_author, limit, book_id);
    }

    /**
     * 检测输入是否含有emoji表情字符
     *
     * @param str
     * @return
     */
    @Override
    public Boolean containsEmoji(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (isEmojiCharacter(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    @Override
    public String getSchedule(Long book_id, Long member_id) {
        return bookRepoMapper.getSchedule(book_id, member_id);
    }

    @Override
    public void uploadBooks(List<FileForm> fileForms) {
        for (FileForm fileForm : fileForms) {
            if (!fileForm.getUrl().endsWith(".epub")) {
                continue;
            }
            BookRepo bookRepo = new BookRepo();
            bookRepo.setFile_name(fileForm.getName());
            bookRepo.setBook_url(fileForm.getUrl());
            autoParseForAll(bookRepo);
        }
    }

    @Override
    public void updateBookChapterPurl(Long book_id, String purl) {
        bookChapterMapper.updateBookChapterPurl(book_id, purl);
    }


    @Override
    public BookExportNums getNums() {
        BookExportNums bookExportNums = new BookExportNums();
        Integer bookNames = bookRepoMapper.bookNames();
        bookExportNums.setBookNames(bookNames);

        Integer bookAuthors = bookRepoMapper.bookAuthors();
        bookExportNums.setBookAuthors(bookAuthors);

        Integer publishers = bookRepoMapper.publishers();
        bookExportNums.setPublishers(publishers);

        Integer publishertimes = bookRepoMapper.publishertimes();
        bookExportNums.setPublisherTimes(publishertimes);

        Integer endTimes = bookRepoMapper.endTimes();
        bookExportNums.setEndTimes(endTimes);

        Integer bookIsbns = bookRepoMapper.bookIsbns();
        bookExportNums.setBookIsbns(bookIsbns);

        Integer remarks = bookRepoMapper.remarks();
        bookExportNums.setRemarks(remarks);

        Integer tags = bookRepoMapper.tags();
        bookExportNums.setTags(tags);

        Integer covers = bookRepoMapper.covers();
        bookExportNums.setCovers(covers);

        Integer price = bookRepoMapper.price();
        bookExportNums.setPrice(price);

        return bookExportNums;

    }


    @Override
    public void updateOrderById(Long book_id, Long cat_id, Long order_weight) {
        bookRepoMapper.updateOrderById(book_id, cat_id, order_weight);
    }

    //判断字符串是否为整数组成
    @Override
    public boolean isInteger(String str) {
        return pattern.matcher(str).matches();
    }

    @Override
    public List<BookRepo> exportTaskBook(Long task_id) {
        return bookRepoMapper.exportTaskBook(task_id);
    }

    @Override
    public List<UnBookErr> exportTaskErrBook(Long task_id) {
        return bookRepoMapper.exportTaskErrBook(task_id);
    }

    @Override
    public BookBaseInfo findBookPrice(Long bkid) {
        return bookRepoMapper.findBookPrice(bkid);
    }

    //删除索引
    public void deleteChapterIndexForEpub(Long book_id) {
        String indexDir = getIndexBaseDir();
        if (StringUtils.isEmpty(indexDir)) {
            return;
        }
        File fileIndex = new File(indexDir);
        analyzer = new StandardAnalyzer();
        try {
            directory = FSDirectory.open(FileSystems.getDefault().getPath(fileIndex.getPath()));
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, config);
            indexWriter.deleteDocuments(new Term("book_id", book_id.toString()));
            closeWriter();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    //获取图书索引路径
    public static String getIndexBaseDir() {
        try {
            File file = SpringContextUtil.getApplicationContext().getResource("").getFile();
            String filepath = file.getPath() + File.separator + indexPath + File.separator;
            if (!new File(filepath).exists()) {
                new File(filepath).mkdirs();
            }
            return filepath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void closeWriter() throws Exception {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }

    /**
     * 创建索引
     *
     * @param chapterList
     * @param file
     * @param laypath
     * @param book
     */
    public boolean createChapterIndexForEpubByChapterList(List<ProxyBookChapter> chapterList, File file, String laypath, BookRepo book) {
        deleteChapterIndexForEpub(book.getBook_id());
        String indexDir = getIndexBaseDir();
        if (StringUtils.isEmpty(indexDir)) {
            return false;
        }
        File fileIndex = new File(indexDir);
        int chaptercount = 0;
        String content;
        for (int ci = 0; ci < chapterList.size(); ci++) {
            chaptercount += 1;
            ProxyBookChapter chapter = chapterList.get(ci);
            String url = chapter.getUrl();
            if (StringUtils.isEmpty(url)) {
                continue;
            }
            if (url.endsWith(".html") || url.endsWith(".htm") || url.endsWith(".xhtml")) {
                String npath = laypath + File.separator + url;
                File tmpFile = new File(npath);
                if (!tmpFile.exists()) {
                    continue;
                }
                Integer chapterid = chapter.getId();
                String idstr = url;
                if (idstr.contains("front")) {
                    continue;
                }
                try {
                    Parser parser = new Parser();
                    parser.setEncoding("UTF-8");
                    parser.setURL(tmpFile.getPath());
                    TextExtractingVisitor visitor = new TextExtractingVisitor();
                    parser.setEncoding("UTF-8");
                    parser.visitAllNodesWith(visitor);
                    content = visitor.getExtractedText().replaceAll("[　\\t\\n\\r\\f(&nbsp;|gt) ]+", " ");
                    if (StringUtils.isNotEmpty(content)) {
                        analyzer = new StandardAnalyzer();
                        directory = FSDirectory.open(FileSystems.getDefault().getPath(fileIndex.getPath()));
                        IndexWriterConfig config = new IndexWriterConfig(analyzer);
                        indexWriter = new IndexWriter(directory, config);
                        Document indexdocument = new Document();
                        indexdocument.add(new TextField("filename", "" + file.getName(), Store.YES));
                        indexdocument.add(new TextField("content", content, Store.YES));
                        indexdocument.add(new TextField("path", file.getPath(), Store.YES));
                        indexdocument.add(new TextField("page_url", "" + chapterid, Store.YES));
                        indexdocument.add(new NumericDocValuesField("page_url", chapterid));
                        indexdocument.add(new TextField("show_inx", "" + chaptercount, Store.YES));
                        indexdocument.add(new TextField("book_id", "" + book.getBook_id(), Store.YES));
                        indexdocument.add(new TextField("book_name", "" + book.getBook_name(), Store.YES));
                        indexdocument.add(new TextField("chapter_name", "" + chapter.getTitle(), Store.YES));
                        indexWriter.addDocument(indexdocument);
                        indexWriter.commit();
                        closeWriter();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
        return true;
    }

    /**
     * 生成索引
     *
     * @param bookId
     */
    public boolean autoIndexForEpubByDBChapter(Long bookId) {
        try {
            File file = SpringContextUtil.getApplicationContext().getResource("").getFile();
            BookRepo book = bookRepoMapper.findByBookId(bookId);
            if (book == null) {
                return false;
            }
            String filePath = file.getPath() + book.getBook_url();
            File newfile = new File(filePath);
            if (newfile.exists()) {
                List<ProxyBookChapter> chapters = bookChapterMapper.findEpubChapter(bookId);
                if (chapters != null && chapters.size() > 0) {
                    /**
                     * 截取图书所在目录 /szyun/books/20171130
                     */
                    String filepath = StringUtils.substringBeforeLast(book.getBook_url(), "/");
                    /**
                     * 截取图书文件名  9787548023524
                     */
                    String bookUrl = StringUtils.substringBeforeLast(book.getFile_name(), ".");
                    String fileLaypath = file.getPath() + filepath + unfile + bookUrl;
                    if (!createChapterIndexForEpubByChapterList(chapters, new File(filePath), fileLaypath, book)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查找所有图书索引，返回符合条件的文件
     *
     * @param text 查找的字符串
     * @return 符合条件的文件List
     */
    @Override
    public BookResult searchDirectoryIndex(String text, Integer pageSize, Integer pageNum, String book_id) {
        BookResult bookResult = new BookResult();
        String indexDir = getIndexBaseDir();
        if (StringUtils.isEmpty(indexDir)) {
            return bookResult;
        }
        Page page = new Page<>(pageNum, pageSize);
        page.setAfterDocId(0);
        List<SearchResult> result = new ArrayList<>();
        Date date1 = new Date();
        Integer num = 226;
        try {
            File fileIndex = new File(indexDir);
            File[] files = fileIndex.listFiles();
            if (files == null || files.length < 1) {
                return bookResult;
            }
            directory = FSDirectory.open(FileSystems.getDefault().getPath(fileIndex.toString()));
            analyzer = new StandardAnalyzer();
            DirectoryReader ireader = DirectoryReader.open(directory);

            IndexSearcher isearcher = new IndexSearcher(ireader);
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(text);
            builder.add(query, BooleanClause.Occur.MUST);

            if (StringUtils.isNotEmpty(book_id)) {
                QueryParser parser3 = new QueryParser("book_id", analyzer);
                Query query3 = parser3.parse(book_id);
                builder.add(query3, BooleanClause.Occur.MUST);
            }
            SortField sortField = new SortField("page_url", SortField.Type.INT, false);
            org.apache.lucene.search.Sort sort = new org.apache.lucene.search.Sort(sortField);

            int totalRecord = searchTotalRecord(isearcher, builder.build());
            //设置总记录数

            page.setTotalRecord(totalRecord);

            ScoreDoc after = getLastScoreDoc(page.getCurrentPage(), page.getPageSize(), builder.build(), isearcher, sort);

            ScoreDoc[] hits = isearcher.searchAfter(after, builder.build(), page.getPageSize(), sort).scoreDocs;
            page.setAfterDocId(hits.length != 0 ? hits[hits.length - 1].doc : 0);

            SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(prefixHTML, suffixHTML);

            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                SearchResult resultItem = new SearchResult();
                Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
                highlighter.setTextFragmenter(new SimpleFragmenter(num));
                String highLightText = highlighter.getBestFragment(analyzer, "contents", hitDoc.get("content"));
                hitDoc.removeField("contents");
                resultItem.setWords(highLightText);
                resultItem.setChapterid(hitDoc.get("page_url"));
                resultItem.setChapter_name(hitDoc.get("chapter_name"));
                String show_inx = hitDoc.get("show_inx");
                resultItem.setPage(StringUtils.isEmpty(show_inx) ? null : Integer.parseInt(show_inx));
                String bookid = hitDoc.get("book_id");
                resultItem.setBookId(bookid);
                resultItem.setBook_name(hitDoc.get("book_name"));
                result.add(resultItem);

            }
            ireader.close();
            directory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date date2 = new Date();
        Long m_second = date2.getTime() - date1.getTime();
        bookResult.setTime(m_second);
        page.setDocList(result);
        bookResult.setData(page);
        return bookResult;
    }

    public static int searchTotalRecord(IndexSearcher searcher, Query query) throws IOException {
        TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
        if (topDocs == null || topDocs.scoreDocs == null || topDocs.scoreDocs.length == 0) {
            return 0;
        }
        ScoreDoc[] docs = topDocs.scoreDocs;
        return docs.length;
    }

    /**
     * 根据页码和分页大小获取上一次的最后一个ScoreDoc 获取这个scoreDoc的时候，如果有排序，一定加上排序（sort） 否则会出现ScoreDoc 无法转换FieldDoc的错误
     */
    private static ScoreDoc getLastScoreDoc(int pageIndex, int pageSize, Query query, IndexSearcher searcher, org.apache.lucene.search.Sort sort)
            throws IOException {
        if (pageIndex == 1) {
            // 如果是第一页就返回空
            return null;
        }
        // 获取上一页的数量
        int num = pageSize * (pageIndex - 1);
        TopDocs tds = searcher.search(query, num, sort);
        return tds.scoreDocs[num - 1];
    }
}
