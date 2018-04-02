package com.cjsz.tech.count.ctrl;

import com.cjsz.tech.book.service.BookIndexRecordService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.SearchBean;
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
 * 图书指数记录
 * Created by LuoLi on 2017/4/27 0027.
 */
@Controller
public class BookIndexRecordController {

    @Autowired
    private BookIndexRecordService bookIndexRecordService;

    @RequestMapping(value = "/admin/bookIndexRecord/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody SearchBean searchBean){
        try{
            Object obj = bookIndexRecordService.pageQuery( searchBean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
