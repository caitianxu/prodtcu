package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.api.beans.ShelfBook;
import com.cjsz.tech.api.service.BookShelfService;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.BookBaseInfo;
import com.cjsz.tech.book.domain.BookChapter;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.service.BookChapterService;
import com.cjsz.tech.book.service.BookOrgRelService;
import com.cjsz.tech.book.service.BookRepoService;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.meb.domain.MemberReadRecord;
import com.cjsz.tech.meb.service.MemberReadRecordService;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class BookController {
    @Autowired
    BookRepoService bookRepoService;
    @Autowired
    BookOrgRelService bookOrgRelService;
    @Autowired
    BookChapterService bookChapterService;
    @Autowired
    UnifyMemberService unifyMemberService;
    @Autowired
    BookShelfService bookShelfService;
    @Autowired
    MemberReadRecordService memberReadRecordService;

    @RequestMapping("/web/book/{book_id}/{chapterid}")
    public ModelAndView search(HttpServletRequest request, HttpServletResponse respone, RedirectAttributes redirectAttributes, @PathVariable String book_id, @PathVariable String chapterid) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            try {
                String path = request.getContextPath();
                String basePath = request.getScheme() + "://" + request.getServerName();
                if (request.getServerPort() != 80) {
                    basePath = basePath + ":" + request.getServerPort();
                }
                basePath += path;
                UnifyMember member = (UnifyMember) request.getSession().getAttribute("member_info");
                if (member != null) {
                    String member_id = member.getMember_id().toString();
                    Long bkid = 0L;
                    bkid = Long.parseLong(book_id);
                    BookRepo book = bookRepoService.findByBookId(bkid);
                    BookBaseInfo bookObj = bookRepoService.findBaseInfoByBookId(bkid);
                    String schedule = bookRepoService.getSchedule(bkid, Long.parseLong(member_id));
                    if (schedule == null) {
                        schedule = "0|0";
                        bookObj.setSchedule(schedule);
                    } else {
                        bookObj.setSchedule(schedule);
                    }
                    if (bookObj != null) {
                        bookObj.setBook_cover(basePath + bookObj.getBook_cover());
                        bookObj.setBook_cover_small(basePath + bookObj.getBook_cover_small());
                        List<BookChapter> chapterList = bookChapterService.getTreeEpubChapters(book_id);
                        mv.setViewName("web/book");
                        request.setAttribute("book_id", book_id);
                        mv.addObject("book", bookObj);
                        mv.addObject("chapterList", chapterList);

                        String content = "";
                        if (chapterList.size() > 0) {
                            BookChapter chapter = bookChapterService.findMyChapterById(Long.valueOf(book_id), Long.valueOf(chapterid));
                            //进入阅读器，更换章节，用户阅读记录，图书指数记录（阅读）;web:000004
                            MemberReadRecord memberReadRecord = new MemberReadRecord(member.getMember_id(), Long.valueOf(book_id),2, Long.valueOf(chapterid));
                            memberReadRecord.setBook_name(book.getBook_name());
                            memberReadRecord.setBook_cover(book.getBook_cover_small());
                            memberReadRecord.setChapter_name(chapter.getName());
                            memberReadRecord.setPlan(chapterid + "|1.00");
                            memberReadRecordService.updateRecord(member.getOrg_id(), memberReadRecord, "pc");
                            //章节内容
                            content = loadEpubChapter(book, chapter, basePath);
                            mv.addObject("content", content);
                            request.setAttribute("chapterid", chapterid);
                            //成功
                            mv.setViewName("web/book");
                        } else {
                            mv.setViewName("web/err");
                        }
                    } else {
                        mv.setViewName("web/err");
                    }
                } else {
                    mv.setViewName("web/login");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("book_id", "");
                request.setAttribute("chapterid", "");
                mv.addObject("book", "");
                mv.addObject("chapterList", "");
                mv.addObject("content", "");
                mv.addObject("member_token", "");
            }
        }
        else{
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping("/web/book/{book_id}")
    public ModelAndView search_byid(HttpServletRequest request, HttpServletResponse respone, @PathVariable String book_id) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            try {
                String path = request.getContextPath();
                String basePath = request.getScheme() + "://" + request.getServerName();
                if (request.getServerPort() != 80) {
                    basePath = basePath + ":" + request.getServerPort();
                }
                basePath += path;
                //图书信息
                String token = (String) request.getSession().getAttribute("member_token");
                UnifyMember member = null;
                if (token != null) {
                    member = (UnifyMember) request.getSession().getAttribute("member_info");
                }
                if (member != null) {
                    String member_id = member.getMember_id().toString();
                    Long bkid = 0L;
                    bkid = Long.parseLong(book_id);
                    BookRepo book = bookRepoService.findByBookId(bkid);
                    BookBaseInfo bookObj = bookRepoService.findBaseInfoByBookId(bkid);
                    String schedule = bookRepoService.getSchedule(bkid, Long.parseLong(member_id));
                    if (schedule == null) {
                        schedule = "0|0";
                        bookObj.setSchedule(schedule);
                    } else {
                        bookObj.setSchedule(schedule);
                    }
                    if (bookObj != null) {
                        bookObj.setBook_cover(basePath + bookObj.getBook_cover());
                        bookObj.setBook_cover_small(basePath + bookObj.getBook_cover_small());
                        List<BookChapter> chapterList = bookChapterService.getTreeEpubChapters(book_id);

                        mv.setViewName("web/book");
                        request.setAttribute("book_id", book_id);
                        mv.addObject("book", bookObj);
                        mv.addObject("chapterList", chapterList);

                        String chapterid = "";
                        String content = "";
                        if (chapterList.size() > 0) {
                            chapterid = chapterList.get(0).getId().toString();
                            BookChapter chapter = bookChapterService.findMyChapterById(Long.valueOf(book_id), Long.valueOf(chapterid));
                            //进入阅读器，更换章节，用户阅读记录，图书指数记录（阅读）;web:000004
                            MemberReadRecord memberReadRecord = new MemberReadRecord(member.getMember_id(), Long.valueOf(book_id),2, Long.valueOf(chapterid));
                            memberReadRecord.setBook_name(book.getBook_name());
                            memberReadRecord.setBook_cover(book.getBook_cover_small());
                            memberReadRecord.setChapter_name(chapter.getName());
                            memberReadRecord.setPlan(chapterid + "|1.00");
                            memberReadRecordService.updateRecord(member.getOrg_id(), memberReadRecord, "pc");
                            //章节内容
                            content = loadEpubChapter(book, chapter, basePath);
                            mv.addObject("content", content);
                            request.setAttribute("chapterid", chapterid);
                            //成功
                            mv.setViewName("web/book");
                        } else {
                            mv.setViewName("web/err");
                        }
                    } else {
                        mv.setViewName("web/err");
                    }
                } else {
                    mv.setViewName("web/login");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("book_id", "");
                request.setAttribute("chapterid", "");
                mv.addObject("book", "");
                mv.addObject("chapterList", "");
                mv.addObject("content", "");
                mv.setViewName("web/err");
            }
        }
        else {
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping("/web/book")
    public ModelAndView search_(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("web/err");
        return mv;
    }

    /**
     * 根据EPUB书籍编码，章节编码，获取章节内容
     *
     * @param book
     * @param chapter
     * @return
     */
    public String loadEpubChapter(BookRepo book, BookChapter chapter, String webpath) {
        try {
            File file = SpringContextUtil.getApplicationContext().getResource("").getFile();
            //图书解压路径
            String un_book_path = book.getBook_url().replace(book.getFile_name(),"")+ "unfile/"+ book.getFile_name().replace(".epub","");
            String chapter_path = file.getPath() + un_book_path + "/" + chapter.getPurl() + chapter.getUrl();
            File un_book_file = new File(chapter_path);
            if(!un_book_file.exists()){
                return  "无内容";
            }
            String content = FileUtils.readFileToString(un_book_file);
            String cssPath = webpath + un_book_path + "/" + chapter.getPurl();
            if (cssPath.endsWith("/")) {
                cssPath = cssPath.substring(0, cssPath.length() - 1);
            }
//            content = content.replaceAll("\\.\\.", "");
            content = content.replace("src=\"", "src=\"" + cssPath + "/");
//            content = content.replace("href=\"", "href=\"" + cssPath + "/");
            content = content.replace("href=\"", "");


            String regxp = "<\\s*html\\s+([^>]*)\\s*>";
            Pattern pattern = Pattern.compile(regxp);
            Matcher matcher = pattern.matcher(content);
            boolean result = matcher.find();
            if(result){
                StringBuffer sb = new StringBuffer();
                matcher.appendReplacement(sb, "<html>");
                matcher.appendTail(sb);
                return sb.toString();
            }else{
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "无内容";
    }

    /**
     * 个人书架-书列表
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/site/bookShelf/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object shelfList(HttpServletRequest request, PageConditionBean bean) {
        try {
            String token = (String) request.getSession().getAttribute("member_token");
            String token_type = (String) request.getSession().getAttribute("token_type");
            UnifyMember member = null;
            if (token != null) {
                member = unifyMemberService.findByToken(token, token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            Sort sort = new Sort(Sort.Direction.DESC, "update_time");
            Object data = bookShelfService.sitePageQuery(sort, bean, member.getMember_id());
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 个人书架-增加书
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/site/bookShelf/addBook", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelfAdd(HttpServletRequest request) {
        try {
            String token = (String) request.getSession().getAttribute("member_token");
            String token_type = (String) request.getSession().getAttribute("token_type");
            UnifyMember member = null;
            if (token != null) {
                member = unifyMemberService.findByToken(token.toString(), token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            String book_id = request.getParameter("book_id");
            BookRepo repoBook = bookRepoService.findByBookId(Long.parseLong(book_id));
            ShelfBook shelfBook = bookShelfService.findShelfBookByUser(member.getMember_id(), Long.parseLong(book_id));
            if (shelfBook != null) {
                if (shelfBook.getIs_delete() == 1) {
                    shelfBook.setIs_delete(2);//是否删除（1：是  2：否）
                    shelfBook.setUpdate_time(new Date());
                    bookShelfService.updateBookShelf(shelfBook);
                } else {
                    return JsonResult.getError("该图书已在书架中");
                }
            } else {
                shelfBook = new ShelfBook();
                shelfBook.setBk_id(Long.parseLong(book_id));
                shelfBook.setBk_name(repoBook.getBook_name());
                shelfBook.setBk_cover(repoBook.getBook_cover());
                shelfBook.setBk_cover_small(repoBook.getBook_cover_small());
                shelfBook.setBk_url(repoBook.getBook_url());
                shelfBook.setBk_author(repoBook.getBook_author());
                shelfBook.setUser_id(member.getMember_id());
                shelfBook.setUser_name(member.getNick_name());
                shelfBook.setIs_delete(2);//是否删除（1：是  2：否）
                shelfBook.setCreate_time(new Date());
                shelfBook.setUpdate_time(new Date());
                shelfBook.setSchedule("");
                bookShelfService.saveShelfBook(shelfBook, member.getOrg_id(), token_type);
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_ADD);
            result.setData(shelfBook);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 个人书架-删除书
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/site/bookShelf/delBook", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelfDelete(HttpServletRequest request) {
        try {
            String token = (String) request.getSession().getAttribute("member_token");
            String token_type = (String) request.getSession().getAttribute("token_type");
            UnifyMember member = null;
            if (token != null) {
                member = unifyMemberService.findByToken(token.toString(), token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            String book_ids = request.getParameter("book_id");
            bookShelfService.deleteShelfBooks(member.getMember_id(), book_ids);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 记录用户阅读习惯（阅读背景，字体）
     * @param request
     * @return
     */
    @RequestMapping(value = "/site/member/readHabit",method = RequestMethod.POST)
    @ResponseBody
    public Object readHabit(HttpServletRequest request){
        try{
            String token = (String) request.getSession().getAttribute("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = null;
            if (token != null) {
                member = unifyMemberService.findByToken(token.toString(), token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            String read_size = request.getParameter("read_size");
            if (StringUtils.isNotEmpty(read_size)){
                member.setRead_size(Integer.parseInt(read_size));
            }
            String read_skin = request.getParameter("read_skin");
            if (StringUtils.isNotEmpty(read_skin)){
                member.setRead_skin(read_skin);
            }
            unifyMemberService.updateMember(member);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(member);
            HttpSession session = request.getSession(true);
            session.setAttribute("member_info", jsonResult.data);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
