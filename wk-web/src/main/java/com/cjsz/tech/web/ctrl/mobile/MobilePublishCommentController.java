package com.cjsz.tech.web.ctrl.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


@Controller
public class MobilePublishCommentController {

    /**
     * 发布、回复评论
     *
     * @return
     */
    @RequestMapping("/mobile/publishComment/{bookId}/{commId}")
    public ModelAndView mobilePublishCommentComm(HttpServletRequest request, @PathVariable String bookId, @PathVariable String commId) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            if (request.getSession().getAttribute("member_token") != null) {
                mv.setViewName("mobile/publishComment");
                mv.addObject("bookId", bookId);
                mv.addObject("commId", commId);
            } else {
                mv.setViewName("mobile/login");
            }
        } else {
            mv.setViewName("mobile/404");
        }
        return mv;
    }

    /**
     * 发布、回复评论
     *
     * @return
     */
    @RequestMapping("/mobile/publishComment/{bookId}")
    public ModelAndView mobilePublishCommentBook(HttpServletRequest request, @PathVariable String bookId) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            if (request.getSession().getAttribute("member_token") != null) {
                mv.setViewName("mobile/publishComment");
                mv.addObject("bookId", bookId);
                mv.addObject("commId", "");
            } else {
                mv.setViewName("mobile/login");
            }
        } else {
            mv.setViewName("mobile/404");
        }
        return mv;
    }

    /**
     * 发布、回复评论  err
     *
     * @return
     */
    @RequestMapping("/mobile/publishComment")
    public ModelAndView mobilePublishCommentErr(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("mobile/err");
        return mv;
    }
}
