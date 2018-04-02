package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.conditions.AppNavCondition;
import com.cjsz.tech.system.domain.AppNav;
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
public class AppNavController {

    @Autowired
    private AppNavService appNavService;


    /**
     * 导航列表 分页数据
     * @param request
     * @param condition
     * @return
     */
    @RequestMapping(value = "/admin/appNav/json/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object appNav_list(HttpServletRequest request, @RequestBody AppNavCondition condition){
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight");
            Object data = appNavService.pageQuery(sort, condition);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 导航列表 不分页
     * @param request
     * @param condition
     * @return
     */
    @RequestMapping(value = "/admin/appNav/json/appNavList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object get_appNav_list(HttpServletRequest request, @RequestBody AppNavCondition condition){
        try{
            Object data = appNavService.getListByOrgIdAndSearch(condition.getOrg_id(), condition.getSearchText());
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增、修改  导航
     * @param request
     * @param appNav
     * @return
     */
    @RequestMapping(value = "/admin/appNav/json/update_appNav", method = {RequestMethod.POST})
    @ResponseBody
    public Object update_appNav(HttpServletRequest request, @RequestBody AppNav appNav){
        try{
            JsonResult result = JsonResult.getSuccess("");
            if(appNav.getNav_id() == null){
                //新增
                appNav.setCreate_time(new Date());
                appNav.setEnabled(2);//默认不显示
                appNavService.saveAppNav(appNav);
                result.setMessage(Constants.ACTION_ADD);
            }else{
                //修改
                appNavService.updateAppNav(appNav);
                result.setMessage(Constants.ACTION_UPDATE);
            }
            result.setData(appNav);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改启用停用
     * @param request
     * @param appNav
     * @return
     */
    @RequestMapping(value = "/admin/appNav/json/update_enabled", method = {RequestMethod.POST})
    @ResponseBody
    public Object update_enabled(HttpServletRequest request, @RequestBody AppNav appNav){
        try{
            JsonResult result = JsonResult.getSuccess("");
            if(appNav.getEnabled() == 1){
                appNav.setEnabled(2);
            }else{
                appNav.setEnabled(1);
            }
            appNavService.updateAppNav(appNav);
            result.setData(appNav);
            result.setMessage(Constants.ACTION_UPDATE);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除导航
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/admin/appNav/json/delete_appNavs", method = {RequestMethod.POST})
    @ResponseBody
    public Object delete_appNavs(HttpServletRequest request, @RequestBody Map map){
        try{
            String nav_ids_str = (String) map.get("nav_ids");
            appNavService.deleteByNavIds(nav_ids_str);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
