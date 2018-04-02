package com.cjsz.tech.journal.ctrl;

import com.cjsz.tech.journal.beans.DelJournalBean;
import com.cjsz.tech.journal.beans.FindJournalBean;
import com.cjsz.tech.journal.domain.Journal;
import com.cjsz.tech.journal.service.JournalCatService;
import com.cjsz.tech.journal.service.JournalService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.BaseService;
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

/**
 * Created by Administrator on 2016/11/22 0022.
 */
@Controller
public class JournalController {

    @Autowired
    private JournalService journalService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private UserService userService;

    @Autowired
    private JournalCatService journalCatService;

    /**
     * 期刊页面 分页查询
     * @return
     */
    @RequestMapping(value = "/admin/journal/json/list", method = RequestMethod.POST)
    @ResponseBody
    public Object journal_list(@RequestBody FindJournalBean bean){
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight");
            Object data = journalService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增/修改 期刊
     * @param request
     * @param journal
     * @return
     */
    @RequestMapping(value = "/admin/journal/json/updateJournal", method = RequestMethod.POST)
    @ResponseBody
    public Object updateJournal(HttpServletRequest request, @RequestBody Journal journal){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            Long cat_id = journalCatService.getCatId(journal.getJournal_cat_id(),user.getOrg_id());
            if (cat_id==null){
                return JsonResult.getOther("不是自建的不能进行操作！");
            }
            if(journal.getJournal_id()==null){
                //期刊表数据添加
                //图片处理，压缩图片
                String journal_cover_small = baseService.getImgScale(journal.getJournal_cover(), "small", 0.5);
                journal.setJournal_cover_small(journal_cover_small);
                if(StringUtils.isNotEmpty(journal.getAndroid_qr_code())){
                    String android_qr_code_small = baseService.getImgScale(journal.getAndroid_qr_code(), "small", 0.5);
                    journal.setAndroid_qr_code_small(android_qr_code_small);
                }
                if(StringUtils.isNotEmpty(journal.getIos_qr_code())){
                    String ios_qr_code_small = baseService.getImgScale(journal.getIos_qr_code(), "small", 0.5);
                    journal.setIos_qr_code_small(ios_qr_code_small);
                }
                journal = journalService.saveJournal(journal, user.getUser_id(), user.getOrg_id());
                result.setMessage(Constants.ACTION_ADD);
            }else{
                //期刊表数据修改
                Journal art = journalService.selectById(journal.getJournal_id());
                if(art == null){
                    return JsonResult.getError(Constants.JOURNAL_NOT_EXIST);
                }
                /*if(!journal.getJournal_cover().equals(art.getJournal_cover())){
                    //图片处理，压缩图片
                    String cover_url_small = baseService.getImgScale(journal.getJournal_cover(), "small", 0.5);
                    journal.setJournal_cover_small(cover_url_small);
                }
                if(!journal.getAndroid_qr_code().equals(art.getAndroid_qr_code())){
                    //图片处理，压缩图片
                    String android_qr_code_small = baseService.getImgScale(journal.getAndroid_qr_code(), "small", 0.5);
                    journal.setAndroid_qr_code_small(android_qr_code_small);
                }
                if(!journal.getIos_qr_code().equals(art.getIos_qr_code())){
                    //图片处理，压缩图片
                    String ios_qr_code_small = baseService.getImgScale(journal.getIos_qr_code(), "small", 0.5);
                    journal.setIos_qr_code_small(ios_qr_code_small);
                }*/
                if(StringUtils.isNotEmpty(journal.getJournal_cover())){
                    //图片处理，压缩图片
                    String cover_url_small = baseService.getImgScale(journal.getJournal_cover(), "small", 0.5);
                    journal.setJournal_cover_small(cover_url_small);
                }
                if(StringUtils.isNotEmpty(journal.getAndroid_qr_code())){
                    //图片处理，压缩图片
                    String android_qr_code_small = baseService.getImgScale(journal.getAndroid_qr_code(), "small", 0.5);
                    journal.setAndroid_qr_code_small(android_qr_code_small);
                }
                if(StringUtils.isNotEmpty(journal.getIos_qr_code())){
                    //图片处理，压缩图片
                    String ios_qr_code_small = baseService.getImgScale(journal.getIos_qr_code(), "small", 0.5);
                    journal.setIos_qr_code_small(ios_qr_code_small);
                }
                journal = journalService.updateJournal(journal, user.getUser_id());
                result.setMessage(Constants.ACTION_UPDATE);
            }

            result.setData(journal);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 期刊详情 发布、取消发布，推荐，头条
     * @param request
     * @param journal
     * @return
     */
    @RequestMapping(value = "/admin/journal/json/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object updateStatus(HttpServletRequest request, @RequestBody Journal journal){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if(user.getOrg_id() != 1){
                return JsonResult.getError(Constants.INIT_REFUSED);
            }
            Journal art = journalService.selectById(journal.getJournal_id());
            if(art == null){
                return JsonResult.getError(Constants.JOURNAL_NOT_EXIST);
            }
            Object object = journalService.updateStatus(journal, user.getUser_id());
            return object;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除期刊
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/journal/json/deleteJournals", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteJournals(HttpServletRequest request, @RequestBody DelJournalBean bean){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            for (Long journal_id:bean.getJournal_ids()) {
                Long cat_id= journalService.getCatId(journal_id);
                Long journal_cat_id = journalCatService.getCatId(cat_id,user.getOrg_id());
                if (journal_cat_id == null){
                    return JsonResult.getOther("不是自建的不能进行操作！");
                }
                Long[] journal_ids = bean.getJournal_ids();
                String journal_ids_str = StringUtils.join(journal_ids, ",");
                journalService.deleteByJournalIds(journal_ids_str);
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
