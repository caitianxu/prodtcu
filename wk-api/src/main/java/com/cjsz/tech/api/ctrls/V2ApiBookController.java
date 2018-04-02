package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.APIConstants;
import com.cjsz.tech.api.service.BookShelfService;
import com.cjsz.tech.book.beans.BookMarkBean;
import com.cjsz.tech.book.beans.FindBookBean;
import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.book.domain.BookMark;
import com.cjsz.tech.book.service.*;
import com.cjsz.tech.count.domain.SearchCount;
import com.cjsz.tech.count.service.SearchCountService;
import com.cjsz.tech.dev.domain.AppVersionAndroid;
import com.cjsz.tech.dev.domain.AppVersionIos;
import com.cjsz.tech.dev.domain.DeviceFeedback;
import com.cjsz.tech.dev.service.AppVersionAndroidService;
import com.cjsz.tech.dev.service.AppVersionIosService;
import com.cjsz.tech.dev.service.DeviceFeedbackService;
import com.cjsz.tech.meb.service.MemberReadRecordService;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@Controller
@RequestMapping("/v2/api")
public class V2ApiBookController {

    @Autowired
    private BookOrgRelService bookOrgRelService;
    @Autowired
    private BookCatService bookCatService;
    @Autowired
    private BookIndexRecordService bookIndexRecordService;
    @Autowired
    private BookShelfService bookShelfService;
    @Autowired
    private BookRepoService bookRepoService;
    @Autowired
    private BookReviewService bookReviewService;
    @Autowired
    private MemberReadRecordService memberReadRecordService;
    @Autowired
    private SearchCountService searchCountService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private DeviceFeedbackService deviceFeedbackService;
    @Autowired
    private UnifyMemberService unifyMemberService;
    @Autowired
    private BookMarkService bookMarkService;
    @Autowired
    private AppVersionAndroidService appVersionAndroidService;
    @Autowired
    private AppVersionIosService appVersionIosService;

