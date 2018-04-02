package com.cjsz.tech.api.ctrls;

import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.api.APIConstants;
import com.cjsz.tech.api.beans.*;
import com.cjsz.tech.api.service.BookShelfService;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.*;
import com.cjsz.tech.book.domain.*;
import com.cjsz.tech.book.service.*;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.count.domain.SearchCount;
import com.cjsz.tech.count.service.SearchCountService;
import com.cjsz.tech.dev.domain.AppVersionAndroid;
import com.cjsz.tech.dev.domain.AppVersionIos;
import com.cjsz.tech.dev.service.AppVersionAndroidService;
import com.cjsz.tech.dev.service.AppVersionIosService;
import com.cjsz.tech.meb.domain.MemberReadRecord;
import com.cjsz.tech.meb.service.MemberReadRecordService;
import com.cjsz.tech.member.beans.BalanceBean;
import com.cjsz.tech.member.beans.MemberInfoJSONBean;
import com.cjsz.tech.member.beans.UnifyMemeberConstants;
import com.cjsz.tech.member.domain.BookOrder;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.enums.ClientType;
import com.cjsz.tech.member.service.BookOrderService;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.domain.AdvCat;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.service.AdvCatService;
import com.cjsz.tech.system.service.AdvService;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.utils.HttpClientUtil;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.utils.PasswordUtil;
import com.github.pagehelper.StringUtil;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/9/6 0006.
 */
@Controller
public class ApiPublicController {

    @Autowired
    private BookCatService bookCatService;

    @Autowired
    private RecommendBooksService recommendBooksService;

    @Autowired
    private BookDiscountService bookDiscountService;

    @Autowired
    private BookOrgRelService bookOrgRelService;

    @Autowired
    private BookRepoService bookRepoService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private SearchCountService searchCountService;

    @Autowired
    private UnifyMemberService unifyMemberService;

    @Autowired
    private BookIndexRecordService bookIndexRecordService;

    @Autowired
    private BookReviewService bookReviewService;

    @Autowired
    private BookShelfService bookShelfService;

    @Autowired
    private AdvCatService advCatService;

    @Autowired
    private AdvService advService;

    @Autowired
    private BookChapterService bookChapterService;

    @Autowired
    private MemberReadRecordService memberReadRecordService;

    @Autowired
    private BookReviewPraiseService bookReviewPraiseService;

    @Autowired
    private BookOrderService bookOrderService;

    @Autowired
    private AppVersionAndroidService appVersionAndroidService;

    @Autowired
    private AppVersionIosService appVersionIosService;

