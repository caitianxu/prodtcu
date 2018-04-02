package com.cjsz.tech.cms.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.cms.beans.DelArticleTempBean;
import com.cjsz.tech.cms.domain.ArticleTemp;
import com.cjsz.tech.cms.service.ArticleTempService;
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
 * Created by Administrator on 2016/11/22 0022.
 */
@Controller
public class ArticleTempController {


    @Autowired
    private ArticleTempService articleTempService;

    @Autowired
    private UserService userService;


    /**
     * 资讯模板 分页查询
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/articleTemp/json/list", method = RequestMethod.POST)
    @ResponseBody
    public Object articleTemp_list(@RequestBody PageConditionBean bean){
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "upload_time");
            //模板分页数据
            Object data = articleTempService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 资讯模板 全部模板
     * @return
     */
    @RequestMapping(value = "/admin/articleTemp/json/listAll", method = RequestMethod.POST)
    @ResponseBody
    public Object articleTemp_listAll(){
        try{
            //模板全部数据
            Object data = articleTempService.getAll();
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增/修改 资讯模板
     * @param request
     * @param articleTemp
     * @return
     */
    @RequestMapping(value = "/admin/articleTemp/json/updateArticleTemp", method = RequestMethod.POST)
    @ResponseBody
    public Object updateArticleTemp(HttpServletRequest request, @RequestBody ArticleTemp articleTemp){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            //赋值
            articleTemp.setTemp_url(articleTemp.getTemp_url());
            articleTemp.setUser_id(sysUser.getUser_id());
            articleTemp.setUpdate_time(new Date());
            if(articleTemp.getArticle_temp_id() == null){
                //增加
                articleTemp.setUpload_time(new Date());
                result.setMessage(Constants.ACTION_ADD);
                articleTemp = articleTempService.saveArticleTemp(articleTemp);
            } else {
                //修改
                result.setMessage(Constants.ACTION_UPDATE);
                articleTemp = articleTempService.updateArticleTemp(articleTemp);
            }
            result.setData(articleTemp);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除 资讯模板
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/article/json/deleteArticleTemps", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteArticleTemps(HttpServletRequest request, @RequestBody DelArticleTempBean bean){
       try{
           Integer[] temp_ids = bean.getArticle_temp_ids();
           String temp_ids_str = StringUtils.join(temp_ids, ",");
           //根据模板Ids删除模板
           articleTempService.deleteByTempIds(temp_ids_str);
           JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
           result.setData(new ArrayList());
           return result;
       }catch (Exception e){
           e.printStackTrace();
           return JsonResult.getException(Constants.EXCEPTION);
       }
    }

}
