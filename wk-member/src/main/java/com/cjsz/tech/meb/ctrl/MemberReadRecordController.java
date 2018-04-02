package com.cjsz.tech.meb.ctrl;

import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.meb.service.MemberReadRecordService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.SearchBean;
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

/**
 * 用户阅读记录
 *
 */
@Controller
public class MemberReadRecordController {

    @Autowired
    private MemberReadRecordService memberReadRecordService;

    @Autowired
    private UserService userService;


    /**
     * 图书会员阅读记录统计
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/memberReadRecord/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody SearchBean bean){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
            if(sysUser == null){
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (bean.getPageNum() == null || bean.getPageSize() == null){
                JsonResult.getError(Constants.EXCEPTION);
            }
            Long org_id = sysUser.getOrg_id();
            PageList pageList = (PageList)memberReadRecordService.pageQuery( bean,org_id);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(pageList);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 图书点击详情统计记录
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/memberReadRecord/clickList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object clickList(HttpServletRequest request, @RequestBody SearchBean bean){
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
            if (sysUser == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (bean.getPageNum() == null || bean.getPageSize() == null){
                JsonResult.getError(Constants.EXCEPTION);
            }
            Long org_id = sysUser.getOrg_id();
            Sort sort = new Sort(Sort.Direction.DESC, "book_id");
            PageList pageList = (PageList)memberReadRecordService.clickList(sort, bean,org_id);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(pageList);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return  JsonResult.getException(Constants.EXCEPTION);
        }
    }


}
