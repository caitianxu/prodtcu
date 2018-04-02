package com.cjsz.tech.journal.ctrl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.journal.beans.DelNewspaperBean;
import com.cjsz.tech.journal.beans.FindNewspaperBean;
import com.cjsz.tech.journal.beans.NewspaperBean;
import com.cjsz.tech.journal.domain.Newspaper;
import com.cjsz.tech.journal.service.NewspaperCatService;
import com.cjsz.tech.journal.service.NewspaperService;
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
import java.util.Map;

@Controller
@RequestMapping("/admin/newspaper")
public class NewsPaperController {

    @Autowired
    private NewspaperService newspaperService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private UserService userService;

    @Autowired
    private NewspaperCatService newspaperCatService;

    /**
     * 报纸页面 分页查询
     * @param request
     * @param bean  {pageNum:1,pageSize:10,catId:可选,searchText:可选}
     * @return
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object listNewsPaper(HttpServletRequest request,@RequestBody FindNewspaperBean bean) {
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight");
            Object data = newspaperService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 新增、修改报纸
     *
     * @param request
     * @param newspaper
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public Object save(HttpServletRequest request, @RequestBody Newspaper newspaper) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            Long newspaper_cat_id=newspaperCatService.getCatId(newspaper.getNewspaper_cat_id(),sysUser.getOrg_id());
            if (newspaper_cat_id==null){
                return JsonResult.getOther("不是自建的不能进行操作！");
            }
            if (newspaper.getNewspaper_id() == null){
                newspaper.setUser_id(sysUser.getUser_id());
                newspaper.setOrg_id(sysUser.getOrg_id());
                //图片处理，压缩图片
                String cover_url_small = baseService.getImgScale(newspaper.getPaper_img(), "small", 0.5);
                newspaper.setPaper_img_small(cover_url_small);
                newspaper.setIs_delete(2);
                newspaper = newspaperService.saveNewspaper(newspaper);
                result.setMessage(Constants.ACTION_ADD);
            }
            else {

                Newspaper org_audio = newspaperService.selectById(newspaper.getNewspaper_id());
                /*if(!video.getCover_url().equals(org_audio.getCover_url())){
                    //图片处理，压缩图片
                    String cover_url_small = baseService.getImgScale(video.getCover_url(), "small", 0.5);
                    video.setCover_url_small(cover_url_small);
                }*/
                if(StringUtils.isNotEmpty(newspaper.getPaper_img())) {
                    //图片处理，压缩图片
                    String cover_url_small = baseService.getImgScale(newspaper.getPaper_img(), "small", 0.5);
                    newspaper.setPaper_img_small(cover_url_small);
                }
                newspaper.setIs_delete(2);
                newspaper.setUser_id(sysUser.getUser_id());
                newspaper = newspaperService.updateNewspaper(newspaper);
                result.setMessage(Constants.ACTION_UPDATE);
            }

            result.setData(newspaper);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }




    /**
     * 删除报纸
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/del")
    @ResponseBody
    public Object delete(HttpServletRequest request, @RequestBody DelNewspaperBean bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            //查询是自建 还是分配
            for (Long newspaper_id:bean.getNewspaper_ids()) {
                Long cat_id = newspaperService.getNewsCatId(newspaper_id);
                Long newspaper_cat_id=newspaperCatService.getCatId(cat_id,user.getOrg_id());
                if (newspaper_cat_id == null){
                    return JsonResult.getOther("不是自建的不能进行操作！");
                }
                Long[] newspaper_ids = bean.getNewspaper_ids();
                String newspaper_ids_str = StringUtils.join(newspaper_ids, ",");
                newspaperService.deleteByIds(newspaper_ids_str);
                result.setMessage(Constants.ACTION_DELETE);
                result.setData(new ArrayList());
                return result;
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 报纸推荐
     * @param request
     * @param paper
     * @return
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object updateStatus(HttpServletRequest request, @RequestBody Newspaper paper){
        try{
            newspaperService.updateStatus(paper.getIs_recommend(), paper.getNewspaper_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(paper);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改报纸排序
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/updateOrder" , method = RequestMethod.POST)
    @ResponseBody
    public Object updateOrder(HttpServletRequest request, @RequestBody Newspaper bean){
        try{
            Long newspaper_id = bean.getNewspaper_id();
            if (newspaper_id == null){
                return JsonResult.getError("请选择报纸");
            }
            Long order_weight = bean.getOrder_weight();
            if (order_weight == null){
                order_weight = System.currentTimeMillis();
            }
            newspaperService.updateOrder(newspaper_id,order_weight);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}