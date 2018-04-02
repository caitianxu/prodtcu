package com.cjsz.tech.journal.ctrl;

import com.cjsz.tech.journal.beans.DelJournalCatBean;
import com.cjsz.tech.journal.beans.FindCatOrgBean;
import com.cjsz.tech.journal.beans.JournalCatBean;
import com.cjsz.tech.journal.beans.UpdateCatOrgBean;
import com.cjsz.tech.journal.domain.Journal;
import com.cjsz.tech.journal.domain.JournalCat;
import com.cjsz.tech.journal.service.JournalCatService;
import com.cjsz.tech.journal.service.JournalService;
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
import java.util.*;


/**
 * Created by Administrator on 2016/10/25.
 */
@Controller
public class JournalCatController {

    @Autowired
    JournalCatService journalCatService;

    @Autowired
    JournalService journalService;

    @Autowired
    UserService userService;


    /**
     * 期刊分类树(期刊分类列表用)
     *
     * @return
     */
    @RequestMapping(value = "/admin/journalCat/getTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getTree(HttpServletRequest request) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //获取当前用户机构全部期刊分类，转化为树形结构
            List<JournalCatBean> journalCats = journalCatService.getCats(sysUser.getOrg_id());
            List<JournalCatBean> cats = journalCatService.selectTree(journalCats);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(cats);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 期刊分类树(本机构自建，期刊分类详情用)
     *
     * @return
     */
    @RequestMapping(value = "/admin/journalCat/getOrgTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getOrgTree(HttpServletRequest request) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //获取当前用户机构全部期刊分类，转化为树形结构
            List<JournalCatBean> journalCats = journalCatService.getOrgCats(sysUser.getOrg_id());
            List<JournalCatBean> cats = journalCatService.selectTree(journalCats);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(cats);
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
    @RequestMapping(value = "/admin/journalCat/save_journal_cat", method = RequestMethod.POST)
    @ResponseBody
    public Object save_journal_cat(HttpServletRequest request, @RequestBody JournalCatBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            JsonResult jsonResult = JsonResult.getSuccess("");
            JournalCatBean journalCatBean = null;
            if (bean.getJournal_cat_id() == null) {
                //新增
                //分类名称重复(分类名查找)
                List<JournalCat> journalCats = journalCatService.selectByCatName(sysUser.getOrg_id(), bean.getJournal_cat_name());
                if (journalCats.size() > 0) {
                    return JsonResult.getError(Constants.CATNAME_REPETITION);
                }
                journalCatBean = journalCatService.saveCat(bean, sysUser.getOrg_id());
                jsonResult.setData(journalCatBean);
                jsonResult.setMessage(Constants.ACTION_ADD);
                return jsonResult;
            } else {
                //修改
                //分类名称重复(分类名查找不包括本身)
                List<JournalCat> journalCats = journalCatService.selectOtherByCatName(sysUser.getOrg_id(), bean.getJournal_cat_name(), bean.getJournal_cat_id());
                if (journalCats.size() > 0) {
                    return JsonResult.getError(Constants.CATNAME_REPETITION);
                }
                //是否自建(1:自建;2:分配)
                if (bean.getSource_type() == 2) {
                    //分配只更新关系表数据
                    JournalCat cat = journalCatService.selectByCatId(bean.getJournal_cat_id());

                    //不可修改总部数据的层次、名称、备注信息
                    if ((!cat.getPid().equals(bean.getPid())) || (!cat.getJournal_cat_name().equals(bean.getJournal_cat_name()))) {
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
                journalCatBean = journalCatService.updateCat(bean, sysUser.getOrg_id());
                jsonResult.setData(journalCatBean);
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
    @RequestMapping(value = "/admin/journalCat/json/updateEnabled", method = RequestMethod.POST)
    @ResponseBody
    public Object updateEnabled(HttpServletRequest request, @RequestBody JournalCatBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser.getOrg_id() == 1) {
                //是否启用(1:启用,2:不启用)
                journalCatService.updateEnabled(bean);
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
    @RequestMapping(value = "/admin/journalCat/json/updateShow", method = RequestMethod.POST)
    @ResponseBody
    public Object updateShow(HttpServletRequest request, @RequestBody JournalCatBean bean) {
        try {
            if (bean.getEnabled() == 2) {
                return JsonResult.getError("期刊分类未启用！");
            }
            journalCatService.updateShow(bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(bean);
            return result;
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
    @RequestMapping(value = "/admin/journalCat/delete_journal_cat", method = {RequestMethod.POST})
    @ResponseBody
    public Object delete_journal_cat(HttpServletRequest request, @RequestBody DelJournalCatBean delCatBean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //需要删除的期刊分类（1,2,3）
            Long[] journal_cat_ids = delCatBean.getJournal_cat_ids();

            //只能删除本机构的分类：is_delete
            for (int i = 0; i < journal_cat_ids.length; i++) {
                List<Long> catids = journalCatService.selectOrgCatIds(sysUser.getOrg_id());
                if (!catids.contains(journal_cat_ids[i])) {
                    return JsonResult.getException("只能删除本机构的分类！");
                }
            }
            String cat_ids = StringUtils.join(journal_cat_ids, ",");
            //通过cat_ids找到full_path合集
            List<String> full_paths = journalCatService.getFullPathsByCatIds(cat_ids);
            //需要删除的所有期刊分类catId合集
            List<Long> catIdList = new ArrayList<Long>();
            for (String full_path : full_paths) {
                //通过full_path获取自身和所有子集的catId合集
                catIdList.addAll(journalCatService.getCatIdsByFullPath(full_path));
            }
            //自身和所有子集的catIds
            String catIds = StringUtils.join(catIdList, ",");
            //通过cat_ids查询期刊详情
            List<Journal> journals = journalService.selectByCatIds(catIds);
            if (StringUtil.isEmpty(delCatBean.getMark()) && journals.size() > 0) {
                return JsonResult.getError("分类下存在关联的期刊，是否删除？");
            }
            journalCatService.deleteAllByCatIds(catIds);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(new ArrayList());
            return jsonResult;

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
    @RequestMapping(value = "/admin/journalCat/json/addOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object addOrg(HttpServletRequest request, @RequestBody UpdateCatOrgBean bean) {
        try {
            Long org_id = bean.getOrg_id();
            if (org_id == 1) {
                return JsonResult.getError("不能为长江科技分配期刊分类！");
            }
            journalCatService.addOrg(bean.getJournal_cat_id(), org_id);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
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
    @RequestMapping(value = "/admin/journalCat/json/removeOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object removeOrg(HttpServletRequest request, @RequestBody UpdateCatOrgBean bean) {
        try {
            if (bean.getOrg_id() == 1) {
                return JsonResult.getError("不能移除长江科技的期刊分类！");
            }
            journalCatService.removeOrg(bean.getJournal_cat_id(), bean.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
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
    @RequestMapping(value = "/admin/journalCat/json/orgList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object orgList(HttpServletRequest request, @RequestBody FindCatOrgBean bean) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "is_delete").and(new Sort(Sort.Direction.ASC, "org_id"));
            Object obj = journalCatService.getOrgsPageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(obj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
