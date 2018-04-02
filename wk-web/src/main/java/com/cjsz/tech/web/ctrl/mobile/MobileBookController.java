package com.cjsz.tech.web.ctrl.mobile;

import com.cjsz.tech.book.beans.BookBean;
import com.cjsz.tech.book.service.BookOrgRelService;
import com.cjsz.tech.member.domain.UnifyMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


@Controller
public class MobileBookController {

    @Autowired
    private BookOrgRelService bookOrgRelService;

    /**
     * 图书阅读器
     *
     * @return
     */
    @RequestMapping("/mobile/book/{bookId}")
    public ModelAndView mobileBookshelf(HttpServletRequest request, @PathVariable String bookId) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            if (request.getSession().getAttribute("member_token") != null) {
                mv.setViewName("mobile/book");
                mv.addObject("bookId", bookId);
                Long member_id = 0L;
                UnifyMember member = (UnifyMember) request.getSession().getAttribute("member_info");
                if (member != null) {
                    member_id = member.getMember_id();
                }
                Long org_id = Long.parseLong(request.getAttribute("org_id").toString());
                BookBean bookInfo = bookOrgRelService.findByOrgIdAndBookIdAndMemberId(org_id, Long.valueOf(bookId), member_id);
                //图书详情
                mv.addObject("bookInfo", bookInfo);
            } else {
                mv.setViewName("mobile/login");
            }
        } else {
            mv.setViewName("mobile/404");
        }
        return mv;
    }

}
