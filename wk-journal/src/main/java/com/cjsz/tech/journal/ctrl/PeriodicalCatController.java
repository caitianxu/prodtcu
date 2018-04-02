package com.cjsz.tech.journal.ctrl;

import com.cjsz.tech.journal.beans.*;
import com.cjsz.tech.journal.domain.PeriodicalCat;
import com.cjsz.tech.journal.domain.PeriodicalRepo;
import com.cjsz.tech.journal.service.PeriodicalCatService;
import com.cjsz.tech.journal.service.PeriodicalRepoService;
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
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/10/25.
 */
@Controller
public class PeriodicalCatController {

    @Autowired
    PeriodicalCatService periodicalCatService;

    @Autowired
    PeriodicalRepoService periodicalRepoService;

    @Autowired
    UserService userService;


    /**
     * 期刊分类树(期刊分类列表用)
     *
     * @return
     */
    @RequestMapping(value = "/admin/periodicalCat/getTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getTree(HttpServletRequest request) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (null == sysUser.getOrg_id()) {
                return JsonResult.getError(Constants.ACTION_ERROR);
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            //获取当前用户机构全部期刊分类，转化为树形结构
            List<PeriodicalCatBean> periodicalCat = periodicalCatService.getCatList(sysUser.getOrg_id());
            if (periodicalCat.size() > 0) {
                List<PeriodicalCatBean> cats = periodicalCatService.getCatTree(periodicalCat);
                jsonResult.setData(cats);
            } else {
                jsonResult.setData(new ArrayList<>());
            }
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增,修改期刊分类
     *
     * @return
     */
    @RequestMapping(value = "/admin/periodicalCat/savePeriodicalCat", method = RequestMethod.POST)
    @ResponseBody
    public Object savePeriodicalCat(HttpServletRequest request, @RequestBody PeriodicalCatBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (null == bean.getPeriodical_cat_name()) {
                return JsonResult.getError(Constants.ACTION_ERROR);
            }
            if (bean.getPeriodical_cat_id() == null) {
                if (sysUser.getOrg_id().intValue() != 1) {
                    return JsonResult.getError(Constants.INIT_REFUSED);
                }
                //新增
                //分类名称重复(分类名查找)
                List<PeriodicalCat> periodicalCats = periodicalCatService.selectByCatName(bean.getPeriodical_cat_name());
                if (periodicalCats.size() > 0) {
                    return JsonResult.getError(Constants.CATNAME_REPETITION);
                }
                boolean flag = periodicalCatService.saveCat(bean);
                if (flag) {
                    return JsonResult.getSuccess(Constants.ACTION_ADD);
                } else {
                    return JsonResult.getError(Constants.ACTION_ERROR);
                }
            } else {
                //修改
                //分类名称重复(分类名查找不包括本身)
                List<PeriodicalCat> journalCats = periodicalCatService.selectOtherByCatName(bean.getPeriodical_cat_name(), bean.getPeriodical_cat_id());
                if (journalCats.size() > 0) {
                    return JsonResult.getError(Constants.CATNAME_REPETITION);
                }
                //分配只更新关系表数据
                PeriodicalCat cat = periodicalCatService.selectByCatId(bean.getPeriodical_cat_id());
                if (null == cat) {
                    return JsonResult.getError(Constants.CAT_NOT_EXIST);
                }
                if (sysUser.getOrg_id().intValue() != 1) {
                    //不可修改总部数据的层次、名称、备注信息
                    if ((!cat.getPeriodical_cat_pid().equals(bean.getPeriodical_cat_pid())) ||
                            (!cat.getPeriodical_cat_name().equals(bean.getPeriodical_cat_name()))) {
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
                boolean flag = periodicalCatService.updateCat(bean, sysUser.getOrg_id());
                if (flag) {
                    return JsonResult.getSuccess(Constants.ACTION_UPDATE);
                } else {
                    return JsonResult.getError(Constants.ACTION_ERROR);
                }
            }
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 期刊分类 是否启用
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/periodicalCat/updateEnabled", method = RequestMethod.POST)
    @ResponseBody
    public Object updateEnabled(HttpServletRequest request, @RequestBody PeriodicalCatBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser != null && sysUser.getOrg_id() == 1) {
                //是否启用(1:启用,2:不启用)
                boolean flag = periodicalCatService.updateEnabled(bean);
                if (flag) {
                    return JsonResult.getSuccess(Constants.ACTION_UPDATE);
                } else {
                    return JsonResult.getError(Constants.ACTION_ERROR);
                }
            } else {
                return JsonResult.getError(Constants.INIT_REFUSED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 期刊分类 是否显示
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/periodicalCat/updateIsShow", method = RequestMethod.POST)
    @ResponseBody
    public Object updateIsShow(HttpServletRequest request, @RequestBody PeriodicalCatBean bean) {
        try {
            if (bean.getEnabled() == 2) {
                return JsonResult.getError("期刊分类未启用！");
            }
            boolean flag = periodicalCatService.updateShow(bean);
            if (flag) {
                return JsonResult.getSuccess(Constants.ACTION_UPDATE);
            } else {
                return JsonResult.getError(Constants.ACTION_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 期刊分类列表 删除数据
     *
     * @param request
     * @param delCatBean
     */
    @RequestMapping(value = "/admin/periodicalCat/deletePeriodicalCat", method = {RequestMethod.POST})
    @ResponseBody
    public Object deletePeriodicalCat(HttpServletRequest request, @RequestBody DelPeriodicalCatBean delCatBean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (null == sysUser) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (sysUser.getOrg_id().intValue() != 1) {
                return JsonResult.getError(Constants.INIT_REFUSED);
            }
            Long[] journal_cat_ids;
            if (null != delCatBean && null != delCatBean.getPeriodical_cat_ids()) {
                journal_cat_ids = delCatBean.getPeriodical_cat_ids();
            } else {
                return JsonResult.getError(Constants.ACTION_ERROR);
            }
            //需要删除的期刊分类（1,2,3）
            String cat_ids = StringUtils.join(journal_cat_ids, ",");
            //通过cat_ids找到full_path合集
            List<String> full_paths = periodicalCatService.getFullPathsByCatIds(cat_ids);
            //需要删除的所有期刊分类catId合集
            List<Long> catIdList = new ArrayList<Long>();
            for (String full_path : full_paths) {
                //通过full_path获取自身和所有子集的catId合集
                catIdList.addAll(periodicalCatService.getCatIdsByFullPath(full_path));
            }
            //自身和所有子集的catIds
            String catIds = StringUtils.join(catIdList, ",");
            //通过cat_ids查询期刊详情
            List<PeriodicalRepo> journals = periodicalRepoService.selectByCatIds(catIds);
            if (journals.size() > 0) {
                return JsonResult.getError("分类下存在关联的期刊，不可删除");
            }
            periodicalCatService.deleteByCatIds(catIds);
            return JsonResult.getSuccess(Constants.ACTION_DELETE);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 期刊分类添加机构
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/periodicalCat/addOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object addOrg(HttpServletRequest request, @RequestBody UpdateNewCatOrgBean bean) {
        try {
            if (null == bean.getOrg_id() || null == bean.getPeriodical_cat_id()) {
                return JsonResult.getError(Constants.ACTION_ERROR);
            }
            Long org_id = bean.getOrg_id();
            if (org_id == 1) {
                return JsonResult.getError("不能为长江科技分配期刊分类！");
            }
            boolean flag = periodicalCatService.addOrg(bean.getPeriodical_cat_id(), org_id);
            if (flag) {
                return JsonResult.getSuccess(Constants.ACTION_UPDATE);
            } else {
                return JsonResult.getError(Constants.ACTION_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 期刊分类移除机构
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/periodicalCat/removeOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object removeOrg(HttpServletRequest request, @RequestBody UpdateNewCatOrgBean bean) {
        try {
            if (null == bean.getOrg_id() || null == bean.getPeriodical_cat_id()) {
                return JsonResult.getError(Constants.ACTION_ERROR);
            }
            if (bean.getOrg_id() == 1) {
                return JsonResult.getError("不能移除长江科技的期刊分类！");
            }
            boolean flag = periodicalCatService.removeOrg(bean.getPeriodical_cat_id(), bean.getOrg_id());
            if (flag) {
                return JsonResult.getSuccess(Constants.ACTION_UPDATE);
            } else {
                return JsonResult.getError(Constants.ACTION_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 期刊分类机构列表
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/periodicalCat/getOrgList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getOrgList(HttpServletRequest request, @RequestBody FindNewCatOrgBean bean) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "jcor.is_delete").and(new Sort(Sort.Direction.ASC, "org_id"));
            Object obj = periodicalCatService.getOrgsPageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(obj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 期刊分类树(网站)
     *
     * @return
     */
    @RequestMapping(value = "/site/periodicalCat/catPathTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object catPathTree(HttpServletRequest request, @RequestBody FindSeriesBean bean) {
        try {
            if (bean.getOrg_id() == null) {
                return JsonResult.getError("请提供机构id");
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            //获取当前用户机构全部期刊分类，转化为树形结构
            List<PeriodicalCatBean> periodicalCat = periodicalCatService.catList(bean.getOrg_id());
            if (periodicalCat.size() > 0) {
                List<PeriodicalCatBean> cats = periodicalCatService.getCatTree(periodicalCat);
                jsonResult.setData(cats);
            } else {
                jsonResult.setData(new ArrayList<>());
            }
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 修改机构期刊分类排序
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/periodicalCat/orderByOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object orderByOrg(HttpServletRequest request, @RequestBody PeriodicalCatBean bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getPeriodical_cat_id() == null) {
                return JsonResult.getError("请选择期刊分类");
            }
            periodicalCatService.orderByOrg(bean.getPeriodical_cat_id(), bean.getOrder_weight(), user.getOrg_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
