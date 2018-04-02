package com.cjsz.tech.book.ctrl;

import com.cjsz.tech.book.beans.BookBean;
import com.cjsz.tech.book.beans.CommonBean;
import com.cjsz.tech.book.beans.FindBookBean;
import com.cjsz.tech.book.beans.SelectBookBean;
import com.cjsz.tech.book.domain.BookOrgRel;
import com.cjsz.tech.book.service.BookOrgRelService;
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
public class BookOrgRelController {

    @Autowired
    private BookOrgRelService bookOrgRelService;

    @Autowired
    private UserService userService;


    /**
     * 图书页面 分页查询
     * @return
     */
    @RequestMapping(value = "/admin/bookOrgRel/json/getList", method = RequestMethod.POST)
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody FindBookBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            bean.setOrg_id(user.getOrg_id());
            Object data = bookOrgRelService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 机构图书列表 修改图书
     * @param request
     * @param bookBean
     * @return
     */
    @RequestMapping(value = "/admin/bookOrgRel/json/updateBookOrgRel", method = RequestMethod.POST)
    @ResponseBody
    public Object updateBookOrgRel(HttpServletRequest request, @RequestBody BookBean bookBean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            //查询原始关系数据
            BookOrgRel bookOrgRel = bookOrgRelService.selectBookOrgRelByUnionIds(bookBean.getBook_id(),
                    user.getOrg_id(), bookBean.getBook_cat_id(), bookBean.getPkg_id());
            if(!Objects.equals(bookBean.getIs_hot(), bookOrgRel.getIs_hot())){
                //是否热门
                bookOrgRelService.updateRelHot(bookBean);
            }else if(!Objects.equals(bookBean.getIs_recommend(), bookOrgRel.getIs_recommend())){
                //是否推荐
                bookOrgRelService.updateRelRecommend(bookBean);
            }else if(!bookBean.getOffline_status().equals(bookOrgRel.getOffline_status()) ){
                //是否离线
                bookOrgRelService.updateOffLine(bookBean);
            }else if(!bookBean.getOrder_weight().equals(bookOrgRel.getOrder_weight()) ){
                //排序置顶
                bookOrgRelService.updateOrderTop(bookBean);
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
     * 机构图书列表 修改图书 启用、停用
     * @param request
     * @param bookBeans
     * @return
     */
    @RequestMapping(value = "/admin/bookOrgRel/json/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object updateStatus(HttpServletRequest request, @RequestBody SelectBookBean bookBeans){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            Long orgid = user.getOrg_id();
            bookOrgRelService.updateBookRelStatus(orgid, bookBeans.getBeanList());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 更新图书离线
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/bookOrgRel/json/updateBookOffLine", method = RequestMethod.POST)
    @ResponseBody
    public Object  updateBookOffLine(HttpServletRequest request, @RequestBody CommonBean bean){
        try{
            if(bean.getIds()==null || bean.getIds().size()==0) {
                return JsonResult.getError("请选择一本到多本图书");
            }
            SysUser user = userService.findByToken(request.getHeader("token"));
            Long orgid = user.getOrg_id();
            //更新组织下某一设备的图书离线状态
            bookOrgRelService.updateBookOffLine(orgid, bean.getDevice_id(), bean.getIds(), bean.getBool());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 删除当前组织的选中的图书关系
     * @param request
     * @param bookBeans
     * @return
     */
    @RequestMapping(value = "/admin/bookOrgRel/json/deleteBookOrgRels", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteBooks(HttpServletRequest request, @RequestBody SelectBookBean bookBeans){
        try{
            if(bookBeans==null || bookBeans.getBeanList()==null || bookBeans.getBeanList().size()==0) {
                return JsonResult.getError("请选择一本到多本图书");
            }
            SysUser user = userService.findByToken(request.getHeader("token"));
            Long orgid = user.getOrg_id();
            bookOrgRelService.deleteBooksRel(orgid, bookBeans.getBeanList());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 图书管理图书全部导出
     * @param request
     * @param response
     */
    @RequestMapping(value = "/admin/bookOrgRel/json/allExport", method = {RequestMethod.GET, RequestMethod.POST})
    public void allExport(HttpServletRequest request, HttpServletResponse response, @RequestParam("book_cat_id") Long book_cat_id){
        try{
            String token = (String) request.getSession().getAttribute("token");
            SysUser sysUser = userService.findByToken(token);// 当前登录用户
            Long org_id = sysUser.getOrg_id();
            List<Map<String, Object>> books = bookOrgRelService.getAllBooksBaseInfo(org_id, book_cat_id);
            String title = "图书";
            String[] rowName = {"序号", "书名", "作者", "出版社"};
            List<Object[]> dataList = new ArrayList<Object[]>();
            for(Map<String, Object> book : books){
                Object[] obj = {book.get("book_id"), book.get("book_name"), book.get("book_author"), book.get("book_publisher")};
                dataList.add(obj);
            }
            ExportExcel excel = new ExportExcel(title,rowName, dataList, response);
            excel.export();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
