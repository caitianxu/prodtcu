package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.APIConstants;
import com.cjsz.tech.api.service.CommonBookService;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.BookReview;
import com.cjsz.tech.book.service.BookRepoService;
import com.cjsz.tech.book.service.BookReviewService;
import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.OrgExtend;
import com.cjsz.tech.system.service.OrgExtendService;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.StringUtil;
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
 * 图书评论
 * Created by Administrator on 2017/3/16 0016.
 */
@Controller
public class ApiBookReviewController {

    @Autowired
    private BookReviewService bookReviewService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrgExtendService orgExtendService;

    @Autowired
    private BookRepoService bookRepoService;

    @Autowired
    private CommonBookService commonBookService;

    /**
     * 评论列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/bookReview/getList", method = {RequestMethod.POST})
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
        BookRepo repoBook = bookRepoService.findByBookId(Long.parseLong(book_id));
        if (repoBook == null) {
            return JsonResult.getError(APIConstants.NO_BOOK);
        }
        String org_id = request.getParameter("org_id");
        Sort sort = new Sort(Sort.Direction.DESC, "create_time");
        Object obj = bookReviewService.sitePageQuery(sort, bean, Long.valueOf(org_id), Long.valueOf(book_id));
        JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
        result.setData(obj);
        return result;

    }

    /**
     * 添加评论
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/bookReview/addReview", method = {RequestMethod.POST})
    @ResponseBody
    public Object addReview(HttpServletRequest request) {
        String member_token = request.getParameter("member_token");
        if (StringUtil.isEmpty(member_token)) {
            return JsonResult.getError(APIConstants.TOKEN_NULL);
        }
        Member member = memberService.findByToken(member_token, "2");
        if (member == null) {
            return JsonResult.getError(Constants.LOGIN_BIND_NOUSER);
        }
        Long org_id = null;
        OrgExtend orgExtend = orgExtendService.getByOrgId(member.getOrg_id());
        if (orgExtend != null) {
            org_id = orgExtend.getOrg_id();
        } else {
            org_id = 1L;
        }

        String book_id = request.getParameter("book_id");
        if (StringUtil.isEmpty(book_id)) {
            return JsonResult.getError(APIConstants.MEMBER_SHELF_NOTHING_ERROR);
        }
        BookRepo repoBook = bookRepoService.findByBookId(Long.parseLong(book_id));
        if (repoBook == null) {
            return JsonResult.getError(APIConstants.NO_BOOK);
        }
        String review_content = request.getParameter("review_content");
        if (StringUtil.isEmpty(review_content)) {
            return JsonResult.getError(APIConstants.REVIEW_CONTENT_NOTHING_ERROR);
        }
        String device_type = request.getParameter("device_type");
        if (StringUtil.isEmpty(device_type)) {
            return JsonResult.getError(APIConstants.DEVICE_CODE_NOTHING_ERROR);
        }

        BookReview review = new BookReview();
        review.setPid(0L);
        review.setMember_id(member.getMember_id());
        review.setBook_id(Long.parseLong(book_id));
        review.setReview_content(review_content);
        review.setOrg_id(org_id);
        review.setCreate_time(new Date());
        review.setReview_nums(0L);

        String pid = request.getParameter("pid");
        if (StringUtil.isEmpty(pid)) {
            //图书评论:添加评论、图书指数记录、用户积分变更、用户积分记录
//            bookReviewService.saveBookReview(review, device_type);
            commonBookService.saveReview(review, device_type);
        } else {
            //评论回复
            BookReview bookReview = bookReviewService.selectById(Long.parseLong(pid));
            if (bookReview == null) {
                return JsonResult.getError(APIConstants.NO_BOOK_REVIEW);
            }
            review.setPid(Long.parseLong(pid));
            review.setFull_path(bookReview.getFull_path());
            bookReviewService.saveBookReviewResponse(review, device_type);
        }
        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.BOOK_REVIEW_ADD_SUCCESS);
        jsonResult.setData(review);
        return jsonResult;
    }


    /**
     * 删除评论（只能删除自己的，状态删除）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/bBookReview/deleteReviews", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object deleteReviews(HttpServletRequest request) {
        try {
            String reviewIds = request.getParameter("reviewIds");
            String member_token = request.getParameter("member_token");
            Member member = null;
            if (member_token != null) {
                member = memberService.findByToken(member_token, "2");
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
