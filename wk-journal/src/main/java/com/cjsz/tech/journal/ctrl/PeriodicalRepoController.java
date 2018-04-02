package com.cjsz.tech.journal.ctrl;

import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.beans.FileForm;
import com.cjsz.tech.beans.ImagesBean;
import com.cjsz.tech.beans.MultiFileForm;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.journal.beans.FindJournalBean;
import com.cjsz.tech.journal.beans.FindPeriodicalBean;
import com.cjsz.tech.journal.beans.FindSeriesBean;
import com.cjsz.tech.journal.beans.UpdateEnabledBean;
import com.cjsz.tech.journal.domain.PeriodicalChild;
import com.cjsz.tech.journal.domain.PeriodicalRepo;
import com.cjsz.tech.journal.mapper.PeriodicalChildMapper;
import com.cjsz.tech.journal.service.PeriodicalRepoService;
import com.cjsz.tech.journal.service.UnPeriodicalService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.BaseService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.IDUtil;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.utils.images.ImageHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
@Controller
public class PeriodicalRepoController {

    @Autowired
    PeriodicalRepoService periodicalRepoService;

    @Autowired
    UserService userService;

    @Autowired
    BaseService baseService;

    @Autowired
    PeriodicalChildMapper periodicalChildMapper;

    @Autowired
    UnPeriodicalService unPeriodicalService;

