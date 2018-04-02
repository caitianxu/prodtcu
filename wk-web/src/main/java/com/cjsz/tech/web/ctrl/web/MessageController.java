package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.service.BookRepoService;
import com.cjsz.tech.dev.domain.DeviceFeedback;
import com.cjsz.tech.dev.service.DeviceFeedbackService;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MessageController {


    @Autowired
    private BookRepoService bookRepoService;
    @Autowired
    private DeviceFeedbackService deviceFeedbackService;


    @RequestMapping("/web/message")
    public ModelAndView memberMessage(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        UnifyMember member = (UnifyMember) request.getSession().getAttribute("member_info");
        if (member != null) {
            PageConditionBean bean = new PageConditionBean();
            bean.setPageNum(1);
            bean.setPageSize(10);
            Object result = deviceFeedbackService.getMemberFeedBackList(bean,member.getMember_id());
            mv.addObject("memberMessage",result);
            mv.setViewName("web/message");
        }else {
            mv.setViewName("web/login");
        }
        return mv;
    }



    /**
     * 提交意见反馈
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/site/message/suggest", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object suggest(HttpServletRequest request) {
        try {
            String content = request.getParameter("content");
            if (StringUtils.isEmpty(content)) {
                return JsonResult.getError("请输入留言内容!");
            }
            Boolean flag = bookRepoService.containsEmoji(content);
            if (flag == true) {
                return JsonResult.getOther("请勿输入非法字符!");
            }
            UnifyMember member = (UnifyMember) request.getSession().getAttribute("member_info");
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }

            DeviceFeedback feedback = new DeviceFeedback();
            feedback.setOrg_id(member.getOrg_id());
            if (member.getDept_id() != null) {
                feedback.setDept_id(member.getDept_id());
            }else {
                feedback.setDept_id(member.getOrg_id());
            }
            feedback.setUser_id(member.getMember_id());
            feedback.setUser_name(member.getNick_name());
            feedback.setUser_type(2);//1:管理员用户;2:会员用户
            feedback.setOpinion(content);
            feedback.setDevice_type_code("web");
            deviceFeedbackService.saveDeviceFeedback(feedback);
            JsonResult result = JsonResult.getSuccess("发送成功！");
            result.setData(feedback);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 分页获取会员留言列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/site/message/getMemberList",method = RequestMethod.POST)
    @ResponseBody
    public Object siteGetMemberList(HttpServletRequest request,PageConditionBean bean){
        try{
            UnifyMember member = (UnifyMember) request.getSession().getAttribute("member_info");
            if (member == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            Object result = deviceFeedbackService.getMemberFeedBackList(bean,member.getMember_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    @RequestMapping(value = "/site/message/deleteMessage",method = RequestMethod.POST)
    @ResponseBody
    public  Object siteDeleteMessage(HttpServletRequest request,DeviceFeedback bean){
        try{
            UnifyMember member = (UnifyMember)request.getSession().getAttribute("member_info");
            if (member == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            deviceFeedbackService.deleteMessage(bean.getDevice_feedback_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
