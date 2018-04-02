package com.cjsz.tech.book.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.BookDetail;
import com.cjsz.tech.book.beans.CJZWWBookBean;
import com.cjsz.tech.book.beans.DelRecommendBookBean;
import com.cjsz.tech.book.beans.RecommendBookListBean;
import com.cjsz.tech.book.domain.RecommendBooks;
import com.cjsz.tech.book.domain.RecommendCat;
import com.cjsz.tech.book.service.RecommendBooksService;
import com.cjsz.tech.dev.domain.AppTypeOrgRel;
import com.cjsz.tech.dev.service.AppTypeService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.ProOrgExtend;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/5 0005.
 */
@Controller
public class RecommendBooksController {

    @Autowired
    private RecommendBooksService recommendBooksService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProOrgExtendService proOrgExtendService;

    /**
     * 获取图书推荐列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/admin/recommend/getList",method = RequestMethod.POST)
    @ResponseBody
    public Object getList(HttpServletRequest request){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            List<RecommendCat> list = recommendBooksService.getList();
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(list);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 网文搜索图书
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/cjzww/getBook",method = RequestMethod.POST)
    @ResponseBody
    public Object getBook(HttpServletRequest request, @RequestBody PageConditionBean bean){
        try{
            if (bean.getPageNum() == null || bean.getPageSize() == null){
                return JsonResult.getError("请提供参数");
            }
            if (StringUtils.isEmpty(bean.getSearchText())){
                return JsonResult.getError("请输入搜索内容");
            }
            CJZWWBookBean book = recommendBooksService.searchBook(bean.getSearchText(),bean.getPageNum().toString(),bean.getPageSize().toString());
            List<RecommendBooks> books = new ArrayList<>();
            if (book.getBk_list() != null && book.getBk_list().size()>0) {
                List<BookDetail> cjzwwBooks = book.getBk_list();
                for (BookDetail bookDetail : cjzwwBooks) {
                    RecommendBooks recommendBooks = new RecommendBooks();
                    recommendBooks.setBook_name(bookDetail.getBookName());
                    recommendBooks.setBook_remark(bookDetail.getBrief());
                    if (StringUtils.isNotEmpty(bookDetail.getBookID())) {
                        recommendBooks.setBook_id(Long.parseLong(bookDetail.getBookID()));
                    }
                    recommendBooks.setBook_cover(bookDetail.getImage());
                    recommendBooks.setBook_author(bookDetail.getAuthor());
                    recommendBooks.setBook_type(1);
                    books.add(recommendBooks);
                }
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(books);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 获取长江中文网网文列表分页数据
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/cjzww/getBookList",method =RequestMethod.POST)
    @ResponseBody
    public Object getCJZWWBookList(HttpServletRequest request,@RequestBody PageConditionBean bean){
        try{
            if (bean.getPageSize() == null || bean.getPageNum() == null){
                return JsonResult.getError("请提供分页参数");
            }
            Object result = recommendBooksService.getCJZWWBookList(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }

    }



    /**
     * 推荐列表添加图书
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/recommend/addBook",method = RequestMethod.POST)
    @ResponseBody
    public Object addBook(HttpServletRequest request, @RequestBody RecommendBooks bean){
        try{
            if (bean.getRecommend_cat_id() == null){
                return JsonResult.getError("请提供推荐分类Id");
            }
            if (bean.getBook_id() == null){
                return JsonResult.getError("请提供图书id");
            }
            RecommendBooks book = recommendBooksService.selectByBookId(bean.getBook_id(),bean.getRecommend_cat_id());
            if (book != null){
                return JsonResult.getError("图书已存在该分类");
            }
            recommendBooksService.addBook(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 推荐列表移除图书
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/recommend/removeBook",method = RequestMethod.POST)
    @ResponseBody
    public Object removeBook(HttpServletRequest request, @RequestBody RecommendBooks bean){
        try{
            if (bean.getId() == null){
                return JsonResult.getError("请提供参数");
            }
            recommendBooksService.removeBook(bean.getId());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean.getId());
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增，修改
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/recommend/addUpdate",method = RequestMethod.POST)
    @ResponseBody
    public Object addUpdate(HttpServletRequest request, @RequestBody RecommendCat bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getRecommend_code() == null){
                return JsonResult.getOther("请填写分类code");
            }
            if (StringUtils.isEmpty(bean.getRecommend_type_name())){
                return JsonResult.getOther("请填写分类名称");
            }
            if (bean.getRecommend_cat_id() == null){
                //新增
                RecommendCat cat = recommendBooksService.selectByCode(bean.getRecommend_code());
                if (cat != null){
                    return JsonResult.getError("分类code已存在");
                }
                recommendBooksService.saveCat(bean);
            }else {
                //修改
                RecommendCat cat = recommendBooksService.selectByCodeAndId(bean.getRecommend_code(),bean.getRecommend_cat_id());
                if (cat != null){
                    return JsonResult.getError("分类code已存在");
                }
                recommendBooksService.updateCat(bean);
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除推荐分类
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/recommend/deleteCat",method = RequestMethod.POST)
    @ResponseBody
    public Object deleteCat(HttpServletRequest request, @RequestBody DelRecommendBookBean bean){
        try{
            for(Long recommend_cat_id:bean.getIds()){
                List<RecommendBooks> list = recommendBooksService.selectByCatId(recommend_cat_id);
                if (list.size()>0){
                    return JsonResult.getOther("请移除图书后再删除分类");
                }
            }
            String ids = StringUtils.join(bean.getIds(),",");
            recommendBooksService.deleteCat(ids);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取推荐分类下图书列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/recommend/getBookList",method = RequestMethod.POST)
    @ResponseBody
    public Object getBookList(HttpServletRequest request, @RequestBody RecommendBookListBean bean){
        try{
            if (bean.getRecommend_cat_id() == null){
                return JsonResult.getError("请提供分类id");
            }
            Object list = recommendBooksService.getBookList(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(list );
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取长江中文网数据同步
     * @param request
     * @return
     */
    @RequestMapping(value = "/admin/cjzww/bookList",method = RequestMethod.POST)
    @ResponseBody
    public Object bookList(HttpServletRequest request){
        try{
            recommendBooksService.saveBookByCJZWW(0L);
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getError("数据异常");
        }
    }

    /**
     * 推荐图书排序修改
     * @param request
     * @return
     */
    @RequestMapping(value = "/admin/recommend/orderWeight",method = RequestMethod.POST)
    @ResponseBody
    public Object orderWeight(HttpServletRequest request,@RequestBody RecommendBooks bean){
        try{
            if (bean.getId() == null){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            if (bean.getOrder_weight() == null){
                return JsonResult.getOther(Constants.EXCEPTION);
            }
            recommendBooksService.updateOrder(bean.getId(),bean.getOrder_weight());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getError("数据异常");
        }
    }


    /**
     * 扫码下载app
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/appStore/downLoad",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public void bookinfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String org_id = request.getParameter("org_id");
            ProOrgExtend orgExtend = proOrgExtendService.selectByOrgId(Long.parseLong(org_id));
            String url = "http://www.cjszyun.cn/pkgApp";
            if (orgExtend != null) {
                url = "http://" + orgExtend.getServer_name() + ".cjszyun.cn/pkgApp";
            }
            else{
                url += "?org_id=" + org_id;
            }
            response.sendRedirect(url);
        }catch (Exception e){
            e.printStackTrace();
            System.out.print(e.getMessage());
            response.sendRedirect("http://cjszyun.cn/mobile/err");
        }
    }
}



