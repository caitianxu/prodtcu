package com.cjsz.tech.media.ctrl;

import com.cjsz.tech.media.beans.DelAudioBean;
import com.cjsz.tech.media.beans.FindAudioBean;
import com.cjsz.tech.media.domain.Audio;
import com.cjsz.tech.media.service.AudioCatService;
import com.cjsz.tech.media.service.AudioService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * Created by Li Yi on 2016/12/7.
 */
@Controller
@RequestMapping("/admin/audio")
public class AudioController {

    @Autowired
    private AudioService audioService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private UserService userService;

    @Autowired
    private AudioCatService audioCatService;

    /**
     * 音频分页查询页面
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Object audio_list(@RequestBody FindAudioBean bean){
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight");
            Object data = audioService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增、修改音频
     *
     * @param request
     * @param audio
     * @return
     */
    @RequestMapping(value = "/updateAudio", method = RequestMethod.POST)
    @ResponseBody
    public Object updateAudio(HttpServletRequest request, @RequestBody Audio audio){
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //查询是自建 还是分配
            Long audio_cat_id=audioCatService.getAudioCatId(audio.getAudio_cat_id(),sysUser.getOrg_id());
            JsonResult result = JsonResult.getSuccess("");
            if(audio_cat_id == null){
                return JsonResult.getOther("不是自建的不能进行操作！");
            }
            if (audio.getAudio_id() == null) {
                //音频数据添加
                audio.setUser_id(sysUser.getUser_id());
                audio.setOrg_id(sysUser.getOrg_id());

                //图片处理，压缩图片
                String cover_url_small = baseService.getImgScale(audio.getCover_url(), "small", 0.5);
                audio.setCover_url_small(cover_url_small);
                audio.setIs_delete(2);
                audio = audioService.saveAudio(audio);
                result.setMessage(Constants.ACTION_ADD);
            } else {
                //音频数据修改

                Audio org_audio = audioService.selectById(audio.getAudio_id());
            /*if(!audio.getCover_url().equals(org_audio.getCover_url())){
                //图片处理，压缩图片
                String cover_url_small = baseService.getImgScale(audio.getCover_url(), "small", 0.5);
                audio.setCover_url_small(cover_url_small);
            }*/
                if (StringUtils.isNotEmpty(audio.getCover_url())) {
                    //图片处理，压缩图片
                    String cover_url_small = baseService.getImgScale(audio.getCover_url(), "small", 0.5);
                    audio.setCover_url_small(cover_url_small);
                }
                audio.setIs_delete(2);
                audio.setUser_id(sysUser.getUser_id());
                audio = audioService.updateAudio(audio);
                result.setMessage(Constants.ACTION_UPDATE);
            }
            result.setData(audio);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除音频
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/deleteAudio", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteAudios(HttpServletRequest request,@RequestBody DelAudioBean bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            //查询是自建 还是分配
            for (Long audio_id : bean.getAudio_ids()) {
                Long cat_id = audioService.getCatId(audio_id);
                Long audioId = audioCatService.getAudioCatId(cat_id, user.getOrg_id());
                if (audioId == null) {
                    return JsonResult.getOther("不是自建的不能进行操作！");
                } else {
            Long[] audio_ids = bean.getAudio_ids();
            String audio_ids_str = StringUtils.join(audio_ids, ",");
            audioService.deleteByIds(audio_ids_str);
            result.setMessage(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
