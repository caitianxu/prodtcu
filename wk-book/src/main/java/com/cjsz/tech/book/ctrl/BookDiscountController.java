package com.cjsz.tech.book.ctrl;

import com.cjsz.tech.book.beans.BookTypeBean;
import com.cjsz.tech.book.beans.DelBookBean;
import com.cjsz.tech.book.domain.BookDiscount;
import com.cjsz.tech.book.service.BookDiscountService;
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

/**
 * Created by Administrator on 2017/9/8 0008.
 */
@Controller
public class BookDiscountController {

    @Autowired
    private BookDiscountService bookDiscountService;
    @Autowired
    private UserService userService;


    /**
     * 获取折扣书籍分页列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/discount/getList",method = RequestMethod.POST)
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody BookTypeBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            Object result = bookDiscountService.getList(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 添加折扣书籍
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/discount/addBook",method = RequestMethod.POST)
    @ResponseBody
    public Object addBook(HttpServletRequest request, @RequestBody BookDiscount bean){
        try{
            if (bean.getBook_id() == null){
                return JsonResult.getError("请提供图书id");
            }
            if (bean.getChannel_type() == null){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            BookDiscount book = bookDiscountService.selectById(bean.getBook_id(),bean.getChannel_type());
            if (book != null){
                return JsonResult.getError("图书已存在");
            }
            bookDiscountService.addBook(bean.getBook_id(),bean.getChannel_type(),bean.getOrder_weight());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_ADD);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 折扣书籍修改
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/discount/update",method = RequestMethod.POST)
    @ResponseBody
    public Object update(HttpServletRequest request,@RequestBody BookDiscount bean){
        try{
            if (bean.getId() == null){
                return JsonResult.getError("请提供id");
            }
            if (bean.getEnd_time() == null){
                return JsonResult.getOther("请选择截止时间");
            }
            if (bean.getDiscount_price() == null){
                return JsonResult.getOther("请输入折后价格");
            }
            if (bean.getStart_time().getTime()>bean.getEnd_time().getTime()){
                return JsonResult.getError("开始时间不得小于结束时间");
            }
            BookDiscount book =  bookDiscountService.update(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            jsonResult.setData(book);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除折扣书籍
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/discount/delete",method = RequestMethod.POST)
    @ResponseBody
    public Object delete(HttpServletRequest request,@RequestBody DelBookBean bean){
        try{
            if (bean.getIds() == null && bean.getIds().length<=0){
                return JsonResult.getError("请选择图书");
            }
            String book_ids = StringUtils.join(bean.getIds(),",");
            bookDiscountService.delete(book_ids);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(book_ids);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 折扣书籍启用停用
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/discount/enabled",method = RequestMethod.POST)
    @ResponseBody
    public Object enabled(HttpServletRequest request,@RequestBody DelBookBean bean){
        try{
            if (bean.getId() == null ){
                return JsonResult.getError("请选择图书");
            }
            bookDiscountService.updateEnabled(bean.getId(),bean.getEnabled());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 排序
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/discount/order",method = RequestMethod.POST)
    @ResponseBody
    public Object enabled(HttpServletRequest request,@RequestBody BookDiscount bean){
        try{
            if (bean.getId() == null ){
                return JsonResult.getError("请选择图书");
            }
            bookDiscountService.updateOrder(bean.getId(),bean.getOrder_weight());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