    /**
     * pdf上传转image
     *
     * @param request
     * @param multiFileForm
     * @return
     */
    @RequestMapping(value = "/admin/periodical/uploadPdf", method = {RequestMethod.POST})
    @ResponseBody
    public Object uploadPdf(HttpServletRequest request, @RequestBody MultiFileForm multiFileForm) {
        try {
            List<FileForm> fileForms = multiFileForm.getData();
            if (null != fileForms && fileForms.size() > 0) {
                periodicalRepoService.uploadPeriodicals(fileForms);
                return JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            } else {
                return JsonResult.getError(Constants.ACTION_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改 期刊
     *
     * @param request
     * @param repo
     * @return
     */
    @RequestMapping(value = "/admin/periodical/updatePeriodical", method = RequestMethod.POST)
    @ResponseBody
    public Object updatePeriodical(HttpServletRequest request, @RequestBody PeriodicalRepo repo) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (user.getOrg_id().intValue() != 1) {
                return JsonResult.getError(Constants.INIT_REFUSED);
            }
            if (repo.getPeriodical_id() == null) {
                return JsonResult.getError(Constants.JOURNAL_NOT_EXIST);
            } else {
                //期刊表数据修改
                PeriodicalRepo art = periodicalRepoService.selectById(repo.getPeriodical_id());
                if (art == null) {
                    return JsonResult.getError(Constants.JOURNAL_NOT_EXIST);
                }
                //是否需要修改封面
                if (StringUtils.isNotEmpty(repo.getPeriodical_cover()) && !repo.getPeriodical_cover().equals(art.getPeriodical_cover())) {
                    //图片处理，压缩图片
                    String cover_small = baseService.getImgScale(repo.getPeriodical_cover(), "small", 0.5);
                    repo.setPeriodical_cover_small(cover_small);
                }
                periodicalRepoService.updateRepo(repo);
                //修改所有分类
                if (repo.getPeriodical_id() != null && repo.getSeries_name() != null) {
                    periodicalRepoService.setPeriodicalAllToCat(repo.getPeriodical_cat_id(), repo.getSeries_name());
                }
                //修改所有简介
                if (repo.getPeriodical_remark() != null && repo.getSeries_name() != null) {
                    periodicalRepoService.setPeriodicalAllToRemark(repo.getPeriodical_remark(), repo.getSeries_name());
                }

                return JsonResult.getSuccess(Constants.ACTION_UPDATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改 期刊
     *
     * @param request
     * @param repo
     * @return
     */
    @RequestMapping(value = "/admin/periodical/updatePeriodicalFile", method = RequestMethod.POST)
    @ResponseBody
    public Object updatePeriodicalFile(HttpServletRequest request, @RequestBody PeriodicalRepo repo) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (user.getOrg_id().intValue() != 1) {
                return JsonResult.getError(Constants.INIT_REFUSED);
            }
            if (repo.getPeriodical_id() == null) {
                return JsonResult.getError(Constants.JOURNAL_NOT_EXIST);
            } else {
                //期刊表数据修改
                PeriodicalRepo art = periodicalRepoService.selectById(repo.getPeriodical_id());
                if (art == null) {
                    return JsonResult.getError(Constants.JOURNAL_NOT_EXIST);
                }
                art.setChild_file_name(repo.getChild_file_name());
                art.setTotal_page(repo.getTotal_page());
                art.setPeriodical_status(1);
                periodicalRepoService.updateRepo(repo);
                //修改解析任务状态
                unPeriodicalService.updateUnStatus(repo.getSeries_name());
                return JsonResult.getSuccess(Constants.ACTION_UPDATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 期刊详情 发布、取消发布，推荐，头条
     *
     * @param request
     * @param repo
     * @return
     */
    @RequestMapping(value = "/admin/periodical/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object updateStatus(HttpServletRequest request, @RequestBody PeriodicalRepo repo) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (user.getOrg_id().intValue() != 1) {
                return JsonResult.getError(Constants.INIT_REFUSED);
            }
            PeriodicalRepo art = periodicalRepoService.selectById(repo.getPeriodical_id());
            if (art == null) {
                return JsonResult.getError(Constants.JOURNAL_NOT_EXIST);
            }
            return periodicalRepoService.updateStatus(repo);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改系列发布状态
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/periodical/updateEnabled", method = RequestMethod.POST)
    @ResponseBody
    public Object updateEnabled(HttpServletRequest request, @RequestBody UpdateEnabledBean bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (bean.getEnabled() == null || StringUtils.isEmpty(bean.getSeries_name())) {
                return JsonResult.getError("请提供参数");
            }
            periodicalRepoService.updateEnabled(bean.getEnabled(), bean.getSeries_name());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 期刊页面 分页查询
     *
     * @return
     */
    @RequestMapping(value = "/admin/periodical/getList", method = RequestMethod.POST)
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody FindPeriodicalBean bean) {
        try {
            if (bean.getPageNum() == null || bean.getPageSize() == null) {
                return JsonResult.getError("请提供分页参数");
            }
            if (bean.getOrg_id() == null) {
                return JsonResult.getError("请提供机构ID");
            }
            if (bean.getPeriodical_cat_id() == null) {
                bean.setPeriodical_cat_id(0L);
            }
            Object data = periodicalRepoService.pageQuery(bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 期刊阅读获取图片url
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/read/periodical/childImg", method = RequestMethod.POST)
    @ResponseBody
    public Object childImg(HttpServletRequest request) {
        try {
            String periodical_id = request.getParameter("periodical_id");
            if (StringUtils.isEmpty(periodical_id)) {
                return JsonResult.getError("请提供期刊编号");
            }
            List<PeriodicalChild> result = periodicalRepoService.getImg(Long.parseLong(periodical_id));
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 获取期刊列表（同系列显示最新一本）
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/site/periodical/periodicalList", method = RequestMethod.POST)
    @ResponseBody
    public Object periodicalList(HttpServletRequest request, FindPeriodicalBean bean) {
        try {
            if (bean.getOrg_id() == null) {
                return JsonResult.getError("请提供机构ID");
            }
            if (bean.getPageSize() == null || bean.getPageNum() == null) {
                return JsonResult.getError("请提供分页参数");
            }
            if (bean.getPeriodical_cat_id() == null) {
                return JsonResult.getError("请提供分类ID");
            }
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight");
            Object list = periodicalRepoService.webGetList(bean, sort);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(list);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取同系列期刊
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/site/periodical/suggestList", method = RequestMethod.POST)
    @ResponseBody
    public Object suggestList(HttpServletRequest request, @RequestBody FindSeriesBean bean) {
        try {
            if (StringUtils.isEmpty(bean.getSeries_name())) {
                return JsonResult.getError("请提供系列名称");
            }
            if (bean.getOrg_id() == null) {
                return JsonResult.getError("请提供机构ID");
            }
            List<PeriodicalRepo> list = periodicalRepoService.getSuggestList(bean.getPeriodical_id(), bean.getOrg_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(list);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 后台-获取期刊列表（同系列显示最新一本）
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/periodical/periodicalList", method = RequestMethod.POST)
    @ResponseBody
    public Object adminPeriodicalList(HttpServletRequest request, @RequestBody FindPeriodicalBean bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            bean.setOrg_id(user.getOrg_id());
            if (bean.getPageSize() == null || bean.getPageNum() == null) {
                return JsonResult.getError("请提供分页参数");
            }
            if (bean.getPeriodical_cat_id() == null) {
                return JsonResult.getError("请提供分类ID");
            }
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight");
            Object list = periodicalRepoService.getList(bean, sort);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(list);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 后台-获取同系列期刊
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/periodical/sameList", method = RequestMethod.POST)
    @ResponseBody
    public Object sameList(HttpServletRequest request, @RequestBody FindSeriesBean bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (StringUtils.isEmpty(bean.getSeries_name())) {
                return JsonResult.getError("请提供系列名称");
            }
            List<PeriodicalRepo> list = periodicalRepoService.getSameBySNameAll(bean.getSeries_name());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(list);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


}
