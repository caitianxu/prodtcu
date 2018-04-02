package com.cjsz.tech.book.ctrl;

import com.cjsz.tech.book.beans.CommonBean;
import com.cjsz.tech.book.beans.DelBookCatBean;
import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.book.domain.BookOrgRel;
import com.cjsz.tech.book.service.BookCatService;
import com.cjsz.tech.book.service.BookOrgRelService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 图书分类管理
 * Created by shiaihua on 16/12/17.
 */
@Controller
public class BookCatController {

    @Autowired
    BookCatService bookCatService;

    @Autowired
    BookOrgRelService bookOrgRelService;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    UserService userService;

    /**
     * 图书分类树
     *
     * @return
     */
    @RequestMapping(value = "/admin/BookCat/getTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getTree(HttpServletRequest request, @RequestBody CommonBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser.getOrg_id() == null) {
                return JsonResult.getException(Constants.EXCEPTION);
            }
            //获取当前用户机构全部资讯分类，转化为树形结构

            //获取机构分类
            List<BookCat> bookCats = bookCatService.getOrgCats(sysUser.getOrg_id());
            List<BookCat> cats = bookCatService.selectTree(bookCats);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(cats);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增,修改分类
     *
     * @return
     */
    @RequestMapping(value = "/admin/bookCat/save_book_cat", method = RequestMethod.POST)
    @ResponseBody
    public Object save_book_cat(HttpServletRequest request, @RequestBody BookCat catBean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            JsonResult jsonResult = JsonResult.getSuccess("");
            BookCat bookCat = null;
            if (catBean.getBook_cat_id() == null) {
                //新增
                bookCat = bookCatService.saveCat(catBean, sysUser.getOrg_id());
                jsonResult.setData(bookCat);
                jsonResult.setMessage(Constants.ACTION_ADD);
                return jsonResult;
            } else {
                //修改
                //获取当前修改的分类本身和下级分类
                List<BookCat> cats = bookCatService.getOwnerCats(catBean.getOrg_id(), catBean.getBook_cat_path());
                for (BookCat cat : cats) {
                    if (cat.getBook_cat_id().equals(catBean.getBook_cat_pid())) {
                        return JsonResult.getError(Constants.CLASS_PID_ERROR);
                    }
                }
                bookCat = bookCatService.updateCat(catBean);
                jsonResult.setData(bookCat);
                jsonResult.setMessage(Constants.ACTION_UPDATE);
                return jsonResult;
            }
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 图书分类列表 删除数据
     *
     * @param request
     * @param delCatBean
     */
    @RequestMapping(value = "/admin/bookCat/delete_book_cat", method = {RequestMethod.POST})
    @ResponseBody
    public Object delete_book_cat(HttpServletRequest request, @RequestBody DelBookCatBean delCatBean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            Long orgid = user.getOrg_id();
            List<Long> catidList = null;
            List<BookOrgRel> bookOrgRels = null;

            //删除机构分类
            catidList = bookCatService.getOrgAllCat(orgid, delCatBean.getBook_cat_ids());
            //查询机构图书详情
            bookOrgRels = bookOrgRelService.selectOrgRels(orgid, catidList);
            if (StringUtil.isEmpty(delCatBean.getMark()) && bookOrgRels.size() > 0) {
                return JsonResult.getError("分类下存在关联的图书，是否删除？");
            }
            //删除机构的图书分类和详情
            bookCatService.deleteOrgAll(orgid, catidList);

            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(new ArrayList());
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
