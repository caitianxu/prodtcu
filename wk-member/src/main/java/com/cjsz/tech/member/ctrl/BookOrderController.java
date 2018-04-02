package com.cjsz.tech.member.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.member.beans.OrderListBean;
import com.cjsz.tech.member.domain.BookOrder;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.service.BookOrderService;
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

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/10/10 0010.
 */
@Controller
public class BookOrderController {

    @Autowired
    private BookOrderService bookOrderService;
    @Autowired
    private UnifyMemberService unifyMemberService;


    /**
     * 验证出版图书是否已购买
     * @param request
     * @return
     */
    @RequestMapping(value = "/v3/bookOrder/veriftBuy",method = RequestMethod.POST)
    @ResponseBody
    public Object veriftBuy(HttpServletRequest request){
        try{
            //登录验证
            String token = request.getParameter("member_token");
            if (StringUtils.isEmpty(token)){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String token_type = request.getParameter("token_type");
            if (StringUtils.isEmpty(token_type)){
                return JsonResult.getExpire(Constants.EXCEPTION);
            }
            UnifyMember member = unifyMemberService.findByToken(token,token_type);
            if (member == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            String book_id = request.getParameter("book_id");
            if (StringUtils.isEmpty(book_id)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            Boolean flag = bookOrderService.isBuyById(member.getMember_id(),Long.parseLong(book_id));
            JsonResult jsonResult = JsonResult.getSuccess("");
            jsonResult.setData(flag);
            if (flag){
                jsonResult.setMessage("验证成功");
            }else {
                jsonResult.setMessage("验证失败");
            }
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 后台订单查询
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/bookOrder/getOrderList",method = RequestMethod.POST)
    @ResponseBody
    public Object getOrderList(HttpServletRequest request, @RequestBody OrderListBean bean){
        try{
            Object result = bookOrderService.getList(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
