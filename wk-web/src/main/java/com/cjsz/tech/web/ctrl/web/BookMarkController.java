package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.book.domain.BookMark;
import com.cjsz.tech.book.service.BookMarkService;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 书签
 * Created by LuoLi on 2017/4/10.
 */
@Controller
public class BookMarkController {

    @Autowired
    BookMarkService bookMarkService;
    @Autowired
    UnifyMemberService unifyMemberService;

    @RequestMapping(value = "/site/bookMark/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getList(HttpServletRequest request){
        try{
            String token = (String)request.getSession().getAttribute("token");
            UnifyMember member = (UnifyMember)request.getSession().getAttribute("member_info");
            if(member == null){
                return JsonResult.getError("用户信息超时！");
            }
            Long member_id = member.getMember_id();
            String book_id = request.getParameter("book_id");
            List<BookMark> bookMarks = bookMarkService.getListByMemberId(member_id, Long.valueOf(book_id));
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(bookMarks);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    @RequestMapping(value = "/site/bookMark/saveBookMark", method = RequestMethod.POST)
    @ResponseBody
    public Object saveBookMark(HttpServletRequest request, BookMark bookMark){
        try{
            String token = (String)request.getSession().getAttribute("token");
            UnifyMember member =(UnifyMember) request.getSession().getAttribute("member_info");
            if(member == null){
                return JsonResult.getError("用户信息超时！");
            }
            Long member_id = member.getMember_id();
            Long org_id = member.getOrg_id();
            bookMark.setMember_id(member_id);
            bookMark.setOrg_id(org_id);
            bookMarkService.saveBookMark(bookMark);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_ADD);
            result.setData(bookMark);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    @RequestMapping(value = "/site/bookMark/deleteBookMark", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteBookMark(HttpServletRequest request){
        try{
            String token = (String)request.getSession().getAttribute("token");
            UnifyMember member = (UnifyMember)request.getSession().getAttribute("member_info");
            if(member == null){
                return JsonResult.getError("用户信息超时！");
            }
            String markIds = request.getParameter("markIds");
            bookMarkService.deleteBookMarkByIds(markIds);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


}
