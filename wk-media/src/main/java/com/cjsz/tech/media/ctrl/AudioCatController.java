package com.cjsz.tech.media.ctrl;

import com.cjsz.tech.media.beans.*;
import com.cjsz.tech.media.domain.Audio;
import com.cjsz.tech.media.domain.AudioCat;
import com.cjsz.tech.media.service.AudioCatService;
import com.cjsz.tech.media.service.AudioService;
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
 * Created by Li Yi on 2016/12/7.
 */
@Controller
public class AudioCatController {

    @Autowired
    AudioService audioService;

    @Autowired
    AudioCatService audioCatService;

    @Autowired
    UserService userService;

    /**
     * 音频分类树(音频分类列表用)
     *
     * @return
     */
    @RequestMapping(value = "/admin/audioCat/getTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getTree(HttpServletRequest request) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //获取当前用户机构全部音频分类，转化为树形结构
            List<AudioCatBean> audioCats = audioCatService.getCats(sysUser.getOrg_id());
            List<AudioCatBean> cats = audioCatService.selectTree(audioCats);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(cats);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 音频分类树(本机构自建，音频分类详情用)
     *
     * @return
     */
    @RequestMapping(value = "/admin/audioCat/getOrgTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getOrgTree(HttpServletRequest request) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //获取当前用户机构全部音频分类，转化为树形结构
            List<AudioCatBean> audioCats = audioCatService.getOrgCats(sysUser.getOrg_id());
            List<AudioCatBean> cats = audioCatService.selectTree(audioCats);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(cats);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增,修改音频分类
     *
     * @return
     */
    @RequestMapping(value = "/admin/audioCat/save_audio_cat", method = RequestMethod.POST)
    @ResponseBody
    public Object save_audio_cat(HttpServletRequest request, @RequestBody AudioCatBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            JsonResult jsonResult = JsonResult.getSuccess("");
            AudioCatBean audioCatBean = null;
            if (bean.getAudio_cat_id() == null) {
                //新增
                //分类名称重复(分类名查找)
                List<AudioCat> audioCats = audioCatService.selectByCatName(sysUser.getOrg_id(), bean.getAudio_cat_name());
                if (audioCats.size() > 0) {
                    return JsonResult.getError(Constants.CATNAME_REPETITION);
                }
                audioCatBean = audioCatService.saveCat(bean, sysUser.getOrg_id());
                jsonResult.setData(audioCatBean);
                jsonResult.setMessage(Constants.ACTION_ADD);
                return jsonResult;
            } else {
                //修改
                //分类名称重复(分类名查找不包括本身)
                List<AudioCat> audioCats = audioCatService.selectOtherByCatName(sysUser.getOrg_id(), bean.getAudio_cat_name(), bean.getAudio_cat_id());
                if (audioCats.size() > 0) {
                    return JsonResult.getError(Constants.CATNAME_REPETITION);
                }
                //是否自建(1:自建;2:分配)
                if (bean.getSource_type() == 2) {
                    //分配只更新关系表数据
                    AudioCat cat = audioCatService.selectByCatId(bean.getAudio_cat_id());

                    //不可修改总部数据的层次、名称、备注信息
                    if ((!cat.getPid().equals(bean.getPid())) || (!cat.getAudio_cat_name().equals(bean.getAudio_cat_name()))) {
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
                audioCatBean = audioCatService.updateCat(bean, sysUser.getOrg_id());
                jsonResult.setData(audioCatBean);
                jsonResult.setMessage(Constants.ACTION_UPDATE);
                return jsonResult;
            }
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 音频分类 启用
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/audioCat/json/updateEnabled", method = RequestMethod.POST)
    @ResponseBody
    public Object updateEnabled(HttpServletRequest request, @RequestBody AudioCatBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser.getOrg_id() == 1) {
                //是否启用(1:启用,2:不启用)
                audioCatService.updateEnabled(bean);
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
     * 音频分类 显示
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/audioCat/json/updateShow", method = RequestMethod.POST)
    @ResponseBody
    public Object updateShow(HttpServletRequest request, @RequestBody AudioCatBean bean) {
        try {
            if (bean.getEnabled() == 2) {
                return JsonResult.getError("音频分类未启用！");
            }
            audioCatService.updateShow(bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(bean);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 音频分类列表 删除数据
     *
     * @param request
     * @param delCatBean
     */
    @RequestMapping(value = "/admin/audioCat/delete_audio_cat", method = {RequestMethod.POST})
    @ResponseBody
    public Object delete_audio_cat(HttpServletRequest request, @RequestBody DelAudioCatBean delCatBean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //需要删除的音频分类（1,2,3）
            Long[] audio_cat_ids = delCatBean.getAudio_cat_ids();

            //只能删除本机构的分类：is_delete
            for (int i = 0; i < audio_cat_ids.length; i++) {
                List<Long> catids = audioCatService.selectOrgCatIds(sysUser.getOrg_id());
                if (!catids.contains(audio_cat_ids[i])) {
                    return JsonResult.getException("只能删除本机构的分类！");
                }
            }
            String cat_ids = StringUtils.join(audio_cat_ids, ",");
            //通过cat_ids找到full_path合集
            List<String> full_paths = audioCatService.getFullPathsByCatIds(cat_ids);
            //需要删除的所有音频分类catId合集
            List<Long> catIdList = new ArrayList<Long>();
            for (String full_path : full_paths) {
                //通过full_path获取自身和所有子集的catId合集
                catIdList.addAll(audioCatService.getCatIdsByFullPath(full_path));
            }
            //自身和所有子集的catIds
            String catIds = StringUtils.join(catIdList, ",");
            //通过cat_ids查询音频详情
            List<Audio> audios = audioService.selectByCatIds(catIds);
            if (StringUtil.isEmpty(delCatBean.getMark()) && audios.size() > 0) {
                return JsonResult.getError("分类下存在关联的音频，是否删除？");
            }
            audioCatService.deleteAllByCatIds(catIds);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(new ArrayList());
            return jsonResult;

        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 音频分类添加机构
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/audioCat/json/addOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object addOrg(HttpServletRequest request, @RequestBody UpdateCatOrgBean bean) {
        try {
            Long org_id = bean.getOrg_id();
            if (org_id == 1) {
                return JsonResult.getError("不能为长江科技分配音频分类！");
            }
            audioCatService.addOrg(bean.getAudio_cat_id(), org_id);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 音频分类移除机构
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/audioCat/json/removeOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object removeOrg(HttpServletRequest request, @RequestBody UpdateCatOrgBean bean) {
        try {
            if (bean.getOrg_id() == 1) {
                return JsonResult.getError("不能移除长江科技的音频分类！");
            }
            audioCatService.removeOrg(bean.getAudio_cat_id(), bean.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 音频分类机构列表
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/audioCat/json/orgList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object orgList(HttpServletRequest request, @RequestBody FindCatOrgBean bean) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "is_delete").and(new Sort(Sort.Direction.ASC, "org_id"));
            Object obj = audioCatService.getOrgsPageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(obj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
