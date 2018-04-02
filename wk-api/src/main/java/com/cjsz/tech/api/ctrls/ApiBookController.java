package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.APIConstants;
import com.cjsz.tech.api.beans.ShelfBook;
import com.cjsz.tech.api.service.BookShelfService;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.BookBean;
import com.cjsz.tech.book.beans.BookResult;
import com.cjsz.tech.book.beans.FindBookBean;
import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.book.domain.BookChapter;
import com.cjsz.tech.book.domain.BookIndexRecord;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.service.*;
import com.cjsz.tech.count.domain.SearchCount;
import com.cjsz.tech.count.service.SearchCountService;
import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.domain.MemberReadRecord;
import com.cjsz.tech.meb.service.MemberReadRecordService;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.StringUtil;
import com.hankcs.hanlp.HanLP;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LuoLi on 2017/4/11 0011.
 */
@Controller
public class ApiBookController {

    @Autowired
    private BookOrgRelService bookOrgRelService;
    @Autowired
    private BookCatService bookCatService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private BookIndexRecordService bookIndexRecordService;
    @Autowired
    private BookShelfService bookShelfService;
    @Autowired
    private BookRepoService bookRepoService;
    @Autowired
    private MemberReadRecordService memberReadRecordService;
    @Autowired
    private SearchCountService searchCountService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private UnifyMemberService unifyMemberService;
    @Autowired
    private BookChapterService bookChapterService;