    /**
     * 获取所有图书分类
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/bookCat/getList", method = {RequestMethod.GET, RequestMethod.POST})
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
     * 图书列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/book/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getBookList(HttpServletRequest request) {
        try {
            String org_id = request.getParameter("org_id");
            String book_cat_id = request.getParameter("book_cat_id");
            String pageSize = request.getParameter("pageSize");
            String pageNum = request.getParameter("pageNum");
            String searchText = request.getParameter("searchText");
            if (StringUtils.isEmpty(searchText)) {
                searchText = "";
            } else {
                Boolean flag = bookRepoService.containsEmoji(searchText);
                if (flag == true) {
                    return JsonResult.getOther("请勿输入非法字符");
                }
            }
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
            String order_type = request.getParameter("order_type");
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
            if (StringUtils.isNotEmpty(order_type)){
                if ("hot".equals(order_type)){
                    sort = new Sort(Sort.Direction.DESC,"bi.unite_index").and(sort);
                }else if ("new".equals(order_type)){
                    sort = new Sort(Sort.Direction.DESC,"br.create_time").and(sort);
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
     * 提交意见反馈
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/suggest/commitSuggest", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object commitSuggest(HttpServletRequest request) {
        try {
            String content = request.getParameter("content");
            if (StringUtils.isEmpty(content)) {
                return JsonResult.getError("没有建议内容");
            }
            Boolean flag = bookRepoService.containsEmoji(content);
            if (flag == true) {
                return JsonResult.getOther("请勿输入非法字符");
            }
            String token = request.getParameter("member_token");
            if (token == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }

            DeviceFeedback feedback = new DeviceFeedback();
            feedback.setOrg_id(member.getOrg_id());
            feedback.setUser_id(member.getMember_id());
            feedback.setUser_name(member.getNick_name());
            feedback.setUser_type(2);//1:管理员用户;2:会员用户
            feedback.setOpinion(content);
            feedback.setDevice_type_code(token_type);
            deviceFeedbackService.saveDeviceFeedback(feedback);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_ADD);
            result.setData(feedback);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 获取会员所有书评分页列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/member/bookReview", method = {RequestMethod.GET, RequestMethod.POST})
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
            Object object = bookReviewService.getMemberReviewv2(member.getMember_id(), pageNum, pageSize, sort);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(object);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 会员阅读统计
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/member/readCount", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object readCount(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            if (StringUtils.isEmpty(member_token)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            if (token_type == null) {
                return JsonResult.getExpire(Constants.LOGIN_BIND_NULLTYPE);
            }
            UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            Date today = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            df.format(today);
            Integer todayTime = unifyMemberService.getTimes(member.getMember_id(), today);
            if (todayTime == null) {
                todayTime = 0;
            }
            Integer time = unifyMemberService.getTime(member.getMember_id());
            if (time == null) {
                time = 0;
            }
            Integer bookNums = unifyMemberService.getReadNum(member.getMember_id());
            if (bookNums == null) {
                bookNums = 0;
            }
            Integer sum = unifyMemberService.getSum(member.getMember_id());
            if (sum == null) {
                sum = 0;
            }
            Integer rank = unifyMemberService.getMemberRank(member.getMember_id(), member.getOrg_id());
            if (rank == null) {
                rank = 0;
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("todayTime", todayTime);
            data.put("allTime", time);
            data.put("bookNums", bookNums);
            data.put("reviewNum", sum);
            data.put("rank", rank);
            jsonResult.setData(data);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 添加书签
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/member/addBookMark", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object addBookMark(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            if (StringUtils.isEmpty(member_token)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            if (token_type == null) {
                return JsonResult.getExpire(Constants.LOGIN_BIND_NULLTYPE);
            }
            UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            String chapter_id = request.getParameter("chapter_id");
            if (StringUtils.isEmpty(chapter_id)) {
                return JsonResult.getError("请提供章节ID");
            }
            String bookId = request.getParameter("book_id");
            if (StringUtils.isEmpty(bookId)) {
                return JsonResult.getError("请提供正确的章节ID");
            }
            Long book_id = Long.parseLong(bookId);
            String content = request.getParameter("content");
            if (StringUtils.isEmpty(content)) {
                return JsonResult.getError("请提供书签内容");
            }
            String book_type = request.getParameter("book_type");
            if (StringUtils.isEmpty(book_type)) {
                book_type = "2";
            }
            Integer bk_type = Integer.parseInt(book_type);
            String chapter_name = request.getParameter("chapter_name");
            BookMark bookMark = bookMarkService.selectByChapId(Long.parseLong(chapter_id), member.getMember_id(),book_id);
            if (bookMark != null) {
                return JsonResult.getError("已添加本章节书签");
            }
            if (member.getOrg_id() == null){
                member.setOrg_id(189L);
            }
            BookMark bookMarkBean = new BookMark();
            bookMarkBean.setChapter_id(Long.parseLong(chapter_id));
            bookMarkBean.setBook_id(book_id);
            bookMarkBean.setContent(content);
            bookMarkBean.setMember_id(member.getMember_id());
            bookMarkBean.setOrg_id(member.getOrg_id());
            bookMarkBean.setBook_type(bk_type);
            bookMarkBean.setChapter_name(chapter_name);
            bookMarkService.saveBookMark(bookMarkBean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_ADD);
            jsonResult.setData(bookMarkBean);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取会员图书书签列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/member/getMarkList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getMarkList(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            if (StringUtils.isEmpty(member_token)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            if (token_type == null) {
                return JsonResult.getExpire(Constants.LOGIN_BIND_NULLTYPE);
            }
            UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            String book_id = request.getParameter("book_id");
            if (StringUtils.isEmpty(book_id)) {
                return JsonResult.getError("请提供图书编码");
            }
            String book_type = request.getParameter("book_type");
            if (StringUtils.isEmpty(book_type)){
                book_type = "2";
            }
            List<BookMarkBean> data = bookMarkService.getMarkList(member.getMember_id(), Long.parseLong(book_id),Integer.parseInt(book_type));
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(data);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除图书书签
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/member/deleteMarks", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object deleteMarks(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            if (StringUtils.isEmpty(member_token)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            if (token_type == null) {
                return JsonResult.getExpire(Constants.LOGIN_BIND_NULLTYPE);
            }
            UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            String marks = request.getParameter("marks");
            if (StringUtils.isEmpty(marks)) {
                return JsonResult.getError("请选择书签删除");
            }
            bookMarkService.deleteBookMarkByIds(marks);
            return JsonResult.getSuccess(Constants.ACTION_DELETE);

        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * App获取最新启用版本版本
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/appVersion/getAppVersion", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getAppVersion(HttpServletRequest request) {
        try {
            String device_type = request.getParameter("device_type");
            String app_type = request.getParameter("app_type");
            if (StringUtils.isEmpty(app_type)) {
                app_type = "1";
            }

            if ("android".equals(device_type)) {
                AppVersionAndroid appVersion = appVersionAndroidService.selectEnabled(Integer.parseInt(app_type));
                if (appVersion == null) {
                    return JsonResult.getError("版本未发布！");
                }
                String path = request.getContextPath();
                String basePath = request.getScheme() + "://" + request.getServerName();
                if (request.getServerPort() != 80) {
                    basePath = basePath + ":" + request.getServerPort();
                }
                basePath += path;
                appVersion.setPackage_url(basePath + appVersion.getPackage_url());
                JsonResult jsonResult = JsonResult.getSuccess(APIConstants.APP_VERSION_REVICED);
                jsonResult.setData(appVersion);
                return jsonResult;
            } else if ("ios".equals(device_type)) {
                AppVersionIos appVersion = appVersionIosService.selectEnabled(Integer.parseInt(app_type));
                if (appVersion == null) {
                    return JsonResult.getError("版本未发布！");
                }
                String path = request.getContextPath();
                String basePath = request.getScheme() + "://" + request.getServerName();
                if (request.getServerPort() != 80) {
                    basePath = basePath + ":" + request.getServerPort();
                }
                basePath += path;
                appVersion.setPackage_url(basePath + appVersion.getPackage_url());
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
     * web用户注销
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/member/logout", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                return JsonResult.getError("用户未登录");
            }
            String token = request.getAttribute("member_token").toString();
            //清除pc端token值
            unifyMemberService.updateToken(token);
            session.removeAttribute("member_info");
            session.removeAttribute("member_token");
            session.removeAttribute("member");
            return JsonResult.getSuccess("注销成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
