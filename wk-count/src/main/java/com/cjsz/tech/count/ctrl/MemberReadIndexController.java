package com.cjsz.tech.count.ctrl;

import com.cjsz.tech.meb.service.MemberReadIndexService;
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
 * 用户阅读指数
 * Created by LuoLi on 2017/4/27 0027.
 */
@Controller
public class MemberReadIndexController {

    @Autowired
    private MemberReadIndexService memberReadIndexService;

    @RequestMapping(value = "/admin/memberReadIndex/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody SearchBean searchBean){
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            Object obj = memberReadIndexService.pageQuery(sort, searchBean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