    /**
     * 获取出版图书分类信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/bookCat/repoList", method = RequestMethod.POST)
    @ResponseBody
    public Object repoList(HttpServletRequest request) {
        try {
            String pId = request.getParameter("pid");
            if (StringUtils.isEmpty(pId)) {
                pId = "0";
            }
            String channel = request.getParameter("channel");
            if (StringUtils.isEmpty(channel)){
                channel = "2";
            }
            List<BookCat> catList = new ArrayList<>();
            if ("2".equals(channel)) {
                Long pid = Long.parseLong(pId);
                if (pid == 0) {
                    catList = bookCatService.getList();
                } else {
                    catList = bookCatService.getListByPid(pid);
                }
            }else {
                Map<String,Object> param = Maps.newHashMap();
                param.put("channel",channel);
                param.put("pid",pId);
                String result = null;
                try{
                    result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ,UnifyMemeberConstants.API_SECRET_DZ,UnifyMemeberConstants.Module.BOOK, UnifyMemeberConstants.Module.Action.GETCATS), param);
                    if (StringUtils.isEmpty(result)) {
                        return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                    }
                    BookCatJSONBean bean = JSONObject.parseObject(result,BookCatJSONBean.class);
                    if (bean.getCode() == 0){
                        catList = bean.getData();
                    } else {
                      return JsonResult.getError(bean.getMsg());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(catList);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 根据编号获取推荐分类书籍
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/recommendBooks/getList", method = RequestMethod.POST)
    @ResponseBody
    public Object getList(HttpServletRequest request) {
        try {
            String recommend_code = request.getParameter("recommend_code");
            if (StringUtils.isEmpty(recommend_code)) {
                return JsonResult.getError("请提供分类编码");
            }
            String pageNum = request.getParameter("pageNum");
            if (StringUtils.isEmpty(pageNum)) {
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isEmpty(pageSize)) {
                return JsonResult.getError(Constants.EXCEPTION);
            }
            PageConditionBean bean = new PageConditionBean();
            bean.setPageNum(Integer.parseInt(pageNum));
            bean.setPageSize(Integer.parseInt(pageSize));
            RecommendCat cat = recommendBooksService.selectByCode(Long.parseLong(recommend_code));
            if (cat == null) {
                return JsonResult.getError("分类不存在");
            }
            Object result = recommendBooksService.getRecommendBooks(cat.getRecommend_cat_id(), bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 根据多个编号获取推荐分类书籍
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/recommendBooks/getListByCodes", method = RequestMethod.POST)
    @ResponseBody
    public Object getListByCodes(HttpServletRequest request) {
        try {
            String codes = request.getParameter("recommend_codes");
            if (StringUtils.isEmpty(codes)) {
                return JsonResult.getError("请提供分类编码");
            }
            String limit = request.getParameter("limit");
            if (StringUtils.isEmpty(limit)){
                limit = "12";
            }
            List<BookRecommendListBean> result = recommendBooksService.getRecommendBooksByCodes(codes,Integer.parseInt(limit));
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取各频道限时免费分页列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/bookDiscount/getList", method = RequestMethod.POST)
    @ResponseBody
    public Object bookDiscountList(HttpServletRequest request) {
        try {
            String channel_type = request.getParameter("channel_type");
            if (StringUtils.isEmpty(channel_type)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String pageNum = request.getParameter("pageNum");
            if (StringUtils.isEmpty(pageNum)) {
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isEmpty(pageSize)) {
                return JsonResult.getError(Constants.EXCEPTION);
            }
            PageConditionBean bean = new PageConditionBean();
            bean.setPageNum(Integer.parseInt(pageNum));
            bean.setPageSize(Integer.parseInt(pageSize));
            Object result = bookDiscountService.getFreeList(Integer.parseInt(channel_type), bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取出版频道限时特价列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/bookDiscount/discountList", method = RequestMethod.POST)
    @ResponseBody
    public Object discountList(HttpServletRequest request) {
        try {
            String pageNum = request.getParameter("pageNum");
            if (StringUtils.isEmpty(pageNum)) {
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isEmpty(pageSize)) {
                return JsonResult.getError(Constants.EXCEPTION);
            }
            PageConditionBean bean = new PageConditionBean();
            bean.setPageNum(Integer.parseInt(pageNum));
            bean.setPageSize(Integer.parseInt(pageSize));
            Object result = bookDiscountService.getDiscountList(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * app各图书列表（书架搜索列表，书城分类详情列表）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/search/bookList", method = RequestMethod.POST)
    @ResponseBody
    public Object bookList(HttpServletRequest request) {
        try {
            String book_cat_id = request.getParameter("book_cat_id");
            if (StringUtils.isEmpty(book_cat_id)) {
                book_cat_id = "0";
            }
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isEmpty(pageSize)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String pageNum = request.getParameter("pageNum");
            if (StringUtils.isEmpty(pageNum)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String type = request.getParameter("type");
            if (StringUtils.isEmpty(type)) {
                type = "all";
            }
            String order = request.getParameter("order");
            if (StringUtils.isEmpty(order)) {
                order = "hot";
            }
            String book_cat_sonid = request.getParameter("book_cat_sonid");
            if (StringUtils.isEmpty(book_cat_sonid)){
                book_cat_sonid = "0";
            }
            String bookChannel = request.getParameter("bookChannel");
            if (StringUtils.isEmpty(bookChannel)){
                bookChannel = "All";
            }
            String isHubei = request.getParameter("isHubei");
            String searchText = request.getParameter("searchText");
            if (StringUtils.isEmpty(searchText)) {
                searchText = "";
            } else {
                Boolean flag = bookRepoService.containsEmoji(searchText);
                if (flag == true) {
                    return JsonResult.getOther("请勿输入非法字符");
                }
                //添加搜索记录
                SearchCount searchCount = searchCountService.selectByNameAndOrgId(searchText, 189L);
                if (searchCount == null) {
                    //搜索前未搜索过本次搜索内容
                    Organization org = organizationService.selectById(189L);
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
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            String book_type = request.getParameter("book_type");
            if (StringUtils.isEmpty(book_type)){
                book_type = "2";
            }
            if ("1".equals(book_type)){
                //网文
                Map<String,Object> param = Maps.newHashMap();
                String result = null;
                param.put("pageNum",pageNum);
                param.put("pageSize",pageSize);
                param.put("book_cat_id",book_cat_id);
                if (StringUtils.isNotEmpty(isHubei)){
                    param.put("isHubei",isHubei);
                }
                if (StringUtils.isNotEmpty(searchText)){
                    param.put("searchText",searchText);
                }
                param.put("bookChannel",bookChannel);
                param.put("order",order);
                param.put("book_cat_sonid",book_cat_sonid);
                param.put("type",type);
                try{
                    result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ,UnifyMemeberConstants.API_SECRET_DZ ,UnifyMemeberConstants.Module.BOOK, UnifyMemeberConstants.Module.Action.LISTS), param);
                    if (StringUtils.isEmpty(result)) {
                        return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                    }
                    BookListJSONBean bean = JSONObject.parseObject(result,BookListJSONBean.class);
                    if (bean.getCode() == 0){
                        jsonResult.setData(bean.getData());
                    }else {
                        return JsonResult.getError(bean.getMsg());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }


            }else if ("2".equals(book_type)) {
                //出版
                ApiPublicBookBean bean = new ApiPublicBookBean();
                bean.setPageNum(Integer.parseInt(pageNum));
                bean.setPageSize(Integer.parseInt(pageSize));
                bean.setOrder(order);
                bean.setType(type);
                bean.setSearchText(searchText);
                if (StringUtils.isNotEmpty(book_cat_id)) {
                    bean.setBook_cat_id(Long.parseLong(book_cat_id));
                }
                Object obj = bookOrgRelService.getAppBooks(bean);
                jsonResult.setData(obj);
            }

            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 第三方登录保存用户信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/member/login/save", method = RequestMethod.POST)
    @ResponseBody
    public Object saveLogin(HttpServletRequest request) {
        try {
            String member_id = request.getParameter("member_id");
            String token_type = request.getParameter("token_type");
            String token = request.getParameter("member_token");
            String account = request.getParameter("account");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String nick_name = request.getParameter("nick_name");
            String sex = request.getParameter("sex");
            UnifyMember mem = unifyMemberService.findByAccount(Long.parseLong(member_id), token_type);
            if (mem != null) {
                return JsonResult.getOther("会员信息已存在");
            }
            UnifyMember member = unifyMemberService.findByMemberId(Long.parseLong(member_id));
            if (member != null) {
                member.setToken(token);
                member.setToken_type(token_type);
                member.setId(null);
            } else {
                member = new UnifyMember();
                member.setNick_name(nick_name);
                member.setEmail(email);
                member.setPhone(phone);
                member.setAccount(account);
                member.setSex(Integer.parseInt(sex));
                member.setToken(token);
                member.setToken_type(token_type);
                member.setMember_id(Long.parseLong(member_id));
            }
            unifyMemberService.saveMember(member);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_ADD);
            jsonResult.setData(member);
            return jsonResult;
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
    @RequestMapping(value = "/v3/book/getBookDetailInfo", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getBookInfo(HttpServletRequest request) {
        try {
            String bookid = request.getParameter("book_id");
            String token = request.getParameter("member_token");
            if (StringUtils.isEmpty(token)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            String type = request.getParameter("book_type");
            if (StringUtils.isEmpty(type)){
                return JsonResult.getError("请提供图书类型");
            }
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            Integer book_type;
            PublicBookBean bookInfo = new PublicBookBean();
            CJZWWPublicBookBean bookBean = new CJZWWPublicBookBean();
            try{
                book_type = Integer.parseInt(type);
            }catch (Exception e){
                e.printStackTrace();
                return JsonResult.getError(Constants.EXCEPTION);
            }
            if (book_type == 2) {
                //出版图书
                //1.获取图书详情信息
                bookInfo = bookOrgRelService.findByMemberIdAndBookId(Long.valueOf(bookid), member.getMember_id());
                if (bookInfo == null) {
                    return JsonResult.getOther("图书不存在");
                }
                //2.记录用户点击记录
                BookIndexRecord bookIndexRecord = new BookIndexRecord(189L, Long.valueOf(bookid), 2, member.getMember_id(), RecordTypeEnum.CLICK.code(), new Date(), token_type);
                bookIndexRecordService.addRecord(bookIndexRecord);
                result.setData(bookInfo);
            }else if(book_type == 1){
                Map<String,Object> param = Maps.newHashMap();
                param.put("bid",bookid);
                String resultInfo = null;
                try{
                    resultInfo = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ,UnifyMemeberConstants.API_SECRET_DZ,UnifyMemeberConstants.Module.BOOK,UnifyMemeberConstants.Module.Action.INFO),param);
                    if (StringUtils.isEmpty(resultInfo)){
                        return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                    }
                    BookInfoJSONBean bean = JSONObject.parseObject(resultInfo,BookInfoJSONBean.class);
                    if (bean.getCode() == 0) {
                        bookBean = bean.getData();
                        ShelfBook book = bookShelfService.findBookInShelf(member.getMember_id(),Long.parseLong(bookid),book_type);
                        if (book != null){
                            bookBean.setShelf_id(book.getShelf_id());
                        }
                        //2.记录用户点击记录
                        BookIndexRecord bookIndexRecord = new BookIndexRecord(189L, Long.valueOf(bookid), 1, member.getMember_id(), RecordTypeEnum.CLICK.code(), new Date(), token_type);
                        bookIndexRecordService.addRecord(bookIndexRecord);
                        BookDiscount bookDiscount = bookDiscountService.selectByIdAndType(bookBean.getBook_id());
                        if (bookDiscount != null){
                            bookBean.setDiscount_price(bookDiscount.getDiscount_price());
                            bookBean.setEnd_time(bookDiscount.getEnd_time());
                        }
                        result.setData(bookBean);
                    }else{
                        return JsonResult.getError(bean.getMsg());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }

            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 图书评论列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/bookReview/list", method = {RequestMethod.POST})
    @ResponseBody
    public Object getBookReviewList(HttpServletRequest request, PageConditionBean bean) {
        String book_id = request.getParameter("book_id");
        if (StringUtil.isEmpty(book_id)) {
            return JsonResult.getError(APIConstants.MEMBER_SHELF_NOTHING_ERROR);
        } else {
            try {
                Long.parseLong(book_id);
            } catch (Exception e) {
                return JsonResult.getError("图书非法");
            }
        }
        String book_type = request.getParameter("book_type");
        if (StringUtils.isEmpty(book_type)) {
            return JsonResult.getError(Constants.EXCEPTION);
        }
        String token = request.getParameter("member_token");
        if (StringUtils.isEmpty(token)) {
            return JsonResult.getExpire(Constants.TOKEN_FAILED);
        }
        String token_type = request.getParameter("token_type");
        UnifyMember member = unifyMemberService.findByToken(token, token_type);
        if (member == null) {
            return JsonResult.getExpire(Constants.TOKEN_FAILED);
        }
        Sort sort = new Sort(Sort.Direction.DESC, "create_time");
        Object obj = bookReviewService.getReviewList(sort, bean, 189L, Long.valueOf(book_id), Integer.parseInt(book_type), member.getMember_id());
        JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
        result.setData(obj);
        return result;

    }


    /**
     * 个人书架-删除书-v2
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/bookShelf/delBook", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelDelete_v3(HttpServletRequest request) {
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
            String shelf_ids = request.getParameter("shelf_ids");
            bookShelfService.deleteShelfBooks_v3(shelf_ids);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }

    }

    /**
     * 个人书架-增加书-v3
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/bookShelf/addBook", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelfAdd_v3(HttpServletRequest request) {
        try {
            String book_id = request.getParameter("book_id");
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            String book_type = request.getParameter("book_type");
            if (StringUtils.isEmpty(book_type)) {
                return JsonResult.getError(Constants.EXCEPTION);
            }
            Integer bk_type = Integer.parseInt(book_type);
            CJZWWBooks bookInfo = new CJZWWBooks();
            BookRepo repo = new BookRepo();
            if ("2".equals(book_type)){
                repo = bookRepoService.findByBookId(Long.parseLong(book_id));
                if (repo == null){
                    return JsonResult.getError("找不到该图书");
                }
            }else if ("1".equals(book_type)){
                bookInfo = recommendBooksService.selectBook(Long.parseLong(book_id));
                if (bookInfo == null){
                    return JsonResult.getError("找不到该图书");
                }
            }
            UnifyMember member = null;
            if (member_token != null) {
                member = unifyMemberService.findByToken(member_token, token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            ShelfBook shelfBook = bookShelfService.findShelfBookByUser_v3(member.getMember_id(), Long.parseLong(book_id),Integer.parseInt(book_type));
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
                if (bk_type == 2) {
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
                }else if (bk_type == 1){
                    shelfBook.setBk_id(Long.parseLong(book_id));
                    shelfBook.setBk_name(bookInfo.getBook_name());
                    shelfBook.setBk_cover(bookInfo.getBook_cover());
                    shelfBook.setBk_cover_small(bookInfo.getBook_cover());
                    shelfBook.setBk_url("");
                    shelfBook.setBk_author(bookInfo.getBook_author());
                    shelfBook.setUser_id(member.getMember_id());
                    shelfBook.setUser_name(member.getNick_name());
                    shelfBook.setIs_delete(2);//是否删除（1：是  2：否）
                    shelfBook.setCreate_time(new Date());
                    shelfBook.setUpdate_time(new Date());
                    shelfBook.setSchedule("");
                    shelfBook.setBook_type(bk_type);
                }
                bookShelfService.saveShelfBook_v2(shelfBook);
            }
            //添加图书收藏统计
            BookIndexRecord bookIndexRecord = new BookIndexRecord();
            bookIndexRecord.setDevice_type_code(token_type);
            bookIndexRecord.setMember_id(member.getMember_id());
            bookIndexRecord.setRecord_type(2);
            bookIndexRecord.setOrg_id(189L);
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
     * 个人书架-书列表-v3
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/bookShelf/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object shelfList_v3(HttpServletRequest request) {
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
            List<ShelfBook> bookList = bookShelfService.findShelfBookList_v3(member.getMember_id());
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(bookList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 发布、回复评论
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/bookReview/addReview", method = {RequestMethod.POST})
    @ResponseBody
    public Object addReview(HttpServletRequest request) {
        String member_token = request.getParameter("member_token");
        String token_type = request.getParameter("token_type");
        if (StringUtil.isEmpty(member_token)) {
            return JsonResult.getExpire(Constants.TOKEN_FAILED);
        }
        UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
        if (member == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }

        String book_id = request.getParameter("book_id");
        String book_type = request.getParameter("book_type");
        if (StringUtil.isEmpty(book_id)) {
            return JsonResult.getError(APIConstants.MEMBER_SHELF_NOTHING_ERROR);
        }
        BookRepo book = new BookRepo();
        CJZWWBooks books = new CJZWWBooks();
        if ("2".equals(book_type)) {
            book = bookRepoService.findByBookId(Long.parseLong(book_id));
            if (book == null) {
                return JsonResult.getError(APIConstants.NO_BOOK);
            }
        }else if ("1".equals(book_type)){
            books = recommendBooksService.selectBook(Long.parseLong(book_id));
            if (books == null) {
                return JsonResult.getError(APIConstants.NO_BOOK);
            }
        }
        String review_content = request.getParameter("review_content");
        if (StringUtil.isEmpty(review_content)) {
            return JsonResult.getError(APIConstants.REVIEW_CONTENT_NOTHING_ERROR);
        }
        Boolean flag = bookRepoService.containsEmoji(review_content);
        if (flag == true) {
            return JsonResult.getOther("请勿输入非法字符");
        }

        if (StringUtils.isEmpty(book_type)) {
            return JsonResult.getError(Constants.EXCEPTION);
        }
        Integer bk_type = Integer.parseInt(book_type);

        BookReview review = new BookReview();
        review.setPid(0L);
        review.setMember_id(member.getMember_id());
        review.setBook_id(Long.parseLong(book_id));
        review.setReview_content(review_content);
        review.setOrg_id(189L);
        review.setCreate_time(new Date());
        review.setReview_nums(0L);
        review.setNick_name(member.getNick_name());
        review.setBook_type(bk_type);
        if ("2".equals(book_type)) {
            //2.记录用户评论记录
            BookIndexRecord bookIndexRecord = new BookIndexRecord(189L, Long.valueOf(book_id), 2, member.getMember_id(), RecordTypeEnum.COMMENT.code(), new Date(), token_type);
            bookIndexRecordService.addRecord(bookIndexRecord);
            review.setBook_name(book.getBook_name());
            review.setBook_cover_small(book.getBook_cover_small());
        }else if ("1".equals(book_type)){
            //2.记录用户评论记录
            BookIndexRecord bookIndexRecord = new BookIndexRecord(189L, Long.valueOf(book_id), 1, member.getMember_id(), RecordTypeEnum.COMMENT.code(), new Date(), token_type);
            bookIndexRecordService.addRecord(bookIndexRecord);
            review.setBook_name(books.getBook_name());
            review.setBook_cover_small(books.getBook_cover());
        }
        String pid = request.getParameter("pid");
        if (StringUtil.isEmpty(pid)) {
            bookReviewService.saveBookReview(review, token_type);
        } else {
            //评论回复
            BookReviewBean bookReview = bookReviewService.selectReviewById(Long.parseLong(pid));
            if (bookReview == null) {
                return JsonResult.getError(APIConstants.NO_BOOK_REVIEW);
            }
            review.setPid(Long.parseLong(pid));
            review.setFull_path(bookReview.getFull_path());
            review.setBook_type(2);
            bookReviewService.saveBookReviewResponse(review, token_type);
        }
        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.BOOK_REVIEW_ADD_SUCCESS);
        jsonResult.setData(review);
        return jsonResult;
    }

    /**
     * 热门关键词-v3
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/searchKey/getSearchkeyList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getSearchKeyList_v2(HttpServletRequest request) {
        try {
            String display = request.getParameter("display");
            if (StringUtils.isEmpty(display)) {
                display = "10";
            }
            List<SearchCount> searchKeyList = searchCountService.selectByOrgIdAndCount_v2(189L, Long.valueOf(display));
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(searchKeyList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * app广告
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/adv/getAdv", method = RequestMethod.POST)
    @ResponseBody
    public Object getAdv(HttpServletRequest request) {
        try {
            String adv_code = request.getParameter("adv_code");
            if (StringUtils.isEmpty(adv_code)){
                return JsonResult.getError("请输入广告编码");
            }
            AdvCat advCat = advCatService.selectByCatCode(adv_code);
            if (advCat == null) {
                return JsonResult.getError("广告不存在");
            }
            List<Adv> advList = advService.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), 189L);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(advList);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 出版图书排行列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/bookList/rankList", method = RequestMethod.POST)
    @ResponseBody
    public Object rankList(HttpServletRequest request) {
        try {
            String pageNum = request.getParameter("pageNum");
            if (StringUtils.isEmpty(pageNum)) {
                return JsonResult.getError("请提供分页参数");
            }
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isEmpty(pageSize)) {
                return JsonResult.getError("请提供分页参数");
            }
            PageConditionBean bean = new PageConditionBean();
            bean.setPageNum(Integer.parseInt(pageNum));
            bean.setPageSize(Integer.parseInt(pageSize));
            Object result = bookOrgRelService.findRankList(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 长江中文网章节列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/bookChapter/list",method = RequestMethod.POST)
    @ResponseBody
    public Object getCJZWWChapterList(HttpServletRequest request){
        try {
            String token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String book_id = request.getParameter("book_id");
            if (StringUtils.isEmpty(book_id)) {
                return JsonResult.getError("请提供图书");
            }
            CJZWWBooks book = recommendBooksService.selectBook(Long.parseLong(book_id));
            if (book == null){
                return JsonResult.getError("图书不存在");
            }
            Map<String, Object> param = Maps.newHashMap();
            param.put("token", token);
            param.put("bid", book_id);
            String result = null;
            ChapterJSONBean  list = new ChapterJSONBean();
            try {
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getInvokeApiUrl(UnifyMemeberConstants.Module.BOOK, UnifyMemeberConstants.Module.Action.CHAPTERLIST), param);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
                BookChapterListBean bean = JSONObject.parseObject(result,BookChapterListBean.class);
                if (bean.getCode() == 0){
                    list = bean.getData();
                }else {
                    return JsonResult.getError(bean.getMsg());
                }
            }catch (Exception e){
                e.printStackTrace();
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
            Map<String,Object> resultInfo = new HashMap<>();
            resultInfo.put("chapters",list);
            resultInfo.put("info",book);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(resultInfo);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 长江中文网章节内容
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/chapter/content",method = RequestMethod.POST)
    @ResponseBody
    public Object ChapterContent(HttpServletRequest request){
        try{
            String token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String book_id = request.getParameter("book_id");
            if (StringUtils.isEmpty(book_id)) {
                return JsonResult.getError("请提供图书id");
            }
            String ch_id = request.getParameter("ch_id");
            if (StringUtils.isEmpty(ch_id)){
                return JsonResult.getError("请提供章节id");
            }
            String m_key = "";
            //查询该图书是否在限免中
            BookDiscount book = bookDiscountService.selectByIdAndType(Long.parseLong(book_id));
            if (book != null){
                m_key = PasswordUtil.bookMD5Key(book_id);
            }
            Map<String,Object> param = Maps.newHashMap();
            param.put("bid",book_id);
            param.put("cid",ch_id);
            param.put("token",token);
            if (m_key != ""){
                param.put("m_key",m_key);
            }
            String result = null;
            ChapterContentBean resultInfo = new ChapterContentBean();
            try{
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getInvokeApiUrl(UnifyMemeberConstants.Module.BOOK, UnifyMemeberConstants.Module.Action.CHAPTER), param);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
                ChapterContentJSONBean bean = JSONObject.parseObject(result,ChapterContentJSONBean.class);
                if (bean.getCode() == 0){
                    resultInfo = bean.getData();
                }else{
                    return JsonResult.getError(bean.getMsg());
                }
            }catch (Exception e){
                e.printStackTrace();
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
            if (resultInfo != null) {
                resultInfo.setCh_content(URLDecoder.decode(resultInfo.getCh_content(), "UTF-8"));
                resultInfo.setCh_name(URLDecoder.decode(resultInfo.getCh_name(), "UTF-8"));
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(resultInfo);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 章节购买查询价格
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/payChapter/getPrice",method = RequestMethod.POST)
    @ResponseBody
    public Object getPrice(HttpServletRequest request){
        try{
            String token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String book_id = request.getParameter("book_id");
            if (StringUtils.isEmpty(book_id)) {
                return JsonResult.getError("请提供图书id");
            }
            String ch_id = request.getParameter("ch_id");
            if (StringUtils.isEmpty(ch_id)){
                return JsonResult.getError("请提供章节id");
            }
            String count = request.getParameter("count");
            if (StringUtils.isEmpty(count)){
                return JsonResult.getError("请提供购买数量");
            }
            Map<String,Object> param = Maps.newHashMap();
            param.put("bid",book_id);
            param.put("startid",ch_id);
            param.put("token",token);
            param.put("count",count);
            String result = null;
            PayChapterBean resultInfo = new PayChapterBean();
            try{
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getInvokeApiUrl(UnifyMemeberConstants.Module.BOOK, UnifyMemeberConstants.Module.Action.GETCHAPTERSBUYINFO), param);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
                PayChapterJSONBean bean = JSONObject.parseObject(result,PayChapterJSONBean.class);
                if (bean.getCode() == 0){
                    resultInfo = bean.getData();
                }else{
                    return JsonResult.getError(bean.getMsg());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Long member_balance;
            String resultBalance = null;
            Map<String,Object> params = Maps.newHashMap();
            params.put("token",token);
            try {
                resultBalance = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.USER_ACCOUNT), params);
                if (StringUtils.isEmpty(resultBalance)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
                MemberInfoJSONBean bean = JSONObject.parseObject(resultBalance, MemberInfoJSONBean.class);
                if (bean.getCode() == 0) {
                    BalanceBean balanceBean = bean.getData();
                    member_balance = Long.parseLong(balanceBean.getBalance());
                } else {
                    return JsonResult.getError(bean.getMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
            resultInfo.setBalance(member_balance);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(resultInfo);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 获取图书章节内容
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/v3/api/book/getChapterContent", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getChapterContent(HttpServletRequest request) {
        String bkidstr = request.getParameter("book_id");
        if (StringUtils.isEmpty(bkidstr)) {
            return JsonResult.getError("请提供书的编码");
        }
        String member_token = request.getParameter("member_token");
        String token_type = request.getParameter("token_type");
        if (StringUtil.isEmpty(member_token) || StringUtil.isEmpty(token_type)) {
            return JsonResult.getExpire(Constants.TOKEN_FAILED);
        }
        UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
        if (member == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        Long bkid = 0L;
        try {
            bkid = Long.parseLong(bkidstr);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getError("图书编码非法");
        }
        String chapteridstr = request.getParameter("chapter_id");
        if (StringUtils.isEmpty(chapteridstr)) {
            return JsonResult.getError("请提供章节信息");
        }
        Long chapterid = 0L;
        try {
            chapterid = Long.parseLong(chapteridstr);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getError("图书章节信息非法");
        }

        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            basePath = basePath + ":" + request.getServerPort();
        }
        basePath += path;
        BookRepo bookObj = bookRepoService.findByBookId(bkid);
        if (bookObj == null) {
            return JsonResult.getError("该图书不存在");
        }
        BookChapter chapter = bookChapterService.findMyChapterById(bkid, chapterid);
        if (chapter == null) {
            return JsonResult.getError("章节信息不存在");
        }
        //查询是否购买图书
        Boolean isBuy = bookOrderService.isBuyById(member.getMember_id(),bkid);
        if(!isBuy) {
            //查询图书是否限免
            BookDiscount book = bookDiscountService.selectBookIsFree(bkid);
            if (book == null) {
                List<Long> chapterList = bookChapterService.findChapterIdsByBKId(bkid);
                if ((chapterList.size())/3 >= 1) {
                    //章节数量大于5章免费3个章节
                    Integer index = chapterList.indexOf(chapter.getId()) + 1;
                    if (index > (chapterList.size())/3) {
                        return JsonResult.getError("请购买图书");
                    }
                } else if (chapterList.size() == 2) {
                    //章节数量小于三个章节免费一个章节
                    Integer index = chapterList.indexOf(chapter.getId()) + 1;
                    if (index > 1) {
                        return JsonResult.getError("请购买图书");
                    }
                }
            }
        }
        String content = loadEpubChapter(bookObj, chapter, basePath);
        Hashtable result = new Hashtable();
        result.put("content", content);
        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.BOOK_CHAPTER_SUCCESS);
        jsonResult.setData(result);
        return jsonResult;
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
            String un_book_path = book.getBook_url().replace(book.getFile_name(), "") + "unfile/" + book.getFile_name().replace(".epub", "");
            String chapter_path = file.getPath() + un_book_path + "/" + chapter.getPurl() + chapter.getUrl();
            File un_book_file = new File(chapter_path);
            if (!un_book_file.exists()) {
                return "无内容";
            }
            String content = FileUtils.readFileToString(un_book_file);
            String cssPath = webpath + un_book_path + "/" + chapter.getPurl() + chapter.getUrl().replace(un_book_file.getName(), "");
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
            StringBuffer sb = new StringBuffer();
            boolean result = matcher.find();
            matcher.appendReplacement(sb, "<html>");
            matcher.appendTail(sb);

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "无内容";
    }


    /**
     * 点赞
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/bookReviewPraise/addReviewPraise", method = {RequestMethod.POST})
    @ResponseBody
    public Object addReviewPraise(HttpServletRequest request) {
        String member_token = request.getParameter("member_token");
        String token_type = request.getParameter("token_type");
        if (StringUtil.isEmpty(member_token) || StringUtil.isEmpty(token_type)) {
            return JsonResult.getExpire(Constants.TOKEN_FAILED);
        }
        UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
        if (member == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        String review_id = request.getParameter("review_id");
        if (StringUtil.isEmpty(review_id)) {
            return JsonResult.getError(APIConstants.NO_BOOK_REVIEW);
        }
        BookReview review = bookReviewService.selectById(Long.parseLong(review_id));
        if (review == null) {
            return JsonResult.getError(APIConstants.NO_BOOK_REVIEW);
        }
        BookReviewPraise praise = bookReviewPraiseService.getPraiseById(Long.parseLong(review_id), member.getMember_id());
        if (praise == null) {
            bookReviewPraiseService.savePraise(Long.parseLong(review_id), member.getMember_id(), review.getPraise_count());
        } else {
            bookReviewPraiseService.updatePraise(praise, review.getPraise_count());
        }
        return JsonResult.getSuccess(Constants.ACTION_UPDATE);
    }

    /**
     * 章节购买接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/payChapter/buyChapter",method = RequestMethod.POST)
    @ResponseBody
    public Object buyChapter(HttpServletRequest request){
        try{
            String token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String book_id = request.getParameter("book_id");
            if (StringUtils.isEmpty(book_id)) {
                return JsonResult.getError("请提供图书id");
            }
            String ch_id = request.getParameter("ch_id");
            if (StringUtils.isEmpty(ch_id)){
                return JsonResult.getError("请提供章节id");
            }
            String count = request.getParameter("count");
            if (StringUtils.isEmpty(count)){
                return JsonResult.getError("请提供购买数量");
            }
            Map<String,Object> param = Maps.newHashMap();
            param.put("bid",book_id);
            param.put("startid",ch_id);
            param.put("token",token);
            param.put("count",count);
            BuyChapterJSONBean.BuyChapter resultInfo = null;
            try{
                String result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ,UnifyMemeberConstants.API_SECRET_DZ,UnifyMemeberConstants.Module.BOOK, UnifyMemeberConstants.Module.Action.BUYCHAPTERS), param);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
                BuyChapterJSONBean bean = JSONObject.parseObject(result,BuyChapterJSONBean.class);
                if (bean.getCode() == 0){
                    resultInfo = bean.getData();
                }else{
                    return JsonResult.getError(bean.getMsg());
                }
            }catch (Exception e){
                e.printStackTrace();
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
            JsonResult jsonResult = JsonResult.getSuccess("购买成功！");
            jsonResult.setData(resultInfo);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 查询阅读记录
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/v3/memberRead/updateReadRecord", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object queryReadingRecord_v2(HttpServletRequest request, PageConditionBean bean) {
        try {
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
            Object memberList = memberReadRecordService.getMemberReadList(sort, bean, member.getMember_id());
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(memberList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 更新阅读记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/bookShelf/updateMemberReadRecord", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updateMemberReadRecord_v2(HttpServletRequest request) {
        try {
            String book_id = request.getParameter("book_id");
            String last_chapter_id = request.getParameter("last_chapter_id");
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            String schedule = request.getParameter("schedule");
            if(StringUtils.isEmpty(schedule)){
                schedule = last_chapter_id + "|1.00";
            }
            String book_type = request.getParameter("book_type");
            if (StringUtils.isEmpty(book_type)){
                return JsonResult.getError("请提供图书类型");
            }
            String book_name = request.getParameter("book_name");
            if (StringUtils.isEmpty(book_name)){
                return JsonResult.getError("请提供书名");
            }
            String book_cover = request.getParameter("book_cover");
            if (StringUtil.isEmpty(book_cover)){
                return JsonResult.getError("请提供封面");
            }
            String chapter_name = request.getParameter("chapter_name");
            if (StringUtils.isEmpty(chapter_name)){
                return JsonResult.getError("请提供章节名");
            }
            if (StringUtils.isEmpty(book_type)) {
                return JsonResult.getError("请提供图书类型");
            }
            Integer bk_type = Integer.parseInt(book_type);
            UnifyMember member = null;
            if (member_token != null) {
                member = unifyMemberService.findByToken(member_token, token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (StringUtils.isEmpty(last_chapter_id)) {
                return JsonResult.getError("请提供章节ID");
            }
            MemberReadRecord memberReadRecord = new MemberReadRecord(member.getMember_id(), Long.valueOf(book_id), bk_type, Long.valueOf(last_chapter_id));
            memberReadRecord.setBook_cover(book_cover);
            memberReadRecord.setBook_name(book_name);
            memberReadRecord.setChapter_name(chapter_name);
            memberReadRecord.setPlan(schedule);
            memberReadRecordService.updateRecord_v2(189L, memberReadRecord, token_type);
            //添加阅读统计
            BookIndexRecord bookIndexRecord = new BookIndexRecord();
            bookIndexRecord.setOrg_id(189L);
            bookIndexRecord.setRecord_type(5);
            bookIndexRecord.setMember_id(member.getMember_id());
            bookIndexRecord.setDevice_type_code(token_type);
            bookIndexRecord.setBook_id(Long.parseLong(book_id));
            bookIndexRecord.setBook_type(bk_type);
            bookIndexRecordService.addRecord(bookIndexRecord);
            ShelfBook book = bookShelfService.findShelfBookByUser(member.getMember_id(), Long.parseLong(book_id));
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
     * 网文相关推荐
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/recommend/list",method = RequestMethod.POST)
    @ResponseBody
    public Object getCJZWWRecommend(HttpServletRequest request){
        try {
            String token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String book_id = request.getParameter("book_id");
            if (StringUtils.isEmpty(book_id)) {
                return JsonResult.getError("请提供图书");
            }
            CJZWWBooks book = recommendBooksService.selectBook(Long.parseLong(book_id));
            if (book == null){
                return JsonResult.getError("图书不存在");
            }
            String limit = request.getParameter("limit");
            if (StringUtils.isEmpty(limit)){
                limit = "8";
            }
            Map<String, Object> param = Maps.newHashMap();
            param.put("limit", limit);
            param.put("book_id", book_id);
            String result = null;
            List<CJZWWPublicBookBean>  list = new ArrayList<>();
            try {
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ,UnifyMemeberConstants.API_SECRET_DZ,UnifyMemeberConstants.Module.BOOK, UnifyMemeberConstants.Module.Action.RECOMMEND), param);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
                RecommendBookJSONBean bean = JSONObject.parseObject(result,RecommendBookJSONBean.class);
                if (bean.getCode() == 0){
                    list = bean.getData().getRows();
                }else {
                    return JsonResult.getError(bean.getMsg());
                }
            }catch (Exception e){
                e.printStackTrace();
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(list);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 长江币增减
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/payAmount/addCut",method = RequestMethod.POST)
    @ResponseBody
    public Object addCut(HttpServletRequest request){
        try{
            String token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String amount = request.getParameter("amount");
            if (StringUtils.isEmpty(amount)){
                return JsonResult.getError("请输入金额");
            }
            Map<String,Object> param = Maps.newHashMap();
            param.put("amount",amount);
            param.put("token",token);
            param.put("action","add");
            String result = null;
            JSONObject resultInfo = new JSONObject();
            try {
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ,UnifyMemeberConstants.API_SECRET_DZ,UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.CHANGEACCOUNT), param);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
                resultInfo = JSONObject.parseObject(result);
            }catch (Exception e){
                e.printStackTrace();
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(resultInfo.get("data"));
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 购买出版图书
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/bookOrder/saveOrder" , method = RequestMethod.POST)
    @ResponseBody
    public Object saveOrder(HttpServletRequest request){
        try{
            String token = request.getParameter("member_token");
            if (StringUtils.isEmpty(token)){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            if (StringUtils.isEmpty(token_type)){
                return JsonResult.getExpire(Constants.EXCEPTION);
            }
            UnifyMember member = unifyMemberService.findByToken(token,token_type);
            if (member == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String book_id = request.getParameter("book_id");
            if (StringUtils.isEmpty(book_id)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            PublicBookBean book = bookDiscountService.selectBookPrice(Long.parseLong(book_id));
            if (book == null){
                return JsonResult.getError("找不到该图书");
            }
            Boolean isBuy = bookOrderService.isBuyById(member.getMember_id(), Long.parseLong(book_id));
            if (isBuy) {
                return JsonResult.getError("已购买此图书");
            }
            Double price = 0.0;
            JsonResult jsonResult = JsonResult.getSuccess("购买成功！");
            BookOrder bookOrder = new BookOrder();
            if (book.getPrice() != null && book.getPrice() != 0.0) {
                if (book.getDiscount_price() == null) {
                    price = book.getPrice();
                } else {
                    price = book.getDiscount_price();
                }
                JSONObject resultInfo = new JSONObject();
                String amount = String.valueOf(price * 100);
                amount = StringUtils.substringBefore(amount,"." );

                Map<String, Object> param = Maps.newHashMap();
                param.put("token", token);
                param.put("amount", amount);
                param.put("action", "cut");
                String result = null;

                try {
                    result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.CHANGEACCOUNT), param);
                    if (StringUtils.isEmpty(result)) {
                        return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                    }

                    resultInfo = JSONObject.parseObject(result);
                    if ("0".equals( resultInfo.get("code").toString())) {
                        bookOrder.setBook_id(Long.parseLong(book_id));
                        bookOrder.setMember_id(member.getMember_id());
                        bookOrder.setPay_cost(Long.parseLong(amount));
                        bookOrderService.saveOrder(bookOrder);
                        jsonResult.setData(resultInfo.get("data"));
                    }else {
                        return JsonResult.getError(resultInfo.get("msg").toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                bookOrder.setBook_id(Long.parseLong(book_id));
                bookOrder.setMember_id(member.getMember_id());
                bookOrder.setPay_cost(0L);
                bookOrderService.saveOrder(bookOrder);
            }
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 会员书评列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/member/memberReviewList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object bookReview(HttpServletRequest request) {
        try {
            String token = request.getParameter("member_token");
            if (token == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            Integer pageNum = Integer.parseInt(request.getParameter("pageNum"));
            Integer pageSize = Integer.parseInt(request.getParameter("pageSize"));
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            Object object = bookReviewService.getMemberReview(member.getMember_id(), pageNum, pageSize, sort);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(object);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 红书、星级。。。。
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/bookList/randRank", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object randRankList(HttpServletRequest request) {
        try {
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            String bookChannel = request.getParameter("bookChannel");
            if (StringUtils.isEmpty(bookChannel)){
                return JsonResult.getError("请选择频道");
            }
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isEmpty(pageSize)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String pageNum = request.getParameter("pageNum");
            if (StringUtils.isEmpty(pageNum)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String type = request.getParameter("type");
            if (StringUtils.isEmpty(type)) {
                return JsonResult.getError("请选择类型");
            }
            Map<String,Object> param = Maps.newHashMap();
            param.put("bookChannel",bookChannel);
            param.put("pageNum",pageNum);
            param.put("pageSize",pageSize);
            param.put("type",type);
            String resultInfo = null;
            try{
                resultInfo = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ,UnifyMemeberConstants.API_SECRET_DZ,UnifyMemeberConstants.Module.BOOK,UnifyMemeberConstants.Module.Action.HANDRANK),param);
                if (StringUtils.isEmpty(resultInfo)){
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
                BookListJSONBean bean = JSONObject.parseObject(resultInfo,BookListJSONBean.class);
                if (bean.getCode() == 0) {

                    result.setData(bean.getData());
                }else{
                    return JsonResult.getError(bean.getMsg());
                }
            }catch (Exception e){
                e.printStackTrace();
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 章节数量
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/bookChapter/chapterCount",method = RequestMethod.POST)
    @ResponseBody
    public Object chapterCount(HttpServletRequest request){
        try {
            String book_id = request.getParameter("book_id");
            if (StringUtils.isEmpty(book_id)) {
                return JsonResult.getError("请提供图书id");
            }
            String book_type = request.getParameter("book_type");
            if (StringUtils.isEmpty(book_type)) {
                return JsonResult.getError("请提供图书类型");
            }
            Integer count = 0;
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            if ("2".equals(book_type)) {
                BookRepo book = bookRepoService.findByBookId(Long.parseLong(book_id));
                if (book == null) {
                    return JsonResult.getError("图书不存在");
                }
                count = bookChapterService.selectCountChapter(Long.parseLong(book_id));
                jsonResult.setData(count);

            } else if ("1".equals(book_type)) {
                Map<String,Object> param = Maps.newHashMap();
                String result = null;
                param.put("book_id",book_id);
                try{
                    result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ,UnifyMemeberConstants.API_SECRET_DZ ,UnifyMemeberConstants.Module.BOOK, UnifyMemeberConstants.Module.Action.CHAPTERCOUNT), param);
                    if (StringUtils.isEmpty(result)) {
                        return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                    }
                    JSONObject bean = JSONObject.parseObject(result);
                    if ("0".equals(bean.get("code").toString())){
                        jsonResult.setData(JSONObject.parseObject(bean.get("data").toString()).get("chapter_count"));
                    }else {
                        return JsonResult.getError(bean.get("msg").toString());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }

            }
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 小说榜单
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/CJZWWBookList/channelRankList", method = RequestMethod.POST)
    @ResponseBody
    public Object channelRankList(HttpServletRequest request) {
        try {
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isEmpty(pageSize)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String pageNum = request.getParameter("pageNum");
            if (StringUtils.isEmpty(pageNum)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String type = request.getParameter("type");
            if (StringUtils.isEmpty(type)) {
               return JsonResult.getError("请选择榜单类型");
            }
            if (!"popularity".equals(type) && !"sale".equals(type) && !"collection".equals(type)){
                return JsonResult.getError("拼错了！");
            }
            String bookChannel = request.getParameter("bookChannel");
            if (StringUtils.isEmpty(bookChannel)){
                return JsonResult.getError("请选择频道");
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            Map<String,Object> param = Maps.newHashMap();
            String result = null;
            param.put("pageNum",pageNum);
            param.put("pageSize",pageSize);
            param.put("bookChannel",bookChannel);
            param.put("type",type);
            try{
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ,UnifyMemeberConstants.API_SECRET_DZ ,UnifyMemeberConstants.Module.BOOK, UnifyMemeberConstants.Module.Action.AUTORANK), param);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
                BookListJSONBean bean = JSONObject.parseObject(result,BookListJSONBean.class);
                if (bean.getCode() == 0){
                    jsonResult.setData(bean.getData());
                }else {
                    return JsonResult.getError(bean.getMsg());
                }
            }catch (Exception e){
                e.printStackTrace();
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 出版章节列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/book/chapterTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getChapterTree(HttpServletRequest request) {
        String bkidstr = request.getParameter("book_id");
        String token = request.getParameter("member_token");
        if (token == null) {
            return JsonResult.getExpire(Constants.TOKEN_FAILED);
        }
        String token_type = request.getParameter("token_type");
        UnifyMember member = unifyMemberService.findByToken(token, token_type);
        if (member == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        if (StringUtils.isEmpty(bkidstr)) {
            return JsonResult.getError("请提供书的编码");
        }
        Long bkid = 0L;
        try {
            bkid = Long.parseLong(bkidstr);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getError("图书编码非法");
        }
        String schedule = null;
        BookBaseInfo bookObj = bookRepoService.findBookPrice(bkid);
        if (bookObj == null) {
            return JsonResult.getError("该图书不存在");
        }
        schedule = bookRepoService.getSchedule(bkid, member.getMember_id());
        if (schedule == null) {
            schedule = "0|0";
            bookObj.setSchedule(schedule);
        } else {
            bookObj.setSchedule(schedule);
        }
        List<BookChapter> chapterList = bookChapterService.getTreeEpubChapters(bkidstr);

        //查询余额
        Map<String, Object> params = Maps.newHashMap();
        params.put("token", token);
        Long member_balance;
        String result = null;
        try {
            result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.USER_ACCOUNT), params);
            if (StringUtils.isEmpty(result)) {
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
            MemberInfoJSONBean resultInfo = JSONObject.parseObject(result, MemberInfoJSONBean.class);
            if (resultInfo.getCode() == 0) {
                BalanceBean bean = resultInfo.getData();
                member_balance = Long.parseLong(bean.getBalance());
            } else {
                return JsonResult.getError(resultInfo.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
        }
        bookObj.setBalance(member_balance);
        Hashtable resultInfo = new Hashtable();
        resultInfo.put("info", bookObj);
        resultInfo.put("chapters", chapterList);
        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.BOOK_CHAPTER_SUCCESS);
        jsonResult.setData(resultInfo);
        return jsonResult;
    }

    /**
     * 记录用户分享记录统计
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/bookShare/addBookIndexRecord",method = RequestMethod.POST)
    @ResponseBody
    public Object addBookIndexRecord(HttpServletRequest request){
        try{
            String token = request.getParameter("member_token");
            if (token == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            String book_id = request.getParameter("book_id");
            if (StringUtils.isEmpty(book_id)){
                return JsonResult.getError("请提供图书Id");
            }
            String book_type = request.getParameter("book_type");
            if (StringUtils.isEmpty(book_type)){
                return JsonResult.getError("请选择图书类型");
            }
            BookIndexRecord bookIndexRecord = new BookIndexRecord(189L, Long.valueOf(book_id), Integer.parseInt(book_type), member.getMember_id(), RecordTypeEnum.SHARE.code(), new Date(), token_type);
            bookIndexRecordService.addRecord(bookIndexRecord);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bookIndexRecord);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 第三方请求
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/otherMember/getJSONString",method = RequestMethod.POST)
    @ResponseBody
    public Object getJSONString(HttpServletRequest request){
        try{
            String url = request.getParameter("otherURL");
            String result = HttpClientUtil.httpPostRequest(url);
            JsonResult jsonResult = JsonResult.getSuccess("请求成功");
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 更改会员基本信息
     *
     * @param request
     * @param nickName
     * @param sex
     * @return
     */
    @RequestMapping(value = "/v3/member/updateMemberInfo", method = {RequestMethod.POST})
    @ResponseBody
    public Object modifyUserInfo(HttpServletRequest request, @RequestParam String member_token, @RequestParam String token_type, String sign, String nickName, Integer sex, String client_type) {
        if (StringUtils.isNotEmpty(nickName)) {
            Boolean flag = bookRepoService.containsEmoji(nickName);
            if (flag == true) {
                return JsonResult.getOther("请勿输入非法字符");
            }
        }
        //判断app客户端类型
        if (StringUtils.isEmpty(client_type)) {
            client_type = ClientType.ORG_VERSION.code();
        }
        JsonResult jsonResult = (JsonResult) unifyMemberService.modifyUserInfo(member_token, nickName, sign, sex, token_type, client_type);
        //判断是否登录成功
        if (jsonResult.code == 0) {
            HttpSession session = request.getSession(true);
            session.setAttribute("member_info", jsonResult.data);
        }
        return jsonResult;
    }


