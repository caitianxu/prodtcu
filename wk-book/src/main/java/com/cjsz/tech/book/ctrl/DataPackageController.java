package com.cjsz.tech.book.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.*;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.DataPackage;
import com.cjsz.tech.book.service.BookRepoService;
import com.cjsz.tech.book.service.DataPackageService;
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
import java.awt.print.Book;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据包管理
 * Created by shiaihua on 16/12/17.
 */
@Controller
public class DataPackageController {

    @Autowired
    private DataPackageService dataPackageService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookRepoService bookRepoService;

    /**
     * 数据包分页列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/dataPackage/json/getList", method = RequestMethod.POST)
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody PageConditionBean bean){
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            Object obj = dataPackageService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增/修改   数据包
     * @param request
     * @param dataPackage
     * @return
     */
    @RequestMapping(value = "/admin/dataPackage/json/updatePkg", method = RequestMethod.POST)
    @ResponseBody
    public Object updatePkg(HttpServletRequest request, @RequestBody DataPackage dataPackage){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            if(dataPackage.getPkg_id() == null){
                //新增
                dataPackage.setOrg_count(0L);
                dataPackage.setBook_count(0L);
                dataPackage.setBook_real_count(0L);
                dataPackage.setCreate_user_id(sysUser.getUser_id());
                dataPackageService.savePkg(dataPackage);
                result.setMessage(Constants.ACTION_ADD);
            }else{
                //修改
                dataPackageService.updatePkg(dataPackage);
                result.setMessage(Constants.ACTION_UPDATE);
            }
            result.setData(dataPackage);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 数据包分配给机构
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/dataPackage/json/allotPkg", method = RequestMethod.POST)
    @ResponseBody
    public Object allotPkg(HttpServletRequest request, @RequestBody AllotPkgBean bean){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            dataPackageService.allotPkg(sysUser.getOrg_id(), bean.getPkg_id(), bean.getOrgs());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 数据包机构列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/dataPackage/json/getAllortOrgs", method = RequestMethod.POST)
    @ResponseBody
    public Object getAllortOrgs(HttpServletRequest request, @RequestBody AllotPkgBean bean){
        try{
            List<AllortedOrgBean> data = dataPackageService.getAllortOrgs(bean.getPkg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除数据包
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/dataPackage/json/deletePkgs", method = RequestMethod.POST)
    @ResponseBody
    public Object deletePkgs(HttpServletRequest request, @RequestBody DataPackagesBean bean){
        try{
            dataPackageService.deletePkgs(bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 复制数据包
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/dataPackage/json/copyPkg", method = RequestMethod.POST)
    @ResponseBody
    public Object copyPkg(HttpServletRequest request, @RequestBody CopyPkgBean bean){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            DataPackage dataPackage = dataPackageService.copyPkg(sysUser.getUser_id(),sysUser.getOrg_id(), bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_ADD);
            result.setData(dataPackage);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 输入图书id批量置顶
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/dataPackage/json/topBooks",method = RequestMethod.POST)
    @ResponseBody
    public Object topBooks(HttpServletRequest request,@RequestBody TopBoosBean bean){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getCat_id() == null || StringUtils.isEmpty(bean.getBook_ids())){
                return JsonResult.getError("请提供参数");
            }
            Long order_weight = System.currentTimeMillis();
            //记录错误数量
            List<String> errIds = new ArrayList<>();
            String[] str = bean.getBook_ids().split(",");
            Long book_id;
            for (int i = 0;i<str.length;i++){
                boolean flag = bookRepoService.isInteger(str[i]);
                if (flag == true) {
                    book_id = Long.parseLong(str[i]);
                    BookRepo book = bookRepoService.findByBookId(book_id);
                    if (book != null){
                        //修改数据包图书排序值为当前时间戳
                        bookRepoService.updateOrderById(book_id,bean.getCat_id(),order_weight);
                    }else {
                        errIds.add(str[i]);
                        continue;
                    }
                }else{
                    errIds.add(str[i]);
                }
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(errIds);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
