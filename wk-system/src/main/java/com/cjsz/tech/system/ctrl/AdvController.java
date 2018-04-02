package com.cjsz.tech.system.ctrl;

import com.alibaba.fastjson.JSONReader;
import com.cjsz.tech.system.beans.AdvOrgListBean;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.DelAdvBean;
import com.cjsz.tech.system.conditions.AdvCondition;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.AdvService;
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
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/22 0022.
 */
@Controller
public class AdvController {

    @Autowired
    private AdvService advService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private UserService userService;

    /**
     * 分页列表
     * @param request
     * @param advCondition
     * @return
     */
    @RequestMapping(value = "/admin/adv/json/getAdvList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getAdvList(HttpServletRequest request, @RequestBody AdvCondition advCondition){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            Sort sort = new Sort(Sort.Direction.DESC, "org_order_weight").and(new Sort(Sort.Direction.ASC, "enabled")).and(new Sort(Sort.Direction.DESC, "create_time"));
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            Object obj = advService.pageQuery(sort,  advCondition);
            result.setData(obj);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增、修改
     * @param request
     * @param adv
     * @return
     */
    @RequestMapping(value = "/admin/adv/json/updateAdv", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updateAdv(HttpServletRequest request, @RequestBody Adv adv){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            if(adv.getAdv_id() == null){
                //新增
                //图片处理，压缩图片
                String adv_img_small = baseService.getImgScale(adv.getAdv_img(), "small", 0.5);
                adv.setAdv_img_small(adv_img_small);

                adv.setOrg_id(sysUser.getOrg_id());
                if(sysUser.getOrg_id() == 1) {
                    adv.setEnabled(2);//不启用
                }else{
                    adv.setEnabled(1);
                }
                adv.setCreate_time(new Date());
                adv.setUpdate_time(new Date());
                adv.setIs_delete(2);
                advService.saveAdv(adv);

                //长江新增广告时默认分配给所有机构
                if (sysUser.getOrg_id() == 1) {
                    advService.saveRelByAllOrg(adv);
                }
                result.setMessage(Constants.ACTION_ADD);
            }else{
                //修改
                if(!sysUser.getOrg_id().equals(adv.getOrg_id())  && sysUser.getOrg_id() !=1){
                    return JsonResult.getError("您不具有操作该数据的权限");
                }
                Adv org_adv = advService.selectAdvById(adv.getAdv_id());
                if(!adv.getAdv_img().equals(org_adv.getAdv_img())){
                    //图片处理，压缩图片
                    String adv_img_small = baseService.getImgScale(adv.getAdv_img(), "small", 0.5);
                    adv.setAdv_img_small(adv_img_small);
                }
                adv.setIs_delete(2);
                adv.setUpdate_time(new Date());
                advService.updateAdv(adv);
                result.setMessage(Constants.ACTION_UPDATE);
            }
            result.setData(adv);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 启用、停用
     * @param request
     * @param adv
     * @return
     */
    @RequestMapping(value = "/admin/adv/json/updateEnable", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updateEnable(HttpServletRequest request, @RequestBody Adv adv){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if(!sysUser.getOrg_id().equals(adv.getOrg_id())){
                return JsonResult.getError("您不具有操作该数据的权限");
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            if(adv.getEnabled() == 2){
                adv.setEnabled(1);
            }else{
                adv.setEnabled(2);
            }
            adv.setUpdate_time(new Date());
            advService.updateAdv(adv);
            result.setData(adv);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除
     * @param request
     * @param delAdvBean
     * @return
     */
    @RequestMapping(value = "/admin/adv/json/deleteAdvs", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object deleteAdvs(HttpServletRequest request, @RequestBody DelAdvBean delAdvBean){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            Long[] advIds = delAdvBean.getAdv_ids();
            String advIdsStr = StringUtils.join(advIds, ",");

            List<Adv> advList = advService.selectAdvByIds(advIdsStr);
            Long user_org_id = sysUser.getOrg_id();
            for(Adv adv : advList){
                if(!adv.getOrg_id().equals(user_org_id)){
                    return JsonResult.getError("数据中包含总部数据，不可删除");
                }
            }
            advService.deleteByIds(advIdsStr);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取广告分配机构列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/adv/json/orgList" , method = RequestMethod.POST)
    @ResponseBody
    public Object orgList(HttpServletRequest request , @RequestBody AdvOrgListBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            Sort sort = new Sort(Sort.Direction.DESC, "is_delete").and(new Sort(Sort.Direction.ASC, "org_id"));
            Object result = advService.getOrgList(bean,sort);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }



    /**
     * 广告添加机构
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/adv/json/addOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object addOrg(HttpServletRequest request, @RequestBody AdvOrgListBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (user.getOrg_id() != 1){
                return JsonResult.getError("无权限");
            }
            Long org_id = bean.getOrg_id();
            advService.addOrg(bean.getAdv_id(), org_id);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 广告移除机构
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/adv/json/removeOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object removeOrg(HttpServletRequest request, @RequestBody AdvOrgListBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (user.getOrg_id() != 1){
                return JsonResult.getError("无权限");
            }
            if(bean.getOrg_id() == 1){
                return JsonResult.getError("不能移除长江科技的广告！");
            }
            advService.removeOrg(bean.getAdv_id(), bean.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     *广告是否显示
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/adv/json/updateShow", method = RequestMethod.POST)
    @ResponseBody
    public Object updateShow(HttpServletRequest request, @RequestBody Adv bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getEnabled() == 2) {
                return JsonResult.getError("广告未启用！");
            }
            advService.updateShow(bean,user.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(bean);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 广告各机构修改排序
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/adv/json/updateOrder", method = RequestMethod.POST)
    @ResponseBody
    public Object updateOrder(HttpServletRequest request, @RequestBody Adv bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            advService.updateOrder(bean.getAdv_id(),bean.getOrg_order_weight(),user.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(bean);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
