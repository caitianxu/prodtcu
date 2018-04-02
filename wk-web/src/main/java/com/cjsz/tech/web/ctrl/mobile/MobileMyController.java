package com.cjsz.tech.web.ctrl.mobile;

import com.cjsz.tech.member.beans.MemberBean;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Controller
public class MobileMyController {

    @Autowired
    private UnifyMemberService unifyMemberService;

    /**
     * 书架
     * @return
     */
    @RequestMapping("/mobile/my")
    public ModelAndView mobileMy(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        Long org_id =0L;
        UnifyMember member = null;
        if(request.getAttribute("org_id") != null) {
            String org_Id = request.getAttribute("org_id").toString();
            org_id = Long.parseLong(org_Id);
            if(request.getSession().getAttribute("member_token") != null) {
                member = (UnifyMember)request.getSession().getAttribute("member_info");
                Date today = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                df.format(today);

                MemberBean memberBean = new MemberBean();
                Integer todayTime = unifyMemberService.getTimes(member.getMember_id(),today);
                Integer time = unifyMemberService.getTime(member.getMember_id());
                Integer bookNums = unifyMemberService.getReadNum(member.getMember_id());
                Integer sum = unifyMemberService.getSum(member.getMember_id());
                Integer rank = unifyMemberService.getMemberRank(member.getMember_id(),org_id);
                memberBean.setAccount(member.getAccount());
                memberBean.setBookNums(bookNums);
                memberBean.setEmail(member.getEmail());
                memberBean.setIcon(member.getIcon());
                memberBean.setMember_id(member.getMember_id());
                memberBean.setNick_name(member.getNick_name());
                memberBean.setOrg_id(member.getOrg_id());
                memberBean.setPhone(member.getPhone());
                memberBean.setRank(rank);
                memberBean.setSex(member.getSex());
                memberBean.setSum(sum);
                memberBean.setTime(time);
                memberBean.setTodayTime(todayTime);

                mv.addObject("member_count",memberBean);
                mv.setViewName("mobile/my");
            }
            else{
                mv.setViewName("mobile/login");
            }
        }
        else{
            mv.setViewName("mobile/404");
        }


        return mv;
    }

}
