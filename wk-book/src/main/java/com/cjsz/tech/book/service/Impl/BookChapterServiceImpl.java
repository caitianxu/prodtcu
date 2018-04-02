package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.beans.ProxyBookChapter;
import com.cjsz.tech.book.domain.BookChapter;
import com.cjsz.tech.book.mapper.BookChapterMapper;
import com.cjsz.tech.book.service.BookChapterService;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookChapterServiceImpl implements BookChapterService {

    @Autowired
    private BookChapterMapper bookChapterMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Object pageQuery(Sort sort, Integer page, Integer rows, String searchText) {
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(page, rows);
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<BookChapter> result = bookChapterMapper.findList();
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    //@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "图书章节")
    @Override
    public Long saveChapter(final BookChapter chapter) {
        if (chapter.getCreate_time() == null) {
            chapter.setCreate_time(new Date());
        }
        final String insertSql = "insert into book_chapter(book_id,format,path,code,name,url,purl,start_page,pid,pname,create_time) values( ? , ? , ? , ? , ? , ? , ?, ? , ?, ? , ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, chapter.getBook_id());
                ps.setString(2, chapter.getFormat());
                ps.setString(3, chapter.getPath());
                ps.setString(4, chapter.getCode());
                ps.setString(5, chapter.getName());
                ps.setString(6, chapter.getUrl());
                ps.setString(7, chapter.getPurl());
                ps.setLong(8, chapter.getStart_page());
                ps.setLong(9, chapter.getPid());
                ps.setString(10, chapter.getPname());
                ps.setDate(11, new java.sql.Date(chapter.getCreate_time().getTime()));
                return ps;
            }
        }, keyHolder);
        Long generatedId = keyHolder.getKey().longValue();
        return generatedId;
    }

    @Override
    public BookChapter findById(Long id) {
        return bookChapterMapper.findById(id);
    }

    @Override
    public BookChapter findMyChapterById(Long bkid, Long chapterid) {
        String sql = "select * from book_chapter where book_id=" + bkid + " and id=" + chapterid;
        List<BookChapter> list = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(BookChapter.class));
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<BookChapter> findList() {
        return bookChapterMapper.findList();
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "图书章节")
    public void deleteById(Long id) {
        bookChapterMapper.deleteById(id);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "图书章节")
    public void deleteByBookId(Long bookId) {
        bookChapterMapper.deleteByBookId(bookId);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "图书章节")
    public void save(BookChapter saveobj) {
        bookChapterMapper.insert(saveobj);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书章节")
    public void update(BookChapter updateobj) {
        BookChapter updateobj1 = findById(updateobj.getId());
        BeanUtils.copyProperties(updateobj, updateobj1);
        bookChapterMapper.updateByPrimaryKey(updateobj1);
    }

    @Override
    public List<BookChapter> findPdfChapterByBook_id(Long book_id) {
        return bookChapterMapper.findPdfChapterByBook_id(book_id);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "图书章节")
    public void deleteChapterByBookAndType(Long book_id, String fmt) {
        String sql = "delete from book_chapter where book_id=" + book_id + " and format='" + fmt + "'";
        jdbcTemplate.update(sql);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书章节")
    public void updatePath(Long book_id, Long chapterid, String curpath) {
        String sql = "update book_chapter set path='" + curpath + "' where book_id=" + book_id + " and id=" + chapterid;
        jdbcTemplate.update(sql);
    }


    @Override
    public List<ProxyBookChapter> findEpubChapterForTwo(String bkid) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select  a.id,a.name as title,a.start_page as inx,a.pid,a.path,a.url ,b2.start_page as pdf_page  from ");
        sb.append(" (select * from book_chapter  where book_id=" + bkid + " and format='.epub') a ");
        sb.append("  join (select * from book_chapter where book_id=" + bkid + " and format='.pdf')  b2 ");
        sb.append(" on  a.book_id=b2.book_id and a.name=b2.name and a.pname=b2.pname ");
        sb.append(" where length(a.path)-length(replace(a.path,'|',''))<=5 ");
        sb.append(" order by a.id asc ");
        System.out.println("book chapters sql:" + sb.toString());
        return jdbcTemplate.query(sb.toString(), BeanPropertyRowMapper.newInstance(ProxyBookChapter.class));
    }

    //目录结构化——findEpubChapterForTwo
    @Override
    public List<ProxyBookChapter> getTreeChapters(String bkid) {
        List<ProxyBookChapter> chapters = findEpubChapterForTwo(bkid);
        List<ProxyBookChapter> p_chapters = new ArrayList<ProxyBookChapter>();//第一级目录
        for (ProxyBookChapter chapter : chapters) {
            if (chapter.getPid() == 0) {
                List<ProxyBookChapter> children;
                children = this.getChildrenChapters(chapters, chapter.getId());
                chapter.setChild(children);
                p_chapters.add(chapter);
            }
        }
        return p_chapters;
    }

    @Override
    public List<ProxyBookChapter> getChildrenChapters(List<ProxyBookChapter> chapters, Integer pid) {
        List<ProxyBookChapter> children = new ArrayList<ProxyBookChapter>();
        for (ProxyBookChapter chapter : chapters) {
            if (chapter.getPid().equals(pid)) {
                chapter.setChild(getChildrenChapters(chapters, chapter.getId()));
                children.add(chapter);
            }
        }
        return children;
    }


    @Override
    public List<BookChapter> findEpubChapters(String bookId) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select * from book_chapter where book_id=" + bookId + " and format='.epub'");
        return jdbcTemplate.query(sb.toString(), BeanPropertyRowMapper.newInstance(BookChapter.class));
    }

    @Override
    public List<BookChapter> findPdfChapters(String bookId) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select * from book_chapter where book_id=" + bookId + " and format='.pdf'");
        return jdbcTemplate.query(sb.toString(), BeanPropertyRowMapper.newInstance(BookChapter.class));
    }

    //目录结构化——findEpubChapters
    @Override
    public List<BookChapter> getTreeEpubChapters(String bkid) {
        List<BookChapter> p_chapters = new ArrayList<>();//第一级目录
        List<BookChapter> chapters = findEpubChapters(bkid);
        if (chapters != null && chapters.size()>0) {
            if (chapters.size() > 2) {
                int num = (chapters.size() / 3);
                for (int i = 0; i < num; i++) {
                    chapters.get(i).setIs_free(1);
                }
            } else {
                chapters.get(0).setIs_free(1);
            }
            for (BookChapter chapter : chapters) {
                if (chapter.getPid() == 0) {
                    List<BookChapter> children;
                    children = this.getChildrenChaptersOne(chapters, chapter.getId());
                    chapter.setChild(children);
                    p_chapters.add(chapter);
                }
            }
        }
        return p_chapters;
    }

    //目录结构化——findPdfChapters
    @Override
    public List<BookChapter> getTreePdfChapters(String bkid) {
        List<BookChapter> chapters = findPdfChapters(bkid);
        List<BookChapter> p_chapters = new ArrayList<>();//第一级目录
        for (BookChapter chapter : chapters) {
            if (chapter.getPid() == 0) {
                List<BookChapter> children;
                children = this.getChildrenChaptersOne(chapters, chapter.getId());
                chapter.setChild(children);
                p_chapters.add(chapter);
            }
        }
        return p_chapters;
    }

    @Override
    public List<BookChapter> getChildrenChaptersOne(List<BookChapter> chapters, Long pid) {
        List<BookChapter> children = new ArrayList<>();
        for (BookChapter chapter : chapters) {
            if (chapter.getPid().equals(pid)) {
                chapter.setChild(getChildrenChaptersOne(chapters, chapter.getId()));
                children.add(chapter);
            }
        }
        return children;
    }

    @Override
    //获取Epub章节目录（url不重复）
    public List<BookChapter> findUniqueUrlEpubChapters(String bkidstr) {
        List<BookChapter> chapters = findEpubChapters(bkidstr);
        for (int i = 0; i < chapters.size(); i++) {
            if (equalsPidUrl(chapters, chapters.get(i))) {
                chapters.remove(chapters.get(i));
                i--;
            }
        }
        return chapters;
    }

    @Override
    public List<Long> findChapterIdsByBKId(Long bkid) {
        return bookChapterMapper.findChapterIdsByBKId(bkid);
    }

    @Override
    public Integer selectCountChapter(Long book_id) {
        return bookChapterMapper.selectCountChapter(book_id);
    }

    public boolean equalsPidUrl(List<BookChapter> chapters, BookChapter chapter) {
        for (BookChapter bookChapter : chapters) {
            if (chapter.getPid().equals(bookChapter.getId()) && bookChapter.getUrl().equals(chapter.getUrl())) {
                return true;
            }
        }
        return false;
    }

}