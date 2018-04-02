package com.cjsz.tech.media.ctrl;

import com.cjsz.tech.media.beans.DelVideoBean;
import com.cjsz.tech.media.beans.FindVideoBean;
import com.cjsz.tech.media.domain.Video;
import com.cjsz.tech.media.service.VideoCatService;
import com.cjsz.tech.media.service.VideoService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.BaseService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.system.utils.RequestContextUtil;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * Created by Li Yi on 2016/12/6.
 */
@Controller
@RequestMapping("/admin/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoCatService videoCatService;

    /**
     * 视频分页查询页面
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Object video_list(@RequestBody FindVideoBean bean){
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight");
            Object data = videoService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增、修改视频
     *
     * @param request
     * @param video
     * @return
     */
    @RequestMapping(value = "/updateVideo", method = RequestMethod.POST)
    @ResponseBody
    public Object updateVideo(HttpServletRequest request, @RequestBody Video video){
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            Long video_cat_id = videoCatService.getCatId(video.getVideo_cat_id(),sysUser.getOrg_id());
            if (video_cat_id==null){
                return JsonResult.getOther("不是自建的不能进行操作！");
            }
            if (video.getVideo_id() == null){
                video.setUser_id(sysUser.getUser_id());
                video.setOrg_id(sysUser.getOrg_id());

                //图片处理，压缩图片
                String cover_url_small = baseService.getImgScale(video.getCover_url(), "small", 0.5);
                video.setCover_url_small(cover_url_small);
                video.setIs_delete(2);
                video = videoService.saveVideo(video);
                result.setMessage(Constants.ACTION_ADD);
            }
            else {

                Video org_audio = videoService.selectById(video.getVideo_id());
                /*if(!video.getCover_url().equals(org_audio.getCover_url())){
                    //图片处理，压缩图片
                    String cover_url_small = baseService.getImgScale(video.getCover_url(), "small", 0.5);
                    video.setCover_url_small(cover_url_small);
                }*/
                if(StringUtils.isNotEmpty(video.getCover_url())){
                    //图片处理，压缩图片
                    String cover_url_small = baseService.getImgScale(video.getCover_url(), "small", 0.5);
                    video.setCover_url_small(cover_url_small);
                }
                video.setIs_delete(2);
                video.setUser_id(sysUser.getUser_id());
                video = videoService.updateVideo(video);
                result.setMessage(Constants.ACTION_UPDATE);
            }

            result.setData(video);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除视频
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/deleteVideo", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteVideos(HttpServletRequest request,@RequestBody DelVideoBean bean){
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            //查询是自建 还是分配
            for (Long video_id:bean.getVideo_ids()) {
                Long cat_id = videoService.getVideoCatId(video_id);
                Long video_cat_id = videoCatService.getCatId(cat_id,user.getOrg_id());
                if (video_cat_id==null){
                    return JsonResult.getOther("不是自建的不能进行操作！");
                }
                Long[] video_ids = bean.getVideo_ids();
                String video_ids_str = StringUtils.join(video_ids, ",");
                videoService.deleteByIds(video_ids_str);
                result.setMessage(Constants.ACTION_DELETE);
                result.setData(new ArrayList());
                return result;
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
