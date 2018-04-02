package com.cjsz.tech.count.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.count.beans.DelOrgsSearchBean;
import com.cjsz.tech.count.beans.SearchCountOrgBean;
import com.cjsz.tech.count.domain.SearchCount;
import com.cjsz.tech.count.service.SearchCountService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * 项目搜索记录统计
 * Created by LuoLi on 2017/3/23 0023.
 */
@Controller
public class SearchCountController {

    @Autowired
    private SearchCountService searchCountService;

    @Autowired
    private UserService userService;

    /**
     * 项目搜索记录————分页
     * @return
     */
    @RequestMapping(value = "/admin/searchCount/json/searchList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object searchList(HttpServletRequest request, @RequestBody SearchCountOrgBean bean){
        try{
            if (bean.getOrg_id() == null){
                bean.setOrg_id(1L);
            }
            Sort sort = new Sort(Sort.Direction.ASC, "org_id").and(new Sort(Sort.Direction.DESC, "order_weight"));
            Object data = searchCountService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 项目搜索记录————新增、修改
     * @return
     */
    @RequestMapping(value = "/admin/searchCount/json/updateSearchCount", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updateSearchCount(HttpServletRequest request, @RequestBody SearchCount searchCount){
        try{
            JsonResult result = JsonResult.getSuccess("");

            if (searchCount.getOrg_id() == null){
                return JsonResult.getError("请提供机构信息");
            }
            if(searchCount.getSearch_id() == null){
                //新增
                SearchCount njswSearchCount = searchCountService.selectByNameAndOrgId(searchCount.getName(), searchCount.getOrg_id());
                if(njswSearchCount != null){
                    return JsonResult.getError("搜索词汇重复");
                }
                if(searchCount.getSearch_count() == null){
                    searchCount.setSearch_count(0L);
                }
                searchCount.setStatus(2);//状态（1：启用；2：停用）
                searchCountService.saveSearchCount(searchCount);
                result.setMessage(Constants.ACTION_ADD);
            }else{
                //修改
                SearchCount njswSearchCount = searchCountService.selectById(searchCount.getSearch_id());
                if(!njswSearchCount.getOrg_id().equals(searchCount.getOrg_id()) || !njswSearchCount.getName().equals(searchCount.getName())){
                    SearchCount njswSearchCount1 = searchCountService.selectByNameAndOrgId(searchCount.getName(), searchCount.getOrg_id());
                    if(njswSearchCount1 != null){
                        return JsonResult.getError("搜索词汇重复");
                    }
                }
                searchCountService.updateSearchCount(searchCount);
                result.setMessage(Constants.ACTION_UPDATE);
            }
            result.setData(searchCount);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 项目搜索记录————修改状态
     * @return
     */
    @RequestMapping(value = "/admin/searchCount/json/updateStatus", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updateStatus(HttpServletRequest request, @RequestBody SearchCount searchCount){
        try{
            JsonResult result = JsonResult.getSuccess("");
            //状态（1：启用；2：停用）
            searchCountService.updateSearchCount(searchCount);
            result.setMessage(Constants.ACTION_UPDATE);
            result.setData(searchCount);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 项目搜索记录————删除
     * @return
     */
    @RequestMapping(value = "/admin/searchCount/json/delSearchCounts", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object delSearchCounts(HttpServletRequest request, @RequestBody DelOrgsSearchBean bean){
        try{
            searchCountService.delSearchCounts(StringUtils.join(bean.getIds(), ","));
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList<>());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 长江中文网关键词同步
     * @param request
     * @return
     */
    @RequestMapping(value = "/admin/cjzww/searchList",method = RequestMethod.POST)
    @ResponseBody
    public Object searchList(HttpServletRequest request){
        try{
            searchCountService.saveKeyByCJZWW();
            return "保存成功";
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
