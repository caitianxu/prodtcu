package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.DelHelpBean;
import com.cjsz.tech.system.domain.HelpCenter;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.HelpCenterService;
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
 * Created by Administrator on 2017/8/7 .
 */
@Controller
public class HelpCenterController {

    @Autowired
    private HelpCenterService helpCenterService;

    @Autowired
    private UserService userService;


    /**
     * 获取所有帮助中心问题
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/helpCenter/getList")
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody PageConditionBean bean){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
            if (sysUser == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (bean.getPageNum() == null || bean.getPageSize() == null){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            Sort sort = new Sort(Sort.Direction.DESC,"create_time");
            PageList result = (PageList)helpCenterService.getList(bean,sort);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 创建新问题
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/helpCenter/newHelp",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Object newHelp(HttpServletRequest request, @RequestBody HelpCenter bean){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
            if (sysUser == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (StringUtils.isEmpty(bean.getContent()) || StringUtils.isEmpty(bean.getTitle())){
                JsonResult.getError("标题/内容不可为空");
            }
            bean.setCreate_user(sysUser.getUser_id());
            helpCenterService.saveHelp(bean);
            JsonResult jsonResult =JsonResult.getSuccess(Constants.ACTION_ADD);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 回复问题
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/helpCenter/replyHelp",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Object newReply(HttpServletRequest request, @RequestBody HelpCenter bean){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
            if (sysUser == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (StringUtils.isEmpty(bean.getReply_content()) ){
                return  JsonResult.getError("内容不可为空");
            }
            if (bean.getId() == null){
                return JsonResult.getError("请选择要回答的问题");
            }
            bean.setReply_user(sysUser.getUser_id());
            helpCenterService.saveReply(bean);
            JsonResult jsonResult =JsonResult.getSuccess(Constants.ACTION_ADD);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 删除问题
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/helpCenter/deleteHelp",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Object deleteHelp(HttpServletRequest request, @RequestBody DelHelpBean bean){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
            if (sysUser == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (bean.getIds().length <= 0){
                return JsonResult.getError("请选择要删除的问题");
            }
            String ids = StringUtils.join(bean.getIds(),",");
            helpCenterService.deleteHelp(ids);
            JsonResult jsonResult =JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
