package com.cjsz.tech.web.ctrl.mobile;

import com.cjsz.tech.api.beans.ReviewBeans;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.BookReviewBean;
import com.cjsz.tech.book.service.BookReviewService;
import com.cjsz.tech.core.page.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/8/3 0003.
 */
@Controller
public class MobileCommentDetailController {

    @Autowired
    private BookReviewService bookReviewService;


    @RequestMapping("/mobile/commentDetail/{reviewid}")
    public ModelAndView mobileCommentDetail(HttpServletRequest request, @PathVariable String reviewid){
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null){
            if (request.getSession().getAttribute("member_token") != null){
                BookReviewBean bookReview = bookReviewService.selectReviewById(Long.parseLong(reviewid));
                Sort sort = new Sort(Sort.Direction.DESC, "create_time");
                PageConditionBean bean = new PageConditionBean();
                bean.setPageSize(10);
                bean.setPageNum(1);
                PageList subBookReview = bookReviewService.getReviewsByPid(sort, bean, Long.parseLong(reviewid));
                ReviewBeans reviewBeans = new ReviewBeans();
                reviewBeans.setBookReview(bookReview);
                reviewBeans.setObject(subBookReview);
                mv.addObject("total",subBookReview.getTotal());
                mv.addObject("commentDetail",reviewBeans);
                mv.setViewName("mobile/commentDetail");
            }else {
                mv.setViewName("mobile/login");
            }
        }else {
            mv.setViewName("mobile/404");
        }
        return mv;
    }


    @RequestMapping("/mobile/commentDetail")
    public ModelAndView mobileCommentDetailErr(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("mobile/err");
        return mv;
    }
}
