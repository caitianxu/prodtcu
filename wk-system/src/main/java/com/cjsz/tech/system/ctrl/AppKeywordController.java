package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.conditions.AppNavCondition;
import com.cjsz.tech.system.domain.AppKeyword;
import com.cjsz.tech.system.domain.AppNav;
import com.cjsz.tech.system.service.AppKeywordService;
import com.cjsz.tech.system.service.AppNavService;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
@Controller
public class AppKeywordController {

    @Autowired
    private AppKeywordService appKeywordService;

    /**
     * 关键词列表
     * @param request
     * @param pageConditionBean
     * @return
     */
    @RequestMapping(value = "/admin/appKeyword/json/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object appKeyword_list(HttpServletRequest request, @RequestBody PageConditionBean pageConditionBean){
        try{
            List<AppKeyword> data = appKeywordService.getAppKeywordBySearch(pageConditionBean.getSearchText());
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增、修改  关键词
     * @param request
     * @param appKeyword
     * @return
     */
    @RequestMapping(value = "/admin/appKeyword/json/update_appKeyword", method = {RequestMethod.POST})
    @ResponseBody
    public Object update_appKeyword(HttpServletRequest request, @RequestBody AppKeyword appKeyword){
        try{
            JsonResult result = JsonResult.getSuccess("");
            if(appKeyword.getKeyword_id() == null){
                //新增
                appKeyword.setCreate_time(new Date());
                appKeywordService.saveAppKeyword(appKeyword);
                result.setMessage(Constants.ACTION_ADD);
            }else{
                //修改
                appKeywordService.updateAppKeyword(appKeyword);
                result.setMessage(Constants.ACTION_UPDATE);
            }
            result.setData(appKeyword);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除关键词
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/admin/appKeyword/json/delete_appKeywords", method = {RequestMethod.POST})
    @ResponseBody
    public Object delete_appKeywords(HttpServletRequest request, @RequestBody Map map){
        try{
            String keyword_ids_str = (String) map.get("keyword_ids");
            appKeywordService.deleteByKeywordIds(keyword_ids_str);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
