package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.media.beans.FindVideoBean;
import com.cjsz.tech.media.beans.VideoCatBean;
import com.cjsz.tech.media.domain.VideoCat;
import com.cjsz.tech.media.service.VideoCatService;
import com.cjsz.tech.media.service.VideoService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class WebVideoController {

    @Autowired
    private VideoCatService videoCatService;

    @Autowired
    private VideoService videoService;

    @Autowired
    ProOrgExtendService proOrgExtendService;


    @RequestMapping("/web/video")
    public ModelAndView search(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("web/err");
        return mv;
    }

    @RequestMapping("/web/video/{cat_id}")
    public ModelAndView search(HttpServletRequest request, @PathVariable String cat_id) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            FindVideoBean bean = new FindVideoBean();
            bean.setPageNum(1);
            bean.setPageSize(15);
            bean.setOrg_id(Long.valueOf(org_id));
            bean.setVideo_cat_id(Long.valueOf(cat_id));
            Object videoList = videoService.pageSiteQuery(sort, bean);
            mv.addObject("videoList", videoList);

            VideoCat videoCat = videoCatService.selectByCatId(Long.valueOf(cat_id));
            if (videoCat != null) {
                List<VideoCatBean> videoCats = videoCatService.getOwnerCats(Long.valueOf(org_id), videoCat.getVideo_cat_path());
                List<VideoCatBean> cats = videoCatService.selectTree(videoCats);
                if (cats.size() > 0) {
                    mv.addObject("videoCatList", cats.get(0));
                    request.setAttribute("cat_id", cat_id);
                    mv.setViewName(tempName + "/video");
                } else {
                    mv.setViewName("web/err");
                }
            } else {
                mv.setViewName("web/err");
            }
        } else {
            mv.setViewName("web/404");
        }
        return mv;
    }

    /**
     * 视频分类
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/site/videoCat/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getCatList(HttpServletRequest request) {
        try {
            String cat_id = request.getParameter("cid");
            String org_id = request.getAttribute("org_id").toString();
            VideoCat videoCat = videoCatService.selectByCatId(Long.valueOf(cat_id));
            List<VideoCatBean> videoCats = videoCatService.getOwnerCats(Long.valueOf(org_id), videoCat.getVideo_cat_path());
            List<VideoCatBean> cats = videoCatService.selectTree(videoCats);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(cats);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 视频列表
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/site/video/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getVideoList(HttpServletRequest request, FindVideoBean bean) {
        try {
            String org_id = request.getAttribute("org_id").toString();
            bean.setOrg_id(Long.valueOf(org_id));
            String cat_id = request.getParameter("cid");
            if (cat_id != null && StringUtils.isNotEmpty(cat_id)) {
                bean.setVideo_cat_id(Long.valueOf(cat_id));
            } else {
                bean.setVideo_cat_id(0L);
            }
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            Object data = videoService.pageSiteQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
