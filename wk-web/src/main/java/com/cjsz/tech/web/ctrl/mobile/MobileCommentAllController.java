package com.cjsz.tech.web.ctrl.mobile;

import com.cjsz.tech.beans.PageConditionBean;
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
public class MobileCommentAllController {

    @Autowired
    private BookReviewService bookReviewService;


    @RequestMapping("/mobile/commentAll/{bookid}")
    public ModelAndView mobileComment(HttpServletRequest request, @PathVariable String bookid) {
        ModelAndView mv = new ModelAndView();
        String org_id = "";
        if (request.getAttribute("org_id") != null) {
            org_id = request.getAttribute("org_id").toString();
            if (request.getSession().getAttribute("member_token") != null) {
                PageConditionBean bean = new PageConditionBean();
                bean.setPageNum(1);
                bean.setPageSize(24);
                Sort sort = new Sort(Sort.Direction.DESC, "create_time");
                PageList result = (PageList) bookReviewService.getReviewsList(sort, bean, Long.valueOf(org_id), Long.valueOf(bookid));
                mv.addObject("commentAll", result);
                mv.addObject("bookId", bookid);
                mv.addObject("total", result.getTotal());
                mv.setViewName("mobile/commentAll");
            } else {
                mv.setViewName("mobile/login");
            }
        } else {
            mv.setViewName("mobile/404");
        }
        return mv;
    }

    @RequestMapping("/mobile/commentAll")
    public ModelAndView mobileCommentErr(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("mobile/err");
        return mv;
    }
}
