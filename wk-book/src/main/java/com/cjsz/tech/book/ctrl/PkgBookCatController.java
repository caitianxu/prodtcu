package com.cjsz.tech.book.ctrl;

import com.cjsz.tech.book.beans.CommonBean;
import com.cjsz.tech.book.beans.DelBookCatBean;
import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.book.domain.PkgBookCat;
import com.cjsz.tech.book.service.PkgBookCatService;
import com.cjsz.tech.book.service.PkgBookRelService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
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
public class PkgBookCatController {

    @Autowired
    PkgBookCatService pkgBookCatService;

    @Autowired
    PkgBookRelService pkgBookRelService;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    UserService userService;

    /**
     * 图书分类树
     *
     * @return
     */
    @RequestMapping(value = "/admin/pkgBookCat/getTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getTree(HttpServletRequest request, @RequestBody CommonBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser.getOrg_id() == null) {
                return JsonResult.getException(Constants.EXCEPTION);
            }
            //获取数据包分类
            List<PkgBookCat> bookCats = pkgBookCatService.getPkgCats(sysUser.getOrg_id(), bean.getPkg_id());

            List<PkgBookCat> cats = pkgBookCatService.selectTree(bookCats);
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
    @RequestMapping(value = "/admin/pkgBookCat/save_book_cat", method = RequestMethod.POST)
    @ResponseBody
    public Object save_book_cat(HttpServletRequest request, @RequestBody PkgBookCat catBean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            JsonResult jsonResult = JsonResult.getSuccess("");
            PkgBookCat bookCat = null;
            if (catBean.getBook_cat_id() == null) {
                //新增
                bookCat = pkgBookCatService.saveCat(catBean, sysUser.getOrg_id());
                jsonResult.setData(bookCat);
                jsonResult.setMessage(Constants.ACTION_ADD);
                return jsonResult;
            } else {
                //修改
                //获取当前修改的分类本身和下级分类
                List<BookCat> cats = pkgBookCatService.getOwnerCats(catBean.getOrg_id(), catBean.getPkg_id(), catBean.getBook_cat_path());
                for (BookCat cat : cats) {
                    if (cat.getBook_cat_id().equals(catBean.getBook_cat_pid())) {
                        return JsonResult.getError(Constants.CLASS_PID_ERROR);
                    }
                }
                bookCat = pkgBookCatService.updateCat(catBean);
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
    @RequestMapping(value = "/admin/pkgBookCat/delete_book_cat", method = {RequestMethod.POST})
    @ResponseBody
    public Object delete_book_cat(HttpServletRequest request, @RequestBody DelBookCatBean delCatBean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            Long orgid = user.getOrg_id();

            //删除包的分类
            List<Long> catidList = pkgBookCatService.getPkgAllCat(delCatBean.getPkg_id(), orgid, delCatBean.getBook_cat_ids());

            //删除数据包的图书分类和详情
            pkgBookCatService.deletePkgAll(delCatBean.getPkg_id(), orgid, catidList);

            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(new ArrayList());
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
