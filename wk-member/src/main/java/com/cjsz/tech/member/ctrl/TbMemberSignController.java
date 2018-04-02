package com.cjsz.tech.member.ctrl;

import com.cjsz.tech.member.beans.MemberSignListBean;
import com.cjsz.tech.member.domain.TbMemberSign;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.service.TbMemberSignService;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/12 0012.
 */
@Controller
public class TbMemberSignController {

    @Autowired
    private TbMemberSignService tbMemberSignService;

    @Autowired
    private UnifyMemberService unifyMemberService;

    @Autowired
    private UserService userService;

    /**
     * 会员签到查询分页列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/memberSign/getList",method = RequestMethod.POST)
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody MemberSignListBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            Object obj = tbMemberSignService.getList(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(obj);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 会员当月签到记录查询
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/memberSign/monthList",method = RequestMethod.POST)
    @ResponseBody
    public Object monthList(HttpServletRequest request,@RequestBody MemberSignListBean bean){
        try{
            if (bean.getMember_id() == null){
                return JsonResult.getOther("请选择会员");
            }
            if (bean.getDate_time() == null){
                return JsonResult.getOther("请选择月份");
            }
            List<TbMemberSign> result = tbMemberSignService.monthList(bean.getMember_id(),bean.getDate_time());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 会员签到
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/memberSign/signIn",method = RequestMethod.POST)
    @ResponseBody
    public Object signIn(HttpServletRequest request){
        try {
            String token = request.getParameter("member_token");
            if (StringUtils.isEmpty(token)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            if (StringUtils.isEmpty(token_type)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String action = request.getParameter("action");
            if(StringUtils.isEmpty(action)){
                return JsonResult.getError("请输入操作类型");
            }
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String date = request.getParameter("date");
            if (StringUtils.isEmpty(date)) {
                return JsonResult.getError("请提供日期");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String now = sdf.format(new Date());
            String type = request.getParameter("type");
            if (StringUtils.isEmpty(type)) {
                if (!date.equals(now)) {
                    return JsonResult.getError("请检查设备日期！");
                }
            }
            Date sign_time = null;
            try {
                sign_time = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TbMemberSign memberSign = tbMemberSignService.selectByMemeberIdAndTime(member.getMember_id(),sign_time);
            if (memberSign != null){
                return JsonResult.getError("今日已签到");
            }
            if("is_sign".equals(action)){
                return JsonResult.getSuccess("今日未签到");
            }
            TbMemberSign tbMemberSign = new TbMemberSign();
            tbMemberSign.setMember_id(member.getMember_id());
            tbMemberSign.setSign_time(sign_time);
            TbMemberSign sign = tbMemberSignService.signIn(tbMemberSign);
            JsonResult jsonResult = JsonResult.getSuccess("签到成功");
            jsonResult.setData(sign);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 会员抽奖
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/memberSign/signGift",method = RequestMethod.POST)
    @ResponseBody
    public Object signGift(HttpServletRequest request){
        try {
            String token = request.getParameter("member_token");
            if (StringUtils.isEmpty(token)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            if (StringUtils.isEmpty(token_type)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String date = request.getParameter("date");
            if (StringUtils.isEmpty(date)) {
                date = new Date().toString();
            }
            String sign_gift = request.getParameter("sign_gift");
            if(StringUtils.isEmpty(sign_gift)){
                return JsonResult.getError("请输入奖励的长江币");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date sign_time = null;
            try {
                sign_time = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TbMemberSign memberSign = tbMemberSignService.selectByMemeberIdAndTime(member.getMember_id(),sign_time);
            if (memberSign == null){
                return JsonResult.getError("今日未签到");
            }
            if(StringUtils.isNotEmpty(memberSign.getSign_gift())){
                return JsonResult.getError("今日已抽奖");
            }
            memberSign.setSign_gift(sign_gift);
            tbMemberSignService.updateSignGift(memberSign);
            JsonResult jsonResult = JsonResult.getSuccess("抽奖成功");
            jsonResult.setData(memberSign);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 查询月签到记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/api/memberSign/monthSign",method = RequestMethod.POST)
    @ResponseBody
    public Object monthSign(HttpServletRequest request){
        try {
            String token = request.getParameter("member_token");
            if (StringUtils.isEmpty(token)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            if (StringUtils.isEmpty(token_type)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            List<TbMemberSign> monthList = tbMemberSignService.getMonthList(member.getMember_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(monthList);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
