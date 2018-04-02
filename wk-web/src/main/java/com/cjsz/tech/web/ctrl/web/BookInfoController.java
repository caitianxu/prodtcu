package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.api.service.CommonBookService;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.BookBean;
import com.cjsz.tech.book.beans.BookInfo;
import com.cjsz.tech.book.domain.BookIndexRecord;
import com.cjsz.tech.book.domain.BookReview;
import com.cjsz.tech.book.service.BookIndexRecordService;
import com.cjsz.tech.book.service.BookOrgRelService;
import com.cjsz.tech.book.service.BookReviewService;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.OrgExtend;
import com.cjsz.tech.system.service.OrgExtendService;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.utils.JsonResult;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class BookInfoController {

    @Autowired
    BookOrgRelService bookOrgRelService;
    @Autowired
    UnifyMemberService unifyMemberService;
    @Autowired
    BookReviewService bookReviewService;
    @Autowired
    OrgExtendService orgExtendService;
    @Autowired
    BookIndexRecordService bookIndexRecordService;
    @Autowired
    CommonBookService commonBookService;

    @Autowired
    ProOrgExtendService proOrgExtendService;

    @RequestMapping("/web/bookinfo/{bookid}")
    public ModelAndView search(HttpServletRequest request, @PathVariable String bookid) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            Long member_id = 0L;
            Object token = request.getSession().getAttribute("member_token");
            UnifyMember member = null;
            if (token != null) {
                member = (UnifyMember) request.getSession().getAttribute("member_info");
                if (member != null) {
                    member_id = member.getMember_id();
                }
            }

            BookBean bookInfo = bookOrgRelService.findByOrgIdAndBookIdAndMemberId(Long.valueOf(org_id), Long.valueOf(bookid), member_id);
            if (bookInfo != null) {
                mv.addObject("bookInfo", bookInfo);
                mv.addObject("book_id", bookid);
                //点击图书，图书指数记录
                //record_type(1:点击详情;2:收藏;3:分享;4:评论;5:阅读); pc
                BookIndexRecord bookIndexRecord = new BookIndexRecord(Long.valueOf(org_id), Long.valueOf(bookid),2, member_id, 1, new Date(), "pc");
                bookIndexRecordService.addRecord(bookIndexRecord);



                String num = request.getParameter("limit");
                if (StringUtils.isEmpty(num)){
                    num = "2";
                }
                Integer limit = Integer.parseInt(num);

                //热搜
                List<BookBean> hotList = bookOrgRelService.getHotList(Long.parseLong(org_id),limit);
                mv.addObject("hotList",hotList);
                //推荐
                List<BookBean> recommendList = bookOrgRelService.getRecommendList(Long.parseLong(org_id),limit);
                mv.addObject("recommendList",recommendList);

                limit = 5;
                //相关推荐
                List<BookInfo> datas = Lists.newArrayList();
                //1.获取同作者的的相关推荐
                List<BookInfo> sameAuthorBooks = bookOrgRelService.findSameAuthorBooks(Long.valueOf(bookid), Long.parseLong(org_id), limit);
                if (sameAuthorBooks != null && sameAuthorBooks.size() > 0) {
                    limit -= sameAuthorBooks.size();
                    datas.addAll(sameAuthorBooks);
                }
                List<Long> book_ids = new ArrayList<>();
                String bookIds = "";
                if (sameAuthorBooks.size() > 0) {
                    for (BookInfo books : sameAuthorBooks) {
                        book_ids.add(books.getBook_id());
                    }
                    bookIds = StringUtils.join(book_ids, ",");
                }
                //2.获取同分类的相关推荐
                if (limit > 0) {
                    List<BookInfo> sameCatalogBooks = bookOrgRelService.findSameCatalogBooks(Long.valueOf(bookid), Long.parseLong(org_id), bookInfo.getBook_cat_id(), limit, bookIds);//需要明确指定是那个分类，因为同一本图书可能在不同的分类下面
                    if (sameCatalogBooks != null && sameCatalogBooks.size() > 0) {
                        datas.addAll(sameCatalogBooks);
                    }
                }
                mv.addObject("bookRecommendList",datas);
                mv.setViewName(tempName + "/bookinfo");
            } else {
                mv.setViewName("web/err");
            }
        }
        else{
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping("/web/bookinfo")
    public ModelAndView search(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("web/err");
        return mv;
    }


    /**
     * 评论列表接口
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/site/bookReview/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getReviewList(HttpServletRequest request, PageConditionBean bean) {
        try {
            String book_id = request.getParameter("book_id");
            String org_id = request.getAttribute("org_id").toString();
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            Object obj = bookReviewService.sitePageQuery(sort, bean, Long.valueOf(org_id), Long.valueOf(book_id));
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 评论回复接口
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/site/bookReview/addReview", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object addReview(HttpServletRequest request) {
        try {
            Object token = request.getSession().getAttribute("member_token");
            String token_type = (String)request.getSession().getAttribute("token_type");
            UnifyMember member = null;
            if(token != null){
                member = unifyMemberService.findByToken(token.toString(), token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            String org_id = request.getAttribute("org_id").toString();
            OrgExtend orgExtend = orgExtendService.getByOrgId(Long.valueOf(org_id));
            String book_id = request.getParameter("book_id");
            String review_content = request.getParameter("review_content");
            BookReview review = new BookReview();
            review.setPid(0L);
            review.setMember_id(member.getMember_id());
            review.setBook_id(Long.parseLong(book_id));
            review.setReview_content(review_content);
            review.setOrg_id(orgExtend.getOrg_id());
            review.setDevice_type_code(token_type);
            review.setCreate_time(new Date());
            review.setReview_nums(0L);

            String pid = request.getParameter("pid");
            if (StringUtils.isEmpty(pid)) {
                //图书评论:添加评论、图书指数记录、用户积分变更、用户积分记录
                //bookReviewService.saveBookReview(review, "");
                commonBookService.saveReview(review, "");
            } else {
                //评论回复
                BookReview bookReview = bookReviewService.selectById(Long.parseLong(pid));
                review.setPid(Long.parseLong(pid));
                review.setFull_path(bookReview.getFull_path());
                bookReviewService.saveBookReviewResponse(review, "");
            }
            BookReview bookReview = bookReviewService.getInfoById(review.getReview_id());
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(bookReview);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 删除评论（只能删除自己的，状态删除）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/site/bookReview/deleteReviews", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object deleteReviews(HttpServletRequest request) {
        try {
            String reviewIds = request.getParameter("reviewIds");
            Object token = request.getSession().getAttribute("member_token");
            String token_type = (String)request.getSession().getAttribute("token_type");
            UnifyMember member = null;
            if(token != null){
                member = unifyMemberService.findByToken(token.toString(), token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            List<BookReview> bookReviews = bookReviewService.selectNotMyReviewsByIds(member.getMember_id(), reviewIds);
            if (bookReviews.size() > 0) {
                return JsonResult.getError("不能删除其他用户的评论！");
            }
            bookReviewService.deleteReviews(reviewIds);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
