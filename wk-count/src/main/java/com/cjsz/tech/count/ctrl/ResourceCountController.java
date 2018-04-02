package com.cjsz.tech.count.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.count.service.ResourceCountService;
import com.cjsz.tech.dev.beans.IndexCountNewBean;
import com.cjsz.tech.dev.beans.OrgCountBean;
import com.cjsz.tech.dev.beans.ResCountBean;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.system.utils.CacheUtil;
import com.cjsz.tech.utils.DateUtils;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 统计管理
 * Created by Administrator on 2016/10/24.
 */
@Controller
@RequestMapping("/admin/resourceCount")
public class ResourceCountController {

    @Autowired
    ResourceCountService resourceCountService;

    @Autowired
    UserService userService;

    /**
     * 图书统计
     *
     * @return
     */
    @RequestMapping("/getBookCountList")
    @ResponseBody
    public Object getBookCountList(HttpServletRequest request, @RequestBody PageConditionBean pageCondition) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Order org = new Order(Direction.ASC, "r.org_id");
            Order book = new Order(Direction.ASC, "r.resource_id");
            Order type = new Order(Direction.ASC, "r.operation_type");
            Sort sort = new Sort(org, book, type);
            pageCondition.setSearchText("book");
            PageList result = (PageList) resourceCountService.pageQuery(sort, pageCondition);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 资讯统计
     *
     * @return
     */
    @RequestMapping("/getNewsCountList")
    @ResponseBody
    public Object getNewsCountList(HttpServletRequest request, @RequestBody PageConditionBean pageCondition) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Order org = new Order(Direction.ASC, "r.org_id");
            Order news = new Order(Direction.ASC, "r.resource_id");
            Sort sort = new Sort(org, news);
            pageCondition.setSearchText("news");
            PageList result = (PageList) resourceCountService.pageQuery(sort, pageCondition);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 机构统计
     *
     * @return
     */
    @RequestMapping("/getOrgCount")
    @ResponseBody
    public Object getOrgCount(HttpServletRequest request) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            String year = DateUtils.getYear(new Date());
            //机构访问总量统计
            List<ResCountBean> orgCount = resourceCountService.getOrgCount(year, null);
            //机构图书访问总量统计
            List<ResCountBean> bookCount = resourceCountService.getOrgCount(year, Constants.BOOK_TYPE);
            //机构新闻访问总量统计
            List<ResCountBean> newsCount = resourceCountService.getOrgCount(year, Constants.NEWS_TYPE);
            OrgCountBean result = new OrgCountBean();
            result.setOrgCount(orgCount);
            result.setBookCount(bookCount);
            result.setNewsCount(newsCount);
            result.setYear(year);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 设备统计
     *
     * @return
     */
    @RequestMapping("/getDeviceCount")
    @ResponseBody
    public Object getDeviceCount(HttpServletRequest request) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            String year = DateUtils.getYear(new Date());
            //设备访问总量统计
            List<ResCountBean> orgCount = resourceCountService.getDeviceCount(year);
            OrgCountBean result = new OrgCountBean();
            result.setOrgCount(orgCount);
            result.setYear(year);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 首页统计
     *
     * @return
     */
    @RequestMapping(value = "/indexCount", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object indexCount(HttpServletRequest request) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        JsonResult jsonResult = JsonResult.getSuccess("");
        try {
            Object index = CacheUtil.get("index");
            if (index == null) {
                IndexCountNewBean result = resourceCountService.indexCount(sysUser.getOrg_id());
                jsonResult.setMessage(Constants.LOAD_SUCCESS);
                jsonResult.setData(result);
                return jsonResult;
            } else {
                jsonResult.setMessage(Constants.LOAD_SUCCESS);
                jsonResult.setData(index);
                return jsonResult;
            }
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
