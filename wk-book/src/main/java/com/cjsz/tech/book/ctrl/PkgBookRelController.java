package com.cjsz.tech.book.ctrl;

import com.cjsz.tech.book.beans.*;
import com.cjsz.tech.book.domain.PkgBookRel;
import com.cjsz.tech.book.service.PkgBookRelService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.ExportExcel;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 图书管理
 * Created by shiaihua on 16/12/17.
 */
@Controller
public class PkgBookRelController {

    @Autowired
    private PkgBookRelService pkgBookRelService;

    @Autowired
    private UserService userService;

    /**
     * 图书页面 分页查询____数据包
     * @return
     */
    @RequestMapping(value = "/admin/pkgBookRel/json/getList", method = RequestMethod.POST)
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody FindBookBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            bean.setOrg_id(user.getOrg_id());
            Object data = pkgBookRelService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 数据包新增 图书
     * @param request
     * @param bookBean
     * @return
     */
    @RequestMapping(value = "/admin/pkgBookRel/json/savePkgBookRel", method = RequestMethod.POST)
    @ResponseBody
    public Object savePkgBookRel(HttpServletRequest request, @RequestBody SaveBookBean bookBean){
        try{
            if(bookBean.getBook_cat_id() == null || bookBean.getBook_cat_id() == 0){
                return JsonResult.getError("请选择图书分类！");
            }
            SysUser user = userService.findByToken(request.getHeader("token"));
            pkgBookRelService.savePkgBookRel(user.getOrg_id(), bookBean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_ADD);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 数据包单本加书
     * @param request
     * @param bookBean
     * @return
     */
    @RequestMapping(value = "/admin/pkgBookRel/json/pkgInsertBook", method = RequestMethod.POST)
    @ResponseBody
    public Object pkgInsertBook(HttpServletRequest request, @RequestBody SaveBookBean bookBean){
        try{
            if(bookBean.getBook_cat_id() == null || bookBean.getBook_cat_id() == 0){
                return JsonResult.getError("请选择图书分类！");
            }
            if (bookBean.getBook_id() == null){
                return JsonResult.getError("请提供图书id");
            }
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            PkgBookRel pkgBookRel = pkgBookRelService.selectById(user.getOrg_id(),bookBean);
            if (pkgBookRel != null){
                return JsonResult.getOther("已存在该图书");
            }
            pkgBookRelService.pkgInsertBook(user.getOrg_id(), bookBean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_ADD);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 数据包移除 图书
     * @param request
     * @param bookBean
     * @return
     */
    @RequestMapping(value = "/admin/pkgBookRel/json/removePkgBookRel", method = RequestMethod.POST)
    @ResponseBody
    public Object removePkgBookRel(HttpServletRequest request, @RequestBody BookBean bookBean){
        try{
            pkgBookRelService.removePkgBookRel(bookBean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_ADD);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 数据包移除 图书
     * @param request
     * @param pkgBookBean
     * @return
     */
    @RequestMapping(value = "/admin/pkgBookRel/json/removePkgBookRels", method = RequestMethod.POST)
    @ResponseBody
    public Object removePkgBookRels(HttpServletRequest request, @RequestBody PkgBookBean pkgBookBean){
        try{
            pkgBookRelService.removePkgBookRels(pkgBookBean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_ADD);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 数据包图书列表 修改图书
     * @param request
     * @param bookBean
     * @return
     */
    @RequestMapping(value = "/admin/pkgBookRel/json/updatePkgBookRel", method = RequestMethod.POST)
    @ResponseBody
    public Object updatePkgBookRel(HttpServletRequest request, @RequestBody BookBean bookBean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            //查询原始关系数据
            PkgBookRel bookOrgRel = pkgBookRelService.selectPkgBookRelByUnionIds(bookBean.getBook_id(),
                    user.getOrg_id(), bookBean.getBook_cat_id(), bookBean.getPkg_id());
            if(!Objects.equals(bookBean.getIs_hot(), bookOrgRel.getIs_hot())){
                //是否热门
                pkgBookRelService.updateRelHot(bookBean);
            }else if(!Objects.equals(bookBean.getIs_recommend(), bookOrgRel.getIs_recommend())){
                //是否推荐
                pkgBookRelService.updateRelRecommend(bookBean);
            }else if(!bookBean.getOffline_status().equals(bookOrgRel.getOffline_status()) ){
                //是否离线
                pkgBookRelService.updateOffLine(bookBean);
            }else if(!bookBean.getOrder_weight().equals(bookOrgRel.getOrder_weight()) ){
                //排序置顶
                pkgBookRelService.updateOrderTop(bookBean);
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 数据包图书全部导出
     * @param request
     * @param response
     */
    @RequestMapping(value = "/admin/pkgBookRel/json/allExport", method = {RequestMethod.GET, RequestMethod.POST})
    public void allExport(HttpServletRequest request, HttpServletResponse response, @RequestParam("pkg_id") Long pkg_id){
        try{
            List<Map<String, Object>> books = pkgBookRelService.getAllBooksBaseInfo(pkg_id);
            String title = "图书";
            String[] rowName = {"序号","图书编号", "书名", "作者", "出版社","分类名称"};
            List<Object[]> dataList = new ArrayList<Object[]>();
            for(Map<String, Object> book : books){
                Object[] obj = {"",book.get("book_id"), book.get("book_name"), book.get("book_author"), book.get("book_publisher"),book.get("book_cat_name")};
                dataList.add(obj);
            }
            ExportExcel excel = new ExportExcel(title,rowName, dataList, response);
            excel.export();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
