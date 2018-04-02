package com.cjsz.tech.journal.ctrl;

import com.cjsz.tech.journal.beans.DelNewspaperCatBean;
import com.cjsz.tech.journal.beans.FindCatOrgBean;
import com.cjsz.tech.journal.beans.NewspaperCatBean;
import com.cjsz.tech.journal.beans.UpdateCatOrgBean;
import com.cjsz.tech.journal.domain.Newspaper;
import com.cjsz.tech.journal.domain.NewspaperCat;
import com.cjsz.tech.journal.service.NewspaperCatService;
import com.cjsz.tech.journal.service.NewspaperService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.StringUtil;
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
import java.util.List;

/**
 * Author:Jason
 * Date:2016/12/2
 * 报纸分类控制器
 */
@Controller
public class NewspaperCatController {
    @Autowired
    NewspaperCatService newspaperCatService;

    @Autowired
    NewspaperService newspaperService;

    @Autowired
    UserService userService;


    /**
     * 报纸分类树(报纸分类列表用)
     *
     * @return
     */
    @RequestMapping(value = "/admin/newspaperCat/getTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getTree(HttpServletRequest request) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //获取当前用户机构全部报纸分类，转化为树形结构
            List<NewspaperCatBean> newspaperCats = newspaperCatService.getCats(sysUser.getOrg_id());
            List<NewspaperCatBean> cats = newspaperCatService.selectTree(newspaperCats);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(cats);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 报纸分类树(本机构自建，报纸分类详情用)
     *
     * @return
     */
    @RequestMapping(value = "/admin/newspaperCat/getOrgTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getOrgTree(HttpServletRequest request) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //获取当前用户机构全部报纸分类，转化为树形结构
            List<NewspaperCatBean> newspaperCats = newspaperCatService.getOrgCats(sysUser.getOrg_id());
            List<NewspaperCatBean> cats = newspaperCatService.selectTree(newspaperCats);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(cats);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增,修改报纸分类
     *
     * @return
     */
    @RequestMapping(value = "/admin/newspaperCat/save_newspaper_cat", method = RequestMethod.POST)
    @ResponseBody
    public Object save_newspaper_cat(HttpServletRequest request, @RequestBody NewspaperCatBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            JsonResult jsonResult = JsonResult.getSuccess("");
            NewspaperCatBean newspaperCatBean = null;
            if (bean.getNewspaper_cat_id() == null) {
                //新增
                //分类名称重复(分类名查找)
                List<NewspaperCat> newspaperCats = newspaperCatService.selectByCatName(sysUser.getOrg_id(), bean.getNewspaper_cat_name());
                if (newspaperCats.size() > 0) {
                    return JsonResult.getError(Constants.CATNAME_REPETITION);
                }
                newspaperCatBean = newspaperCatService.saveCat(bean, sysUser.getOrg_id());
                jsonResult.setData(newspaperCatBean);
                jsonResult.setMessage(Constants.ACTION_ADD);
                return jsonResult;
            } else {
                //修改
                //分类名称重复(分类名查找不包括本身)
                List<NewspaperCat> newspaperCats = newspaperCatService.selectOtherByCatName(sysUser.getOrg_id(), bean.getNewspaper_cat_name(), bean.getNewspaper_cat_id());
                if (newspaperCats.size() > 0) {
                    return JsonResult.getError(Constants.CATNAME_REPETITION);
                }
                //是否自建(1:自建;2:分配)
                if (bean.getSource_type() == 2) {
                    //分配只更新关系表数据
                    NewspaperCat cat = newspaperCatService.selectByCatId(bean.getNewspaper_cat_id());

                    //不可修改总部数据的层次、名称、备注信息
                    if ((!cat.getPid().equals(bean.getPid())) || (!cat.getNewspaper_cat_name().equals(bean.getNewspaper_cat_name()))) {
                        return JsonResult.getError("分配数据只能修改排序！");
                    }
                    String catRemark = cat.getRemark();
                    if (cat.getRemark() == null) {
                        catRemark = "";
                    }
                    if (!bean.getRemark().equals(catRemark)) {
                        return JsonResult.getError("分配数据只能修改排序！");
                    }
                }
                newspaperCatBean = newspaperCatService.updateCat(bean, sysUser.getOrg_id());
                jsonResult.setData(newspaperCatBean);
                jsonResult.setMessage(Constants.ACTION_UPDATE);
                return jsonResult;
            }
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新闻分类 启用
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/newspaperCat/json/updateEnabled", method = RequestMethod.POST)
    @ResponseBody
    public Object updateEnabled(HttpServletRequest request, @RequestBody NewspaperCatBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser.getOrg_id() == 1) {
                //是否启用(1:启用,2:不启用)
                newspaperCatService.updateEnabled(bean);
            } else {
                return JsonResult.getError("您没有权限！");
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(bean);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新闻分类 显示
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/newspaperCat/json/updateShow", method = RequestMethod.POST)
    @ResponseBody
    public Object updateShow(HttpServletRequest request, @RequestBody NewspaperCatBean bean) {
        try {
            if (bean.getEnabled() == 2) {
                return JsonResult.getError("报纸分类未启用！");
            }
            newspaperCatService.updateShow(bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(bean);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改机构报纸分类排序
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/newspaperCat/json/orderByOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object orderByOrg(HttpServletRequest request, @RequestBody NewspaperCatBean bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getNewspaper_cat_id() == null) {
                return JsonResult.getError("请选择分类");
            }
            newspaperCatService.orderByOrg(bean.getNewspaper_cat_id(), bean.getOrder_weight(), user.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(bean);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 报纸分类列表 删除数据
     *
     * @param request
     * @param delCatBean
     */
    @RequestMapping(value = "/admin/newspaperCat/delete_newspaper_cat", method = {RequestMethod.POST})
    @ResponseBody
    public Object delete_newspaper_cat(HttpServletRequest request, @RequestBody DelNewspaperCatBean delCatBean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //需要删除的报纸分类（1,2,3）
            Long[] newspaper_cat_ids = delCatBean.getNewspaper_cat_ids();

            //只能删除本机构的分类：is_delete
            for (int i = 0; i < newspaper_cat_ids.length; i++) {
                List<Long> catids = newspaperCatService.selectOrgCatIds(sysUser.getOrg_id());
                if (!catids.contains(newspaper_cat_ids[i])) {
                    return JsonResult.getException("只能删除本机构的分类！");
                }
            }
            String cat_ids = StringUtils.join(newspaper_cat_ids, ",");
            //通过cat_ids找到full_path合集
            List<String> full_paths = newspaperCatService.getFullPathsByCatIds(cat_ids);
            //需要删除的所有报纸分类catId合集
            List<Long> catIdList = new ArrayList<Long>();
            for (String full_path : full_paths) {
                //通过full_path获取自身和所有子集的catId合集
                catIdList.addAll(newspaperCatService.getCatIdsByFullPath(full_path));
            }
            //自身和所有子集的catIds
            String catIds = StringUtils.join(catIdList, ",");
            //通过cat_ids查询报纸详情
            List<Newspaper> newspapers = newspaperService.selectByCatIds(catIds);
            if (StringUtil.isEmpty(delCatBean.getMark()) && newspapers.size() > 0) {
                return JsonResult.getError("分类下存在关联的报纸，是否删除？");
            }
            newspaperCatService.deleteAllByCatIds(catIds);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(new ArrayList());
            return jsonResult;

        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 报纸分类添加机构
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/newspaperCat/json/addOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object addOrg(HttpServletRequest request, @RequestBody UpdateCatOrgBean bean) {
        try {
            Long org_id = bean.getOrg_id();
            if (org_id == 1) {
                return JsonResult.getError("不能为长江科技分配报纸分类！");
            }
            newspaperCatService.addOrg(bean.getNewspaper_cat_id(), org_id);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 报纸分类移除机构
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/newspaperCat/json/removeOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object removeOrg(HttpServletRequest request, @RequestBody UpdateCatOrgBean bean) {
        try {
            if (bean.getOrg_id() == 1) {
                return JsonResult.getError("不能移除长江科技的报纸分类！");
            }
            newspaperCatService.removeOrg(bean.getNewspaper_cat_id(), bean.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 报纸分类机构列表
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/newspaperCat/json/orgList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object orgList(HttpServletRequest request, @RequestBody FindCatOrgBean bean) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "is_delete").and(new Sort(Sort.Direction.ASC, "org_id"));
            Object obj = newspaperCatService.getOrgsPageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(obj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


}
