package com.cjsz.tech.book.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.BookTagsBean;
import com.cjsz.tech.book.beans.TagBooksCount;
import com.cjsz.tech.book.beans.UpdateBookTagRelBean;
import com.cjsz.tech.book.domain.BookTag;
import com.cjsz.tech.book.domain.BookTagRel;
import com.cjsz.tech.book.service.BookTagRelService;
import com.cjsz.tech.book.service.BookTagService;
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
import java.util.*;

/**
 * 标签管理
 * Created by shiaihua on 16/12/17.
 */
@Controller
public class BookTagController {

    @Autowired
    private BookTagService bookTagService;

    @Autowired
    private BookTagRelService bookTagRelService;

    @Autowired
    private UserService userService;
//
//    /**
//     * 图书标签分页列表
//     * @param httpServletRequest
//     * @param bean
//     * @return
//     */
//    @RequestMapping(value = "/admin/bookTag/json/getList", method = RequestMethod.POST)
//    @ResponseBody
//    public Object getList(HttpServletRequest httpServletRequest, @RequestBody PageConditionBean bean){
//        try{
//            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time")).and(new Sort(Sort.Direction.DESC, "tag_id"));
//            Object obj = bookTagService.pageQuery(sort, bean);
//            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
//            result.setData(obj);
//            return result;
//        }catch (Exception e){
//            e.printStackTrace();
//            return JsonResult.getException(Constants.EXCEPTION);
//        }
//    }

    /**
     * 图书标签树形结构查询
     * @param request
     * @return
     */
    @RequestMapping(value = "/admin/bookTag/tagTree", method = RequestMethod.POST)
    @ResponseBody
    public Object tagTree(HttpServletRequest request){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
            if(sysUser == null){
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            List<BookTag> allList = bookTagService.getAllTags();
            //转换为树形结构
            List<BookTag> listTree = bookTagService.getTree(allList);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(listTree);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 图书标签全部
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/admin/bookTag/tagBookCount", method = RequestMethod.POST)
    @ResponseBody
    public Object tagBookCount(HttpServletRequest httpServletRequest){
        try{
            Map<String,Object> count  = new HashMap<String,Object>();
            List<TagBooksCount> books = bookTagService.selectCount();
            for (TagBooksCount tagBooksCount:books){
                count.put(tagBooksCount.getTag_id().toString(),tagBooksCount.getBook_count());
            }
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(count);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 新增修改图书标签
     * @param request
     * @param bookTag
     * @return
     */
    @RequestMapping(value = "/admin/bookTag/saveUpdateTag", method = RequestMethod.POST)
    @ResponseBody
    public Object saveUpdateTag(HttpServletRequest request, @RequestBody BookTag bookTag){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            //查询上级标签是否分配图书，如果有，则不允许建立下级标签
            List<BookTagRel> rels = bookTagRelService.selectById(bookTag.getTag_pid());
            if (rels.size()>0){
                return JsonResult.getError("不可在已分配图书的标签下建立下级标签");
            }
            if (StringUtils.isEmpty(bookTag.getTag_code())){
                return JsonResult.getError("请输入标签code");
            }
            if (StringUtils.isEmpty(bookTag.getTag_name())) {
                return JsonResult.getError("请输入标签名称");
            }
            if(bookTag.getTag_id() == null){
                //新增标签
                List<BookTag> listByCode = bookTagService.findByCode(bookTag.getTag_code());
                if (listByCode.size()>0){
                    return JsonResult.getError("标签编码已存在");
                }


                bookTag.setCreate_time(new Date());
                bookTag.setUpdate_time(new Date());
                bookTagService.saveTag(bookTag);
            }else{
                //修改标签
                List<BookTag> listByCode = bookTagService.selectByCodeAndId(bookTag.getTag_code(),bookTag.getTag_id());
                if (listByCode.size()>0){
                    return JsonResult.getError("标签编码已存在");
                }

                bookTag.setUpdate_time(new Date());
                bookTagService.updateTag(bookTag);
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            jsonResult.setData(bookTag);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }



    /**
     * 删除图书标签
     * @param httpServletRequest
     * @param tagsBean
     * @return
     */
    @RequestMapping(value = "/admin/bookTag/json/deleteBookTag", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteBookTag(HttpServletRequest httpServletRequest, @RequestBody BookTagsBean tagsBean){
        try{
            SysUser sysUser = userService.findByToken(httpServletRequest.getHeader("token"));
            if (sysUser == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            for (Long tag_id:tagsBean.getIds()){
                BookTag tag = bookTagService.selectByPid(tag_id);
                if (tag != null){
                    return JsonResult.getOther("标签下含有其他标签，不允许删除");
                }
                List<BookTagRel> rels = bookTagRelService.selectById(tag_id);
                if (rels.size()>0){
                    return JsonResult.getOther("标签已分配图书，请移除所有图书关系");
                }
            }
            bookTagService.deleteTagByTagIds(tagsBean.getIds());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

//    /**
//     * 删除图书标签
//     * @param request
//     * @param tagsBean
//     * @return
//     */
//    @RequestMapping(value = "/admin/bookTag/delBookTag", method = RequestMethod.POST)
//    @ResponseBody
//    public Object delBookTag(HttpServletRequest request, @RequestBody BookTagsBean tagsBean){
//        try{
//            bookTagService.deleteTagByTagIds(tagsBean.getIds());
//            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
//            result.setData(new ArrayList());
//            return result;
//        }catch (Exception e){
//            e.printStackTrace();
//            return JsonResult.getException(Constants.EXCEPTION);
//        }
//    }

    /**
     * 给标签添加图书/移除标签下图书
     * @param httpServletRequest
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/bookTag/json/updateBookTagRel", method = RequestMethod.POST)
    @ResponseBody
    public Object addBook(HttpServletRequest httpServletRequest, @RequestBody UpdateBookTagRelBean bean){
        try{
            //查询是否存在操作的关系数据
            BookTagRel bookTagRel = bookTagRelService.selectByTagIdAndBookId(bean.getTag_id(), bean.getBook_id());
            if(bookTagRel == null){
                //给标签添加图书
                BookTag bookTag = bookTagService.selectByPid(bean.getTag_id());
                if (bookTag == null) {
                    BookTagRel rel = new BookTagRel(bean.getBook_id(), bean.getTag_id(), new Date());
                    bookTagRelService.saveBookTagRel(rel);
                }else{
                    return JsonResult.getOther("请在标签最后一级(添加/移除)图书");
                }
            }else{
                //移除标签下图书
                bookTagRelService.deleteBookTagRel(bookTagRel);
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 获取所有未分配标签的图书
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/bookTag/noTagBook",method = RequestMethod.POST)
    @ResponseBody
    public Object noTagBook(HttpServletRequest request,@RequestBody PageConditionBean bean){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getPageSize() == null || bean.getPageNum() == null){
                return JsonResult.getError("请提供分页参数");
            }
            Sort sort = new Sort(Sort.Direction.DESC,"update_time");
            Object obj = bookTagService.getNoTagList(bean,sort);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(obj);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


}
