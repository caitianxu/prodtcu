package com.cjsz.tech.cms.ctrl;

import com.cjsz.tech.cms.beans.DelActivityBean;
import com.cjsz.tech.cms.beans.FindActivityBean;
import com.cjsz.tech.cms.domain.Activity;
import com.cjsz.tech.cms.service.ActivityService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.BaseService;
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
import java.util.List;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
@Controller
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private UserService userService;


    /**
     * 活动页面 分页查询
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/activity/json/list", method = RequestMethod.POST)
    @ResponseBody
    public Object activity_list(@RequestBody FindActivityBean bean){
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            Object data = activityService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增/修改 活动
     * @param request
     * @param activity
     * @return
     */
    @RequestMapping(value = "/admin/activity/json/updateActivity", method = RequestMethod.POST)
    @ResponseBody
    public Object updateActivity(HttpServletRequest request, @RequestBody Activity activity){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            if (activity.getStart_time() == null || activity.getEnd_time() == null){
                return JsonResult.getError("请输入时间");
            }
            if(activity.getActivity_id()==null){
                //活动表数据添加
                //图片处理，压缩图片
                String cover_url_small = baseService.getImgScale(activity.getCover_url(), "small", 0.5);
                activity.setCover_url_small(cover_url_small);

                activity = activityService.saveActivity(activity, user.getUser_id(), user.getOrg_id());
                result.setMessage(Constants.ACTION_ADD);
            }else{
                if(!user.getOrg_id().equals(activity.getOrg_id())){
                    return JsonResult.getError("没有修改权限！");
                }
                //活动表数据修改
                Activity act = activityService.selectById(activity.getActivity_id());
                if(act == null){
                    return JsonResult.getError(Constants.NEW_NOT_EXIST);
                }
                if(!activity.getCover_url().equals(act.getCover_url())){
                    //图片处理，压缩图片
                    String cover_url_small = baseService.getImgScale(activity.getCover_url(), "small", 0.5);
                    activity.setCover_url_small(cover_url_small);
                }
                activity = activityService.updateActivity(activity, user.getUser_id());
                result.setMessage(Constants.ACTION_UPDATE);
            }

            result.setData(activity);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 活动详情 启用/停用
     * @param request
     * @param activity
     * @return
     */
    @RequestMapping(value = "/admin/activity/json/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object updateStatus(HttpServletRequest request, @RequestBody Activity activity){
        try{
            //1: 启用；2:停用
            SysUser user = userService.findByToken(request.getHeader("token"));
            activity = activityService.updateActivity(activity, user.getUser_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(activity);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除活动
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/activity/json/deleteActivities", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteActivities(HttpServletRequest request, @RequestBody DelActivityBean bean){
       try{
           SysUser user = userService.findByToken(request.getHeader("token"));
           Long[] activity_ids = bean.getActivity_ids();
           String activity_ids_str = StringUtils.join(activity_ids, ",");
           if(user.getOrg_id() != 1){
               List<Activity> activities = activityService.selectByOrgAndActivityIds(1L, activity_ids_str);
               if(activities.size() > 0){
                   return JsonResult.getException("总部活动不可删除！");
               }
           }
           activityService.deleteByActivityIds(activity_ids_str);
           JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
           result.setData(new ArrayList());
           return result;
       }catch (Exception e){
           e.printStackTrace();
           return JsonResult.getException(Constants.EXCEPTION);
       }
    }

}