    /**
     * 删除用户评论
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/bookReview/deleteReviews", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object deleteReviews(HttpServletRequest request) {
        try {
            String review_ids = request.getParameter("review_ids");
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = null;
            if (StringUtils.isNotEmpty(member_token)) {
                member = unifyMemberService.findByToken(member_token, token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (StringUtils.isEmpty(review_ids)) {
                return JsonResult.getError(Constants.EXCEPTION);
            }
            review_ids = review_ids.replaceAll("，",",");
            String[] ids = review_ids.split(",");
            Integer count = bookReviewService.selectCountMyReviewsById(member.getMember_id(), review_ids);
            if (ids.length != count.intValue()) {
                return JsonResult.getError("不能删除其他用户的评论！");
            }
            bookReviewService.deleteByReviews(review_ids);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取热搜词
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/searchKey/getSearchkeyList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getSearchKeyList_v3(HttpServletRequest request) {
        try {
            String display = request.getParameter("display");
            if (StringUtils.isEmpty(display)) {
                display = "10";
            }
            List<SearchCount> searchKeyList = searchCountService.selectByOrgIdAndCount_v2(189L, Long.valueOf(display));
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(searchKeyList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 查询出版图书购买价格及余额
      * @param request
     * @return
     */
    @RequestMapping(value = "/v3/payBook/getPrice",method = RequestMethod.POST)
    @ResponseBody
    public Object getPriceAndBalance(HttpServletRequest request){
        try{
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = null;
            if (StringUtils.isNotEmpty(member_token)) {
                member = unifyMemberService.findByToken(member_token, token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            String book_id = request.getParameter("book_id");
            if (StringUtils.isEmpty(book_id)){
                return JsonResult.getError("请提供图书id");
            }
            //查询余额
            Map<String, Object> params = Maps.newHashMap();
            params.put("token", member_token);
            Long member_balance;
            String result = null;
            try {
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.USER_ACCOUNT), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
                MemberInfoJSONBean resultInfo = JSONObject.parseObject(result, MemberInfoJSONBean.class);
                if (resultInfo.getCode() == 0) {
                    BalanceBean bean = resultInfo.getData();
                    member_balance = Long.parseLong(bean.getBalance());
                } else {
                    return JsonResult.getError(resultInfo.getMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
            //查询价格
            BookBaseInfo bookObj = bookRepoService.findBookPrice(Long.parseLong(book_id));
            if (bookObj == null) {
                return JsonResult.getError("该图书不存在");
            }
            Map<String,Object> resultInfo = new HashMap<>();
            resultInfo.put("balance",member_balance);
            resultInfo.put("cost",bookObj.getPrice());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(resultInfo);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * app版本更新
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/appVersion/getAppVersion", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getAppVersion(HttpServletRequest request) {
        try {
            String device_type = request.getParameter("device_type");
            if (StringUtils.isEmpty(device_type)){
                return JsonResult.getError("请提供终端类型");
            }
            String app_type = request.getParameter("app_type");
            if (StringUtils.isEmpty(app_type)) {
                return JsonResult.getError("请选择app类型");
            }
            if ("android".equals(device_type)) {
                AppVersionAndroid appVersion = appVersionAndroidService.selectEnabled(Integer.parseInt(app_type));
                if (appVersion == null) {
                    return JsonResult.getError("版本未发布！");
                }
                JsonResult jsonResult = JsonResult.getSuccess(APIConstants.APP_VERSION_REVICED);
                jsonResult.setData(appVersion);
                return jsonResult;
            } else if ("ios".equals(device_type)) {
                AppVersionIos appVersion = appVersionIosService.selectEnabled(Integer.parseInt(app_type));
                if (appVersion == null) {
                    return JsonResult.getError("版本未发布！");
                }
                JsonResult jsonResult = JsonResult.getSuccess(APIConstants.APP_VERSION_REVICED);
                jsonResult.setData(appVersion);
                return jsonResult;
            }
            return JsonResult.getException("错误设备类型");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 打赏
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/book/reward",method = RequestMethod.POST)
    @ResponseBody
    public Object bookReward(HttpServletRequest request){
        try {
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            if (StringUtils.isEmpty(member_token) || StringUtils.isEmpty(token_type)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            String book_id = request.getParameter("book_id");
            CJZWWBooks bookInfo = recommendBooksService.selectBook(Long.parseLong(book_id));
            if (bookInfo == null){
                return JsonResult.getError("图书错误");
            }
            //打赏金额
            String amount = request.getParameter("amount");
            Map<String,Object> param = Maps.newHashMap();
            param.put("token",member_token);
            param.put("amount",amount);
            param.put("bid",book_id);
            String result = null;
            try {
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.BOOK, UnifyMemeberConstants.Module.Action.DOLAREE), param);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
                BookRewardBean bean = JSONObject.parseObject(result,BookRewardBean.class);
                if (bean.getCode() == 0){
                    JsonResult jsonResult = JsonResult.getSuccess(bean.getMsg());
                    return jsonResult;
                }else {
                    return JsonResult.getError(bean.getMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
