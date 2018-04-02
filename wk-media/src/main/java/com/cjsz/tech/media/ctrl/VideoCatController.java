package com.cjsz.tech.media.ctrl;

import com.cjsz.tech.media.beans.*;
import com.cjsz.tech.media.domain.Video;
import com.cjsz.tech.media.domain.VideoCat;
import com.cjsz.tech.media.service.VideoCatService;
import com.cjsz.tech.media.service.VideoService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Li Yi on 2016/12/6.
 */
@Controller
public class VideoCatController {

    @Autowired
    VideoService videoService;

    @Autowired
    VideoCatService videoCatService;

    @Autowired
    UserService userService;


    /**
     * 视频分类树(视频分类列表用)
     *
     * @return
     */
    @RequestMapping(value = "/admin/videoCat/getTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getTree(HttpServletRequest request) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //获取当前用户机构全部视频分类，转化为树形结构
            List<VideoCatBean> articleCats = videoCatService.getCats(sysUser.getOrg_id());
            List<VideoCatBean> cats = videoCatService.selectTree(articleCats);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(cats);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 视频分类树(本机构自建，视频分类详情用)
     *
     * @return
     */
    @RequestMapping(value = "/admin/videoCat/getOrgTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getOrgTree(HttpServletRequest request) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //获取当前用户机构全部视频分类，转化为树形结构
            List<VideoCatBean> articleCats = videoCatService.getOrgCats(sysUser.getOrg_id());
            List<VideoCatBean> cats = videoCatService.selectTree(articleCats);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(cats);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增,修改视频分类
     *
     * @return
     */
    @RequestMapping(value = "/admin/videoCat/save_video_cat", method = RequestMethod.POST)
    @ResponseBody
    public Object save_article_cat(HttpServletRequest request, @RequestBody VideoCatBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            JsonResult jsonResult = JsonResult.getSuccess("");
            VideoCatBean videoCatBean = null;
            if (bean.getVideo_cat_id() == null) {
                //新增
                //分类名称重复(分类名查找)
                List<VideoCat> videoCats = videoCatService.selectByCatName(sysUser.getOrg_id(), bean.getVideo_cat_name());
                if (videoCats.size() > 0) {
                    return JsonResult.getError(Constants.CATNAME_REPETITION);
                }
                videoCatBean = videoCatService.saveCat(bean, sysUser.getOrg_id());
                jsonResult.setData(videoCatBean);
                jsonResult.setMessage(Constants.ACTION_ADD);
                return jsonResult;
            } else {
                //修改
                //分类名称重复(分类名查找不包括本身)
                List<VideoCat> videoCats = videoCatService.selectOtherByCatName(sysUser.getOrg_id(), bean.getVideo_cat_name(), bean.getVideo_cat_id());
                if (videoCats.size() > 0) {
                    return JsonResult.getError(Constants.CATNAME_REPETITION);
                }
                //是否自建(1:自建;2:分配)
                if (bean.getSource_type() == 2) {
                    //分配只更新关系表数据
                    VideoCat cat = videoCatService.selectByCatId(bean.getVideo_cat_id());

                    //不可修改总部数据的层次、名称、备注信息
                    if ((!cat.getPid().equals(bean.getPid())) || (!cat.getVideo_cat_name().equals(bean.getVideo_cat_name()))) {
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
                videoCatBean = videoCatService.updateCat(bean, sysUser.getOrg_id());
                jsonResult.setData(videoCatBean);
                jsonResult.setMessage(Constants.ACTION_UPDATE);
                return jsonResult;
            }
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 视频分类 启用
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/videoCat/json/updateEnabled", method = RequestMethod.POST)
    @ResponseBody
    public Object updateEnabled(HttpServletRequest request, @RequestBody VideoCatBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser.getOrg_id() == 1) {
                //是否启用(1:启用,2:不启用)
                videoCatService.updateEnabled(bean);
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
     * 视频分类 显示
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/videoCat/json/updateShow", method = RequestMethod.POST)
    @ResponseBody
    public Object updateShow(HttpServletRequest request, @RequestBody VideoCatBean bean) {
        try {
            if (bean.getEnabled() == 2) {
                return JsonResult.getError("视频分类未启用！");
            }
            videoCatService.updateShow(bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(bean);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 视频分类列表 删除数据
     *
     * @param request
     * @param delCatBean
     */
    @RequestMapping(value = "/admin/videoCat/delete_video_cat", method = {RequestMethod.POST})
    @ResponseBody
    public Object delete_article_cat(HttpServletRequest request, @RequestBody DelVideoCatBean delCatBean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //需要删除的视频分类（1,2,3）
            Long[] video_cat_ids = delCatBean.getVideo_cat_ids();

            //只能删除本机构的分类：is_delete
            for (int i = 0; i < video_cat_ids.length; i++) {
                List<Long> catids = videoCatService.selectOrgCatIds(sysUser.getOrg_id());
                if (!catids.contains(video_cat_ids[i])) {
                    return JsonResult.getException("只能删除本机构的分类！");
                }
            }
            String cat_ids = StringUtils.join(video_cat_ids, ",");
            //通过cat_ids找到full_path合集
            List<String> full_paths = videoCatService.getFullPathsByCatIds(cat_ids);
            //需要删除的所有视频分类catId合集
            List<Long> catIdList = new ArrayList<Long>();
            for (String full_path : full_paths) {
                //通过full_path获取自身和所有子集的catId合集
                catIdList.addAll(videoCatService.getCatIdsByFullPath(full_path));
            }
            //自身和所有子集的catIds
            String catIds = StringUtils.join(catIdList, ",");
            //通过cat_ids查询视频详情
            List<Video> articles = videoService.selectByCatIds(catIds);
            if (StringUtil.isEmpty(delCatBean.getMark()) && articles.size() > 0) {
                return JsonResult.getError("分类下存在关联的视频，是否删除？");
            }
            videoCatService.deleteAllByCatIds(catIds);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(new ArrayList());
            return jsonResult;

        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 视频分类添加机构
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/videoCat/json/addOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object addOrg(HttpServletRequest request, @RequestBody UpdateCatOrgBean bean) {
        try {
            Long org_id = bean.getOrg_id();
            if (org_id == 1) {
                return JsonResult.getError("不能为长江科技分配视频分类！");
            }
            videoCatService.addOrg(bean.getVideo_cat_id(), org_id);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 视频分类移除机构
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/videoCat/json/removeOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object removeOrg(HttpServletRequest request, @RequestBody UpdateCatOrgBean bean) {
        try {
            if (bean.getOrg_id() == 1) {
                return JsonResult.getError("不能移除长江科技的视频分类！");
            }
            videoCatService.removeOrg(bean.getVideo_cat_id(), bean.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 视频分类机构列表
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/videoCat/json/orgList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object orgList(HttpServletRequest request, @RequestBody FindCatOrgBean bean) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "is_delete").and(new Sort(Sort.Direction.ASC, "org_id"));
            Object obj = videoCatService.getOrgsPageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(obj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
