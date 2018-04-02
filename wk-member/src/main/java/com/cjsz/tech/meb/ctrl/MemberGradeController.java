package com.cjsz.tech.meb.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.meb.domain.MemberGrade;
import com.cjsz.tech.meb.service.MemberGradeService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;

/**
 * 会员等级
 * Created by Administrator on 2017/3/15 0015.
 */
@Controller
public class MemberGradeController {

    @Autowired
    private MemberGradeService memberGradeService;

    /**
     * 会员等级列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/json/memberGrade/list", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody PageConditionBean bean){
        try{
            Sort sort = new Sort(Sort.Direction.ASC, "grade_id");
            Object obj = memberGradeService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改会员等级（只能修改图片）
     * @param request
     * @param memberGrade
     * @return
     */
    @RequestMapping(value = "/admin/json/memberGrade/update", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object updateMemberGrade(HttpServletRequest request, @RequestBody MemberGrade memberGrade){
        try{
            memberGradeService.updateMemberGrade(memberGrade);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(memberGrade);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
