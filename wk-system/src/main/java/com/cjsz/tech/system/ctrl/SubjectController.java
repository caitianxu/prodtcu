package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.system.beans.ArticleBean;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.SubjectOrgBean;
import com.cjsz.tech.system.beans.SubjectOrgListBean;
import com.cjsz.tech.system.domain.*;
import com.cjsz.tech.system.service.SubjectService;
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
 * Created by Administrator on 2017/9/25 0025.
 */
@Controller
public class SubjectController {

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private UserService userService;

    /**
     * 获取专题分页列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/getList" , method = RequestMethod.POST)
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody SubjectOrgBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            bean.setOrg_id(user.getOrg_id());
            Object result = subjectService.getList(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 专题新增、修改
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/addUpdate" , method = RequestMethod.POST)
    @ResponseBody
    public Object addUpdate(HttpServletRequest request , @RequestBody SubjectManage bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            JsonResult jsonResult = JsonResult.getSuccess("");
            if (StringUtils.isEmpty(bean.getSubject_name())){
                return JsonResult.getOther("请输入专题名称");
            }
            if (StringUtils.isEmpty(bean.getSubject_remark())){
                return JsonResult.getOther("请输入专题简介");
            }
            if (StringUtils.isEmpty(bean.getSubject_cover())){
                return JsonResult.getOther("请上传封面");
            }
            if (bean.getSubject_id() == null){
                //新增
                bean.setOrg_id(user.getOrg_id());
                subjectService.save(bean);
                jsonResult.setMessage(Constants.ACTION_ADD);
                jsonResult.setData(bean);
            }else{
                if (!bean.getOrg_id().equals(user.getOrg_id())){
                    return JsonResult.getError("无权限");
                }
                subjectService.update(bean);
                jsonResult.setMessage(Constants.ACTION_UPDATE);
                jsonResult.setData(bean);
            }
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改机构专题排序
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/orderSubject" , method = RequestMethod.POST)
    @ResponseBody
    public Object orderSubject(HttpServletRequest request, @RequestBody SubjectOrgRel bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getSubject_id() == null){
                return JsonResult.getError("请选择专题");
            }
            subjectService.orderSubject(bean.getSubject_id(),bean.getOrder_weight(),user.getOrg_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 删除专题
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/delete" , method = RequestMethod.POST)
    @ResponseBody
    public Object delete(HttpServletRequest request, @RequestBody SubjectOrgBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            for (int i = 0;i<bean.getIds().length;i++){
                Long subject_id = bean.getIds()[i];
                SubjectManage subjectManage = subjectService.findById(subject_id);
                if (!subjectManage.getOrg_id().equals( user.getOrg_id())){
                    return JsonResult.getError("无权限");
                }
                subjectService.deleteById(subject_id);
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 专题启用、停用
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/enabled" , method = RequestMethod.POST)
    @ResponseBody
    public Object enabled(HttpServletRequest request , @RequestBody SubjectManage bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (!user.getOrg_id().equals(bean.getOrg_id())){
                return JsonResult.getError("无权限");
            }
            if(bean.getEnabled() == 2){
                bean.setEnabled(1);
            }else{
                bean.setEnabled(2);
            }
            subjectService.enabled(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 专题是否显示
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/isShow" , method = RequestMethod.POST)
    @ResponseBody
    public Object isShow(HttpServletRequest request , @RequestBody SubjectManage bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getEnabled() != 1){
                return JsonResult.getError("专题未启用");
            }
            subjectService.updateIsShow(bean,user.getOrg_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 专题添加新闻资讯
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/addArticle" , method = RequestMethod.POST)
    @ResponseBody
    public Object addArticle(HttpServletRequest request, @RequestBody SubjectArticleRel bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getArticle_id() == null){
                return JsonResult.getError("请选择新闻资讯");
            }
            if (bean.getSubject_id() == null){
                return JsonResult.getError("请选择专题");
            }
            SubjectManage subjectManage = subjectService.findById(bean.getSubject_id());
            if (!subjectManage.getOrg_id().equals(user.getOrg_id())){
                return JsonResult.getError("无权限");
            }
            SubjectArticleRel rel = subjectService.getArticle(bean.getSubject_id(),bean.getArticle_id());
            if (rel != null){
                return JsonResult.getError("新闻资讯已存在");
            }
            subjectService.addArticle(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取专题新闻
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/getArticle" , method = RequestMethod.POST)
    @ResponseBody
    public Object getArticle(HttpServletRequest request, @RequestBody SubjectOrgBean bean){
        try{
            if (bean.getSubject_id() == null){
                return JsonResult.getError("请选择专题");
            }
            Object result = subjectService.selectArticleById(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 更改专题新闻排序
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/orderArticle", method = RequestMethod.POST)
    @ResponseBody
    public Object orderArticle (HttpServletRequest request,@RequestBody SubjectArticleRel bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getRel_id() == null){
                return JsonResult.getError("请选择资讯");
            }
            SubjectManage subjectManage = subjectService.findById(bean.getSubject_id());
            if (!subjectManage.getOrg_id().equals(user.getOrg_id())){
                return JsonResult.getError("无权限");
            }
            subjectService.orderArticle(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除专题新闻
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/deleteArticle" ,method = RequestMethod.POST)
    @ResponseBody
    public Object deleteArticle(HttpServletRequest request,@RequestBody SubjectArticleRel bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getSubject_id() == null){
                return JsonResult.getError("请选择专题");
            }
            if (bean.getArticle_id() == null){
                return JsonResult.getError("请选择新闻");
            }
            SubjectManage subjectManage = subjectService.findById(bean.getSubject_id());
            if (!subjectManage.getOrg_id().equals(user.getOrg_id())){
                return JsonResult.getError("无权限");
            }
            subjectService.deleteArticle(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取专题分配机构列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/orgList" , method = RequestMethod.POST)
    @ResponseBody
    public Object orgList(HttpServletRequest request , @RequestBody SubjectOrgListBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (user.getOrg_id() != 1) {
                return JsonResult.getError("无权限");
            }
            Sort sort = new Sort(Sort.Direction.DESC, "is_delete").and(new Sort(Sort.Direction.ASC, "org_id"));
            Object result = subjectService.getOrgList(bean,sort);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }



    /**
     * 专题添加机构
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/addOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object addOrg(HttpServletRequest request, @RequestBody SubjectOrgListBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (user.getOrg_id() != 1){
                return JsonResult.getError("无权限");
            }
            Long org_id = bean.getOrg_id();
            subjectService.addOrg(bean.getSubject_id(), org_id);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 专题移除机构
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/removeOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object removeOrg(HttpServletRequest request, @RequestBody SubjectOrgListBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (user.getOrg_id() != 1){
                return JsonResult.getError("无权限");
            }
            if(bean.getOrg_id() == 1){
                return JsonResult.getError("不能移除长江科技的专题！");
            }
            subjectService.removeOrg(bean.getSubject_id(), bean.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 专题添加书籍
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/addBook" ,method = RequestMethod.POST)
    @ResponseBody
    public Object addBook(HttpServletRequest request, @RequestBody SubjectBooksRel bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getBook_id() == null){
                return JsonResult.getError("请选择图书");
            }
            if (bean.getSubject_id() == null){
                return JsonResult.getError("请选择专题");
            }
            SubjectManage subjectManage = subjectService.findById(bean.getSubject_id());
            if (!subjectManage.getOrg_id().equals(user.getOrg_id())){
                return JsonResult.getError("无权限");
            }
            SubjectBooksRel rel = subjectService.findBook(bean.getBook_id(),bean.getSubject_id());
            if (rel != null){
                return JsonResult.getError("图书已存在");
            }
            subjectService.addBook(bean);
            JsonResult jsonResult =JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取专题书籍列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/getBookList" ,method = RequestMethod.POST)
    @ResponseBody
    public Object getBookList(HttpServletRequest request, @RequestBody SubjectOrgBean bean){
        try{
            if (bean.getSubject_id() == null){
                return JsonResult.getError("请选择专题");
            }
            Object obj = subjectService.getBookList(bean);
            JsonResult jsonResult =JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(obj);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 专题移除图书
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/removeBook" , method = RequestMethod.POST)
    @ResponseBody
    public Object removeBook(HttpServletRequest request ,@RequestBody SubjectBooksRel bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getBook_id() == null){
                return JsonResult.getError("请选择图书");
            }
            if (bean.getSubject_id() == null){
                return JsonResult.getError("请选择专题");
            }
            SubjectManage subjectManage = subjectService.findById(bean.getSubject_id());
            if (!subjectManage.getOrg_id().equals(user.getOrg_id())){
                return JsonResult.getError("无权限");
            }
            subjectService.removeBook(bean.getSubject_id(),bean.getBook_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 更改专题书籍排序
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/subject/orderBooks", method = RequestMethod.POST)
    @ResponseBody
    public Object orderBooks (HttpServletRequest request,@RequestBody SubjectBooksRel bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getRel_id() == null){
                return JsonResult.getError("请选择图书");
            }
            SubjectManage subjectManage = subjectService.findById(bean.getSubject_id());
            if (!subjectManage.getOrg_id().equals(user.getOrg_id())){
                return JsonResult.getError("无权限");
            }
            subjectService.orderBooks(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
