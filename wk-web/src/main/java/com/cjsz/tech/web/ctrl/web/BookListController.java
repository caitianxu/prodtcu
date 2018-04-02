package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.book.beans.FindBookBean;
import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.book.service.BookCatService;
import com.cjsz.tech.book.service.BookOrgRelService;
import com.cjsz.tech.count.domain.SearchCount;
import com.cjsz.tech.count.service.SearchCountService;
import com.cjsz.tech.dev.domain.DeviceFeedback;
import com.cjsz.tech.dev.service.DeviceFeedbackService;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.domain.AdvCat;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.service.AdvCatService;
import com.cjsz.tech.system.service.AdvService;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BookListController {

    @Autowired
    private BookOrgRelService bookOrgRelService;
    @Autowired
    private BookCatService bookCatService;
    @Autowired
    SearchCountService searchCountService;
    @Autowired
    OrganizationService organizationService;
    @Autowired
    DeviceFeedbackService deviceFeedbackService;
    @Autowired
    UnifyMemberService unifyMemberService;
    @Autowired
    AdvCatService advCatService;
    @Autowired
    AdvService advService;

    @Autowired
    ProOrgExtendService proOrgExtendService;

    @RequestMapping("/web/booklist")
    public ModelAndView search(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            String key = request.getParameter("key");
            String tag_name = request.getParameter("tag_name");
            String cat_id = request.getParameter("cid");
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            String order_type = request.getParameter("order_type");
            if (StringUtils.isEmpty(key)) {
                key = "";
            } else {
                //添加搜索记录
                SearchCount searchCount = searchCountService.selectByNameAndOrgId(key, Long.valueOf(org_id));
                if (searchCount == null) {
                    //搜索前未搜索过本次搜索内容
                    Organization org = organizationService.selectById(Long.valueOf(org_id));
                    searchCount = new SearchCount();
                    searchCount.setOrg_id(org.getOrg_id());
                    searchCount.setOrg_name(org.getOrg_name());
                    searchCount.setName(key);
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
            cat_id = cat_id != null ? cat_id : "";
            request.setAttribute("key", key);
            request.setAttribute("cat_id", cat_id);
            if (StringUtils.isEmpty(tag_name)) {
                tag_name = "";
            }
            request.setAttribute("tag_name", tag_name);


            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));

            FindBookBean bean = new FindBookBean();
            bean.setPageNum(1);
            bean.setPageSize(25);
            bean.setSearchText(key);
            bean.setTag_name(tag_name);
            bean.setOrg_id(Long.valueOf(org_id));
            if (StringUtils.isNotEmpty(cat_id)) {
                bean.setBook_cat_id(Long.valueOf(cat_id));
            }
            if (StringUtils.isNotEmpty(order_type)) {
                bean.setOrder_type(order_type);
            }
            Object bookList = bookOrgRelService.sitePageQuery(sort, bean);
            mv.addObject("bookList", bookList);

            //获取机构所有分类转树形结构
            List<BookCat> cats = bookCatService.selectAllCats(Long.valueOf(org_id));
            List<BookCat> bookCatList = bookCatService.selectTree(cats);

            //分类下广告
            AdvCat advCat = advCatService.selectByCatCode("00006");
            List<Adv> advModuleList = advService.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), Long.parseLong(org_id));
            mv.addObject("advModuleList", advModuleList);
            mv.addObject("bookCatList", bookCatList);
            mv.setViewName(tempName + "/booklist");
        } else {
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping(value = "/site/book/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getBookList(HttpServletRequest request, FindBookBean bean) {
        try {
            String org_id = request.getAttribute("org_id").toString();
            String book_cat_id = request.getParameter("book_cat_id");
            bean.setOrg_id(Long.valueOf(org_id));
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            if (book_cat_id != null && StringUtils.isNotEmpty(book_cat_id)) {
                bean.setBook_cat_id(Long.valueOf(book_cat_id));
                if (book_cat_id.equals("-1")) {
                    //精选
                    sort = new Sort(Sort.Direction.ASC, "is_recommend").and(sort);
                } else if (book_cat_id.equals("0")) {
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
     * 获取面包屑分类
     * @param request
     * @return
     */
    @RequestMapping(value = "/site/bookCat/getCats" , method = RequestMethod.POST)
    @ResponseBody
    public Object getCats(HttpServletRequest request){
        try {
            String cat_id = request.getParameter("cat_id");
            String org_id = request.getParameter("org_id");
            if (StringUtils.isEmpty(org_id)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            Map<String,Object> map = new HashMap<>();
            if (StringUtils.isNotEmpty(cat_id)) {
                BookCat cat = bookCatService.selectByCatId(Long.parseLong(cat_id), Long.parseLong(org_id));
                if (cat != null) {
                    BookCat c_cat = bookCatService.selectByCatId(cat.getBook_cat_pid(), Long.parseLong(org_id));
                    if (c_cat != null) {
                        map.put("cat_name", c_cat.getBook_cat_name());
                        map.put("cat_name_id", c_cat.getBook_cat_id());
                        map.put("c_cat_name", cat.getBook_cat_name());
                    } else {
                        map.put("cat_name", cat.getBook_cat_name());
                        map.put("cat_name_id", cat.getBook_cat_id());
                        map.put("c_cat_name", null);
                    }
                }
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(map);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    @RequestMapping(value = "/site/bookCat/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getBookCatList(HttpServletRequest request) {
        try {
            String org_id = request.getAttribute("org_id").toString();
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
     * 提交意见反馈
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/site/suggest/commitSuggest", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object commitSuggest(HttpServletRequest request) {
        try {
            String content = request.getParameter("content");
            if (StringUtils.isEmpty(content)) {
                return JsonResult.getError("没有建议内容");
            }
            String member_token = request.getParameter("member_token");
            if (member_token == null) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            }
            UnifyMember member = (UnifyMember) request.getSession().getAttribute("member_info");
            if (member == null) {
                return JsonResult.getError("用户超时");
            } else {
                DeviceFeedback feedback = new DeviceFeedback();
                feedback.setOrg_id(member.getOrg_id());
                feedback.setUser_id(member.getMember_id());
                feedback.setUser_name(member.getNick_name());
                feedback.setUser_type(2);//1:管理员用户;2:会员用户
                feedback.setOpinion(content);
                feedback.setDevice_type_code("000004");
                deviceFeedbackService.saveDeviceFeedback(feedback);
                JsonResult result = JsonResult.getSuccess(Constants.ACTION_ADD);
                result.setData(feedback);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