    /**
     * 热门搜索
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/searchKey/getSearchKeyList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getSearchKeyList(HttpServletRequest request) {
        try {
            String org_id = request.getParameter("org_id");
            if (StringUtils.isEmpty(org_id)) {
                org_id = "1";
            }
            List<SearchCount> searchKeyList = searchCountService.selectByOrgIdAndCount(Long.valueOf(org_id), 10);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(searchKeyList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 图书列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/book/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getBookList(HttpServletRequest request) {
        try {
            String org_id = request.getParameter("org_id");
            String book_cat_id = request.getParameter("book_cat_id");
            String pageSize = request.getParameter("pageSize");
            String pageNum = request.getParameter("pageNum");
            String searchText = request.getParameter("searchText");
            if (StringUtils.isEmpty(org_id)) {
                org_id = "1";
            }
            if (StringUtils.isEmpty(searchText)) {
                searchText = "";
            } else {
                //添加搜索记录
                SearchCount searchCount = searchCountService.selectByNameAndOrgId(searchText, Long.valueOf(org_id));
                if (searchCount == null) {
                    //搜索前未搜索过本次搜索内容
                    Organization org = organizationService.selectById(Long.valueOf(org_id));
                    searchCount = new SearchCount();
                    searchCount.setOrg_id(org.getOrg_id());
                    searchCount.setOrg_name(org.getOrg_name());
                    searchCount.setName(searchText);
                    searchCount.setSearch_count(1L);
                    searchCount.setStatus(1);
                    searchCountService.saveSearchCount(searchCount);
                } else {
                    //搜索前已搜索过本次搜索内容
                    Long count = searchCount.getSearch_count() + 1L;
                    searchCount.setSearch_count(count);
                    searchCountService.updateSearchCount(searchCount);
                }
            }
            if (StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(pageNum)) {
                return JsonResult.getError("分页信息不能为空！");
            }
            FindBookBean bean = new FindBookBean();
            bean.setPageSize(Integer.valueOf(pageSize));
            bean.setPageNum(Integer.valueOf(pageNum));
            bean.setSearchText(searchText);
            bean.setOrg_id(Long.valueOf(org_id));
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            if (book_cat_id != null && StringUtils.isNotEmpty(book_cat_id)) {
                bean.setBook_cat_id(Long.valueOf(book_cat_id));
                if ("-1".equals(book_cat_id)) {
                    //精选
                    sort = new Sort(Sort.Direction.ASC, "is_recommend").and(sort);
                } else if ("0".equals(book_cat_id)) {
                    //热门
                    sort = new Sort(Sort.Direction.ASC, "is_hot").and(sort);
                }
            }
            Object obj = bookOrgRelService.sitePageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 图书分类
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/bookCat/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getBookCatList(HttpServletRequest request) {
        try {
            String org_id = request.getParameter("org_id");
            if (StringUtils.isEmpty(org_id)) {
                org_id = "1";
            }
            List<BookCat> bookCatList = bookCatService.selectFirstCatsByOrgId(Long.valueOf(org_id));
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(bookCatList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 移动端图书详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/book/getBookInfo", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getBookInfo(HttpServletRequest request) {
        try {
            String device_type = request.getParameter("device_type");
            String bookid = request.getParameter("book_id");
            Long member_id = 0L;
            Long org_id = 1L;
            String token = request.getParameter("member_token");
            if (token != null) {
                Member member = memberService.findByToken(token, "2");
                if (member != null) {
                    member_id = member.getMember_id();
                    if (member.getOrg_id() != null && member.getOrg_id() != 0 && member.getOrg_id() != -1) {
                        org_id = member.getOrg_id();
                    }
                }
            }
            BookBean bookInfo = bookOrgRelService.findByOrgIdAndBookIdAndMemberId(org_id, Long.valueOf(bookid), member_id);
            //点击图书，图书指数记录
            //record_type(1:点击详情;2:收藏;3:分享;4:评论;5:阅读);web:000004
            String device_type_code = "000004";
            if (StringUtils.isNotEmpty(device_type)) {
                if ("android".equals(device_type)) {
                    device_type_code = "000003";
                } else if ("ios".equals(device_type)) {
                    device_type_code = "000002";
                }
            }
            BookIndexRecord bookIndexRecord = new BookIndexRecord(org_id, Long.valueOf(bookid), 2, member_id, 1, new Date(), device_type_code);
            bookIndexRecordService.addRecord(bookIndexRecord);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(bookInfo);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 个人书架-书列表
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/bookShelf/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object shelfList(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            Member member = null;
            if (member_token != null) {
                member = memberService.findByToken(member_token, "2");
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            List<ShelfBook> bookList = bookShelfService.findShelfBookList(member.getMember_id());
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(bookList);
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
    @RequestMapping(value = "/api/bookShelf/addBook", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelfAdd(HttpServletRequest request) {
        try {
            String device_type = request.getParameter("device_type");
            String book_id = request.getParameter("book_id");
            String org_id = request.getParameter("org_id");
            String member_token = request.getParameter("member_token");
            if (StringUtils.isEmpty(book_id) && StringUtils.isEmpty(org_id) && StringUtils.isEmpty(member_token)) {
                String path = request.getContextPath();
                String basePath = request.getScheme() + "://" + request.getServerName();
                if (request.getServerPort() != 80) {
                    basePath = basePath + ":" + request.getServerPort();
                }
                basePath += path;
                String url = basePath + "/pkgApp";
                JsonResult result = JsonResult.getError("");
                result.setData(url);
                return result;
            }
            if (StringUtil.isEmpty(org_id)) {
                return JsonResult.getError("找不到机构！");
            }
            Member member = null;
            if (member_token != null) {
                member = memberService.findByToken(member_token, "2");
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            } else {
                //如果会员没有绑定机构，则绑定org_id
                if (member.getOrg_id() == null || member.getOrg_id() == -1) {
                    member.setOrg_id(Long.parseLong(org_id));
                    memberService.updateMember(member);
                }
            }
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
                bookShelfService.saveShelfBook(shelfBook, member.getOrg_id(), device_type);
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
     * 个人书架-增加书(安卓暂时用)
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/bookShelf/addBook1", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelfAdd1(HttpServletRequest request) {
        try {
            String device_type = request.getParameter("device_type");
            String book_id = request.getParameter("b");
            String org_id = request.getParameter("o");
            String member_token = request.getParameter("token");
            if (StringUtils.isEmpty(book_id) && StringUtils.isEmpty(org_id) && StringUtils.isEmpty(member_token)) {
                String path = request.getContextPath();
                String basePath = request.getScheme() + "://" + request.getServerName();
                if (request.getServerPort() != 80) {
                    basePath = basePath + ":" + request.getServerPort();
                }
                basePath += path;
                String url = basePath + "/pkgApp";
                JsonResult result = JsonResult.getError("");
                result.setData(url);
                return result;
            }
            if (StringUtil.isEmpty(org_id)) {
                return JsonResult.getError("找不到机构！");
            }
            Member member = null;
            if (member_token != null) {
                member = memberService.findByToken(member_token, "2");
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            } else {
                //如果会员没有绑定机构，则绑定org_id
                if (member.getOrg_id() == null || member.getOrg_id() == -1) {
                    member.setOrg_id(Long.parseLong(org_id));
                    memberService.updateMember(member);
                }
            }
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
                bookShelfService.saveShelfBook(shelfBook, member.getOrg_id(), device_type);
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
    @RequestMapping(value = "/api/bookShelf/delBook", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelfDelete(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            Member member = null;
            if (member_token != null) {
                member = memberService.findByToken(member_token, "2");
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
     * 个人书架-读书进度
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/bookShelf/schedule", method = {RequestMethod.POST})
    @ResponseBody
    public Object book_schedule(HttpServletRequest request) {
        //TOKEN
        String member_token = request.getParameter("member_token");
        if (StringUtil.isEmpty(member_token)) {
            return JsonResult.getError(APIConstants.TOKEN_NULL);
        }
        //增加书架
        String book_id = request.getParameter("book_id");
        if (StringUtil.isEmpty(book_id)) {
            return JsonResult.getError(APIConstants.MEMBER_SHELF_NOTHING_ERROR);
        }
        BookRepo repoBook = bookRepoService.findByBookId(Long.parseLong(book_id));
        if (repoBook == null) {
            return JsonResult.getError("图书不存在");
        }
        //schedule
        String schedule = request.getParameter("schedule");
        if (StringUtil.isEmpty(schedule)) {
            return JsonResult.getError("进度为空");
        }
        Long member_id = 0L;
        Member member = memberService.findByToken(member_token, "2");
        if (member == null) {
            return JsonResult.getError(Constants.LOGIN_BIND_NOUSER);
        }
        member_id = member.getMember_id();
        ShelfBook book = bookShelfService.findShelfBookByUser(member_id, Long.parseLong(book_id));
        if (book != null) {
            book.setSchedule(schedule);
            book.setUpdate_time(new Date());
            bookShelfService.updateBookShelf(book);
            JsonResult result = JsonResult.getSuccess("进度修改成功");
            result.setData(book);
            return result;
        } else {
            return JsonResult.getError("找不到图书");
        }
    }

    /**
     * 用户阅读记录
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/book/updateMemberReadRecord", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updateMemberReadRecord(HttpServletRequest request) {
        try {
            String member_id = request.getParameter("member_id");
            String book_id = request.getParameter("book_id");
            String org_id = request.getParameter("org_id");//图书的机构Id
            String last_chapter_id = request.getParameter("last_chapter_id");
            String device_type = request.getParameter("device_type");
            MemberReadRecord memberReadRecord = new MemberReadRecord(Long.valueOf(member_id), Long.valueOf(book_id), 2, Long.valueOf(last_chapter_id));
            Long bkid = Long.parseLong(book_id);
            BookRepo book = bookRepoService.findByBookId(bkid);
            BookChapter chapter = bookChapterService.findMyChapterById(Long.valueOf(book_id), Long.valueOf(last_chapter_id));
            memberReadRecord.setBook_name(book.getBook_name());
            memberReadRecord.setBook_cover(book.getBook_cover_small());
            memberReadRecord.setChapter_name(chapter.getName());
            memberReadRecordService.updateRecord(Long.valueOf(org_id), memberReadRecord, device_type);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 个人书架-书列表-v2
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v2/api/bookShelf/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object shelfList_v2(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = null;
            if (member_token != null) {
                member = unifyMemberService.findByToken(member_token, token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            List<ShelfBook> bookList = bookShelfService.findShelfBookList_v2(member.getMember_id());
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(bookList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 个人书架-增加书-v2
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v2/api/bookShelf/addBook", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelfAdd_v2(HttpServletRequest request) {
        try {
            String book_id = request.getParameter("book_id");
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            String book_type = request.getParameter("book_type");
            if (StringUtils.isEmpty(book_type)) {
                book_type = "2";
            }
            Integer bk_type = Integer.parseInt(book_type);
            UnifyMember member = null;
            if (member_token != null) {
                member = unifyMemberService.findByToken(member_token, token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            BookRepo repo = bookRepoService.findByBookId_v2(Long.parseLong(book_id));
            ShelfBook shelfBook = bookShelfService.findShelfBookByUser_v2(member.getMember_id(), Long.parseLong(book_id));
            if (shelfBook != null) {
                if (shelfBook.getIs_delete() == 1) {
                    shelfBook.setIs_delete(2);//是否删除（1：是  2：否）
                    shelfBook.setCreate_time(new Date());
                    bookShelfService.updateBookShelf_v2(shelfBook);
                } else {
                    return JsonResult.getError("该书已经在书架中！");
                }
            } else {
                shelfBook = new ShelfBook();
                shelfBook.setBk_id(Long.parseLong(book_id));
                shelfBook.setBk_name(repo.getBook_name());
                shelfBook.setBk_cover(repo.getBook_cover());
                shelfBook.setBk_cover_small(repo.getBook_cover_small());
                shelfBook.setBk_url(repo.getBook_url());
                shelfBook.setBk_author(repo.getBook_author());
                shelfBook.setUser_id(member.getMember_id());
                shelfBook.setUser_name(member.getNick_name());
                shelfBook.setIs_delete(2);//是否删除（1：是  2：否）
                shelfBook.setCreate_time(new Date());
                shelfBook.setUpdate_time(new Date());
                shelfBook.setSchedule("");
                shelfBook.setBook_type(bk_type);
                bookShelfService.saveShelfBook_v2(shelfBook);
            }
            //添加图书收藏统计
            BookIndexRecord bookIndexRecord = new BookIndexRecord();
            bookIndexRecord.setDevice_type_code(token_type);
            bookIndexRecord.setMember_id(member.getMember_id());
            bookIndexRecord.setRecord_type(2);
            bookIndexRecord.setOrg_id(member.getOrg_id());
            bookIndexRecord.setBook_id(Long.parseLong(book_id));
            bookIndexRecord.setBook_type(bk_type);
            bookIndexRecordService.addRecord(bookIndexRecord);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_ADD);
            result.setData(shelfBook);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }

    }


    /**
     * 个人书架-删除书-v2
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v2/api/bookShelf/delBook", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelDelete_v2(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = null;
            if (member_token != null) {
                member = unifyMemberService.findByToken(member_token, token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            String book_ids = request.getParameter("book_id");
            bookShelfService.deleteShelfBooks_v2(member.getMember_id(), book_ids);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }

    }

    /**
     * 热门关键词-v2
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v2/api/searchKey/getSearchkeyList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getSearchKeyList_v2(HttpServletRequest request) {
        try {
            String org_id = request.getParameter("org_id");
            String display = request.getParameter("display");
            if (StringUtils.isEmpty(org_id)) {
                org_id = "1";
            }
            if (StringUtils.isEmpty(display)) {
                display = "10";
            }
            List<SearchCount> searchKeyList = searchCountService.selectByOrgIdAndCount_v2(Long.valueOf(org_id), Long.valueOf(display));
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(searchKeyList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 个人书架-读书进度
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/v2/api/bookShelf/schedule", method = {RequestMethod.POST})
    @ResponseBody
    public Object book_schedule_v2(HttpServletRequest request) {
        //TOKEN
        String member_token = request.getParameter("member_token");
        if (StringUtil.isEmpty(member_token)) {
            return JsonResult.getExpire(APIConstants.TOKEN_NULL);
        }
        //增加书架
        String book_id = request.getParameter("book_id");
        if (StringUtil.isEmpty(book_id)) {
            return JsonResult.getError(APIConstants.MEMBER_SHELF_NOTHING_ERROR);
        }
        BookRepo repoBook = bookRepoService.findByBookId(Long.parseLong(book_id));
        if (repoBook == null) {
            return JsonResult.getError("图书不存在");
        }
        //schedule
        String schedule = request.getParameter("schedule");
        if (StringUtil.isEmpty(schedule)) {
            return JsonResult.getError("进度为空");
        }
        Long member_id = 0L;
        String token_type = request.getParameter("token_type");
        UnifyMember member = null;
        if (member_token != null) {
            member = unifyMemberService.findByToken(member_token, token_type);
        }
        if (member == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        member_id = member.getMember_id();
        ShelfBook book = bookShelfService.findShelfBookByUser(member_id, Long.parseLong(book_id));
        if (book != null) {
            book.setSchedule(schedule);
            book.setUpdate_time(new Date());
            bookShelfService.updateBookShelf(book);
            JsonResult result = JsonResult.getSuccess("进度修改成功");
            result.setData(book);
            return result;
        } else {
            return JsonResult.getError("找不到图书");
        }
    }

    /**
     * 添加阅读记录（同一本书只保留一条记录）-v2
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v2/api/bookShelf/updateMemberReadRecord", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updateMemberReadRecord_v2(HttpServletRequest request) {
        try {
            String member_id = request.getParameter("member_id");
            String book_id = request.getParameter("book_id");
            String org_id = request.getParameter("org_id");//图书的机构Id
            String last_chapter_id = request.getParameter("last_chapter_id");
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            String schedule = request.getParameter("schedule");
            if (StringUtils.isEmpty(schedule)){
                schedule = last_chapter_id + "|1.00";
            }
            String book_type = request.getParameter("book_type");
            if (StringUtils.isEmpty(book_type)) {
                book_type = "2";
            }
            Integer bk_type = Integer.parseInt(book_type);
            UnifyMember member = null;
            if (member_token != null) {
                member = unifyMemberService.findByToken(member_token, token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (StringUtils.isEmpty(org_id)) {
                return JsonResult.getError("请提供机构编号");
            }
            if (StringUtils.isEmpty(last_chapter_id)) {
                return JsonResult.getError("请提供章节ID");
            }
            BookRepo bookRepo = bookRepoService.findByBookId(Long.parseLong(book_id));
            if (bookRepo == null){
                return JsonResult.getError("找不到图书");
            }
            BookChapter bookChapter = bookChapterService.findById(Long.parseLong(last_chapter_id));
            if (bookChapter == null){
                return JsonResult.getError("找不到章节");
            }
            MemberReadRecord memberReadRecord = new MemberReadRecord(Long.valueOf(member_id), Long.valueOf(book_id), 2, Long.valueOf(last_chapter_id));
            memberReadRecord.setChapter_name(bookChapter.getName());
            memberReadRecord.setBook_cover(bookRepo.getBook_cover_small());
            memberReadRecord.setBook_name(bookRepo.getBook_name());
            memberReadRecord.setPlan(schedule);
            memberReadRecordService.updateRecord_v2(Long.valueOf(org_id), memberReadRecord, token_type);
            //添加阅读统计
            BookIndexRecord bookIndexRecord = new BookIndexRecord();
            bookIndexRecord.setOrg_id(Long.parseLong(org_id));
            bookIndexRecord.setRecord_type(5);
            bookIndexRecord.setMember_id(Long.parseLong(member_id));
            bookIndexRecord.setDevice_type_code(token_type);
            bookIndexRecord.setBook_id(Long.parseLong(book_id));
            bookIndexRecord.setBook_type(bk_type);
            bookIndexRecordService.addRecord(bookIndexRecord);
            ShelfBook book = bookShelfService.findShelfBookByUser(Long.parseLong(member_id), Long.parseLong(book_id));
            if (book != null) {
                book.setSchedule(schedule);
                book.setUpdate_time(new Date());
                bookShelfService.updateBookShelf(book);
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 查询阅读记录-v2
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v2/api/book/queryReadingRecord", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object queryReadingRecord_v2(HttpServletRequest request, PageConditionBean bean) {
        try {
            String member_id = request.getParameter("member_id");
            Sort sort = new Sort(Sort.Direction.DESC, "end_time");
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = null;
            if (member_token != null) {
                member = unifyMemberService.findByToken(member_token, token_type);
            }
            if (member == null) {
                return JsonResult.getExpire("用户失效！");
            }
            Object memberList = memberReadRecordService.queryList(sort, bean, Long.valueOf(member_id));
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(memberList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除所有阅读记录
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v2/api/book/deleteReadRecord", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object deleteReadRecord(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = null;
            if (member_token != null) {
                member = unifyMemberService.findByToken(member_token, token_type);
            }
            if (member == null) {
                return JsonResult.getExpire("用户失效！");
            }
            if (member.getMember_id() == null) {
                return JsonResult.getError("没有阅读记录！");
            }
            memberReadRecordService.deleteReadRecord_v2(member.getMember_id());
            return JsonResult.getSuccess(Constants.ACTION_DELETE);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }

    }


    /**
     * 通过索引查询图书内容
     * @param text
     * @param pageSize
     * @param pageNum
     * @param book_id
     * @return
     */
    @RequestMapping(value = "/api/book/searchIndexContent", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object searchIndexContent(String text, Integer pageSize, Integer pageNum, String book_id) {
        BookResult bookResult = new BookResult();
        String searchText = text.replaceAll(" ", "");
        if (StringUtils.isNotEmpty(searchText)) {
            //更新搜索记录
            text = "\"" + HanLP.convertToSimplifiedChinese(text) + "\"" + "\"" + HanLP.convertToTraditionalChinese(text) + "\"";
            bookResult = bookRepoService.searchDirectoryIndex(text, pageSize, pageNum, book_id);
        }
        return bookResult;
    }
}
