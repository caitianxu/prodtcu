package com.cjsz.tech.dev.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.dev.beans.DelContentBean;
import com.cjsz.tech.dev.beans.FindConfContentBean;
import com.cjsz.tech.dev.domain.ConfContent;
import com.cjsz.tech.dev.domain.DeviceSetting;
import com.cjsz.tech.dev.service.ConfContentService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.system.utils.RequestContextUtil;
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
import java.util.Date;

/**
 * Created by Li Yi on 2016/12/23.
 */
@Controller
@RequestMapping("/admin/configContent")
public class ConfContentController {

    @Autowired
    private ConfContentService confContentService;

    @Autowired
    private UserService userService;

    /**
     * 获取所有导航分页列表
     *
     * @param request
     * @param pageCondition
     * @return
     */
    @RequestMapping(value = "/allList", method = RequestMethod.POST)
    @ResponseBody
    public Object allList(HttpServletRequest request, @RequestBody PageConditionBean pageCondition) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if(sysUser == null){
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "update_time");
            Object data = confContentService.pageQuery(sort, pageCondition);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取某配置的所有导航分页列表
     *
     * @param request
     * @param deviceSetting
     * @return
     */
    @RequestMapping(value = "/selectConfContents", method = RequestMethod.POST)
    @ResponseBody
    public Object selectConfContents(HttpServletRequest request, @RequestBody DeviceSetting deviceSetting) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if(sysUser == null){
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            FindConfContentBean findConfContentBean = new FindConfContentBean();
            findConfContentBean.setConf_id(deviceSetting.getConf_id());
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            Object data = confContentService.selectConfContents(sort, findConfContentBean);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增/修改导航
     *
     * @param request
     * @param confContent
     * @return
     */
    @RequestMapping(value = "/updateContents", method = RequestMethod.POST)
    @ResponseBody
    public Object updateContents(HttpServletRequest request, @RequestBody ConfContent confContent) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if(sysUser == null){
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            JsonResult result = JsonResult.getSuccess("");
            if (confContent.getConf_content_id() == null){
                confContent.setCreate_time(new Date());
                confContent.setUpdate_time(new Date());
                confContent = confContentService.saveContent(confContent);
                result.setMessage(Constants.ACTION_ADD);
            }
            else {
                confContent.setUpdate_time(new Date());
                confContent = confContentService.updateContent(confContent);
                result.setMessage(Constants.ACTION_UPDATE);
            }

            result.setData(confContent);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除导航
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/deleteContents", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteContents(HttpServletRequest request, @RequestBody DelContentBean bean){
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if(sysUser == null){
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Long[] content_ids = bean.getContent_ids();
            String video_ids_str = StringUtils.join(content_ids, ",");
            confContentService.deleteByIds(video_ids_str);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
