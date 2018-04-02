package com.cjsz.tech.api.service;

import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.book.beans.BookOffLineBean;
import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.cms.beans.ArticlePictureProxyItem;
import com.cjsz.tech.cms.domain.Article;
import com.cjsz.tech.cms.domain.ArticleCat;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.dev.domain.ConfContent;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.domain.DeviceSetting;
import com.cjsz.tech.journal.domain.Journal;
import com.cjsz.tech.journal.domain.JournalCat;
import com.cjsz.tech.journal.domain.Newspaper;
import com.cjsz.tech.journal.domain.NewspaperCat;
import com.cjsz.tech.media.domain.Audio;
import com.cjsz.tech.media.domain.AudioCat;
import com.cjsz.tech.media.domain.Video;
import com.cjsz.tech.media.domain.VideoCat;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.domain.AdvCat;
import com.cjsz.tech.system.service.IOfflineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 离线服务
 * Created by shiaihua on 16/12/22.
 */
@Service
public class FrontOffLineService {

    /**
     * 注册模块服务
     */
    private static LinkedHashMap<String, LinkedHashMap<String, IOfflineService>> offLineRegServices = new LinkedHashMap<String, LinkedHashMap<String, IOfflineService>>();


    /**
     * 获取各模块离线内容
     *
     * @param orgid
     * @param timev
     * @return
     */
    public Object getOffLineContent(String webpath, String module, Long orgid, String timev, Device deviceObj, Integer num, Integer size) {
        if (offLineRegServices.size() == 0) {
            regService();
        }
        TreeMap<String, Object> dataValues = new TreeMap<>();
        if ("all".equals(module)) {
            Iterator iter = offLineRegServices.keySet().iterator();
            while (iter.hasNext()) {
                String moduleKey = (String) iter.next();
                getModuleDatas(dataValues, moduleKey, webpath, orgid, timev, deviceObj, num, size);
            }
        } else {
            String[] modules = module.split(",");
            for (int mi = 0; mi < modules.length; mi++) {
                String curModule = modules[mi];
                getModuleDatas(dataValues, curModule, webpath, orgid, timev, deviceObj, num, size);
            }

        }
        return dataValues;
    }

    /**
     * 是否包含模块
     *
     * @param str
     * @return
     */
    public boolean isContainsModule(String str) {
        if (offLineRegServices.size() == 0) {
            regService();
        }
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        if ("all".equals(str)) {
            return true;
        }
        if (str.contains(",")) {
            String[] modules = str.split(",");
            for (int mi = 0; mi < modules.length; mi++) {
                boolean isContains = offLineRegServices.containsKey(str);
                if (!isContains) {
                    return false;
                }
            }
            return true;
        } else {
            return offLineRegServices.containsKey(str);
        }

    }

    /**
     * 按模块获取数据
     *
     * @param dataValues
     * @param moduleKey
     * @param webpath
     * @param orgid
     * @param timev
     */
    private void getModuleDatas(Map dataValues, String moduleKey, String webpath, Long orgid, String timev, Device dev, Integer pageNum, Integer pageSize) {
        Map<String, IOfflineService> offList = offLineRegServices.get(moduleKey);
        Iterator iter = offList.keySet().iterator();
        while (iter.hasNext()) {
            String serviceKey = (String) iter.next();
            IOfflineService service = offList.get(serviceKey);
            Integer num = 0;
            if(pageNum > 1){
                num = (pageNum - 1) * pageSize;
            }
            Integer size = pageNum * pageSize;
            List curList = service.getOffLineNumList(orgid, timev, dev.getDevice_id() , num, size);
            if (curList != null && curList.size() > 0) {
                replaceObjectUrl(webpath, curList, dev.getDevice_code());
//                if("news".equals(serviceKey)) {
//                    replaceNewsUrl(webpath,curList);
//                }else if("newscat".equals(serviceKey)) {
//                    replaceNewCatUrl(webpath,curList);
//                }else if("menu".equals(serviceKey)){
//                    replaceMenuUrl(webpath,curList);
//                }else if ("ui".equals(serviceKey)) {
//                    replaceObjectUrl(webpath,curList);
//                }else if("audiocat".equals(serviceKey)) {
//                    replaceObjectUrl(webpath,curList);
//                }else if("audio".equals(serviceKey)) {
//                    replaceObjectUrl(webpath,curList);
//                }else if("videocat".equals(serviceKey)) {
//                    replaceObjectUrl(webpath,curList);
//                }else if("video".equals(serviceKey)) {
//                    replaceObjectUrl(webpath,curList);
//                }else if("bookcat".equals(serviceKey)) {
//                    replaceObjectUrl(webpath,curList);
//                }else if("book".equals(serviceKey)) {
//                    replaceObjectUrl(webpath,curList);
//                }
                dataValues.put(serviceKey, curList);
            }
        }

    }

    private void replaceObjectUrl(String webpath, List curList, String devcode) {
        for (Object obj : curList) {
            if (obj instanceof DeviceSetting) {
                //设备配置-背景、LOGO等
                DeviceSetting setting = (DeviceSetting) obj;
                if (StringUtils.isNotEmpty(setting.getDetail_url())) {
                    setting.setDetail_url(webpath + setting.getDetail_url());
                }
                if (StringUtils.isNotEmpty(setting.getHome_url())) {
                    setting.setHome_url(webpath + setting.getHome_url());
                }
                if (StringUtils.isNotEmpty(setting.getList_url())) {
                    setting.setList_url(webpath + setting.getList_url());
                }
                if (StringUtils.isNotEmpty(setting.getLogo_url())) {
                    setting.setLogo_url(webpath + setting.getLogo_url());
                }
            } else if (obj instanceof ConfContent) {
                //设备导航配置
                ConfContent content = (ConfContent) obj;
                if (StringUtils.isNotEmpty(content.getContent_icon())) {
                    content.setContent_icon(webpath + content.getContent_icon());
                }
            } else if (obj instanceof Video) {
                //视频
                Video video = (Video) obj;
                if (StringUtils.isNotEmpty(video.getCover_url())) {
                    video.setCover_url(webpath + video.getCover_url());
                }
            } else if (obj instanceof VideoCat) {
                //视频分类
                VideoCat videocat = (VideoCat) obj;
                if (StringUtils.isNotEmpty(videocat.getCat_pic())) {
                    videocat.setCat_pic(webpath + videocat.getCat_pic());
                }
            } else if (obj instanceof Audio) {
                //音频
                Audio audio = (Audio) obj;
                if (StringUtils.isNotEmpty(audio.getCover_url())) {
                    audio.setCover_url(webpath + audio.getCover_url());
                }
            } else if (obj instanceof AudioCat) {
                //音频分类
                AudioCat audioCat = (AudioCat) obj;
                if (StringUtils.isNotEmpty(audioCat.getCat_pic())) {
                    audioCat.setCat_pic(webpath + audioCat.getCat_pic());
                }
            } else if (obj instanceof Journal) {
                //期刊
                Journal journal = (Journal) obj;
                if (StringUtils.isNotEmpty(journal.getJournal_cover())) {
                    journal.setJournal_cover(webpath + journal.getJournal_cover());
                }
                if (StringUtils.isNotEmpty(journal.getAndroid_qr_code())) {
                    journal.setAndroid_qr_code(webpath + journal.getAndroid_qr_code());
                }
                if (StringUtils.isNotEmpty(journal.getIos_qr_code())) {
                    journal.setIos_qr_code(webpath + journal.getIos_qr_code());
                }
            } else if (obj instanceof JournalCat) {
                //期刊分类
                JournalCat audioCat = (JournalCat) obj;
            } else if (obj instanceof Adv) {
                //广告
                Adv adv = (Adv) obj;
                if (StringUtils.isNotEmpty(adv.getAdv_img())) {
                    adv.setAdv_img(webpath + adv.getAdv_img());
                }
            } else if (obj instanceof AdvCat) {
                //广告分类
                AdvCat advCat = (AdvCat) obj;
            } else if (obj instanceof Newspaper) {
                //报纸
                Newspaper paper = (Newspaper) obj;
                if (StringUtils.isNotEmpty(paper.getPaper_img())) {
                    paper.setPaper_img(webpath + paper.getPaper_img());
                }
            } else if (obj instanceof NewspaperCat) {
                //报纸分类
                NewspaperCat papercat = (NewspaperCat) obj;

            } else if (obj instanceof ArticleCat) {
                //更新新闻分类的图片链接地址
                ArticleCat cat = (ArticleCat) obj;
                if (StringUtils.isNotEmpty(cat.getCat_pic())) {
                    cat.setCat_pic(webpath + cat.getCat_pic());
                }
            } else if (obj instanceof Article) {
                //更新新闻的图片链接地址
                Article article = (Article) obj;
                if (StringUtils.isNotEmpty(article.getCover_url())) {
                    article.setCover_url(webpath + article.getCover_url());
                }
                String content = (String) article.getArticle_content();
                if (StringUtils.isNotEmpty(content)) {
                    content = StringUtils.replace(content, "/uploads", webpath + "/uploads");
                    if (article.getArticle_type() == 1) {
                        List objects = JSONObject.parseArray(content, ArticlePictureProxyItem.class);
                        article.setArticle_content(objects);
                    } else {
                        article.setArticle_content(content);
                    }
                }
            } else if (obj instanceof BookOffLineBean) {
                //图书
                BookOffLineBean book = (BookOffLineBean) obj;
                if (StringUtils.isNotEmpty(book.getBook_cover())) {
                    book.setBook_cover(webpath + book.getBook_cover());
                }
                if (StringUtils.isNotEmpty(book.getBook_url())) {
                    book.setBook_url(webpath + book.getBook_url());
                }
                book.setScanurl(webpath + "/api/book/info?o=" + book.getOrg_id() + "&d=" + devcode + "&b=" + book.getBook_id());
            } else if (obj instanceof BookCat) {
                //图书分类的图片链接地址
                BookCat bookcat = (BookCat) obj;

            }
        }
    }


    /**
     * 检查是否有离线数据
     *
     * @param orgid
     * @param timev
     * @return
     */
    public boolean hasOffLine(String module, Long orgid, String timev, Long devid) {
        if (offLineRegServices.size() == 0) {
            regService();
        }
        if ("all".equals(module)) {
            Iterator iter = offLineRegServices.keySet().iterator();
            while (iter.hasNext()) {
                String moduleKey = (String) iter.next();
                boolean hasOffLineData = checkModuleHasOffLineDatas(moduleKey, orgid, timev, devid);
                if (hasOffLineData) {
                    return true;
                }
            }
        } else {
            String[] modules = module.split(",");
            for (int mi = 0; mi < modules.length; mi++) {
                String curModule = modules[mi];
                boolean hasOffLineData = checkModuleHasOffLineDatas(curModule, orgid, timev, devid);
                if (hasOffLineData) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取需要离线的数据数量
     *
     * @param orgid
     * @param timev
     * @return
     */
    public List<Map<String, Object>> getOffLineDataCount(String module, Long orgid, String timev, Long devid) {
        if (offLineRegServices.size() == 0) {
            regService();
        }
        Map<String, IOfflineService> offList = offLineRegServices.get(module);
        Iterator iter = offList.keySet().iterator();
        List<Map<String, Object>> list = new ArrayList<>();
        while (iter.hasNext()) {
            Map<String, Object> map = new HashMap<>();
            String serviceKey = (String) iter.next();
            IOfflineService service = offList.get(serviceKey);
            Integer hasOffLineData_num = service.hasOffLine(orgid, timev, devid);
            map.put(serviceKey, hasOffLineData_num);
            list.add(map);
        }
        return list;
    }

    /**
     * 检查模块是否有更新数据
     *
     * @param moduleKey
     * @param orgid
     * @param timev
     * @return
     */
    private boolean checkModuleHasOffLineDatas(String moduleKey, Long orgid, String timev, Long devid) {
        Map<String, IOfflineService> offList = offLineRegServices.get(moduleKey);
        Iterator iter = offList.keySet().iterator();
        while (iter.hasNext()) {
            String serviceKey = (String) iter.next();
            IOfflineService service = offList.get(serviceKey);
            Integer hasOffLineData_num = service.hasOffLine(orgid, timev, devid);
            if (hasOffLineData_num > 0) {
                return true;
            }
        }
        return false;
    }


    /**
     * 注册服务
     */
    private synchronized void regService() {
        //图书模块
        LinkedHashMap<String, IOfflineService> bookModuleMap = new LinkedHashMap<String, IOfflineService>();
        bookModuleMap.put("bookcat", (IOfflineService) SpringContextUtil.getBean("bookCatServiceImpl"));
        bookModuleMap.put("book", (IOfflineService) SpringContextUtil.getBean("bookOffLineServiceImpl"));
        offLineRegServices.put("book", bookModuleMap);

        //注册导航菜单-背景与LOGO
        LinkedHashMap<String, IOfflineService> uiModuleMap = new LinkedHashMap<String, IOfflineService>();
        uiModuleMap.put("menu", (IOfflineService) SpringContextUtil.getBean("confContentServiceImpl"));
        uiModuleMap.put("ui", (IOfflineService) SpringContextUtil.getBean("deviceSettingServiceImpl"));
        uiModuleMap.put("device", (IOfflineService) SpringContextUtil.getBean("deviceServiceImpl"));
        offLineRegServices.put("config", uiModuleMap);

        //注册新闻的离线服务
        LinkedHashMap<String, IOfflineService> newsModuleMap = new LinkedHashMap<String, IOfflineService>();
        newsModuleMap.put("newscat", (IOfflineService) SpringContextUtil.getBean("articleCatServiceImpl"));
        newsModuleMap.put("news", (IOfflineService) SpringContextUtil.getBean("articleServiceImpl"));
        offLineRegServices.put("news", newsModuleMap);

        //注册音频模块
        LinkedHashMap<String, IOfflineService> audioModuleMap = new LinkedHashMap<String, IOfflineService>();
        audioModuleMap.put("audiocat", (IOfflineService) SpringContextUtil.getBean("audioCatServiceImpl"));
        audioModuleMap.put("audio", (IOfflineService) SpringContextUtil.getBean("audioServiceImpl"));
        offLineRegServices.put("audio", audioModuleMap);

        //注册视频模块
        LinkedHashMap<String, IOfflineService> videoModuleMap = new LinkedHashMap<String, IOfflineService>();
        videoModuleMap.put("videocat", (IOfflineService) SpringContextUtil.getBean("videoCatServiceImpl"));
        videoModuleMap.put("video", (IOfflineService) SpringContextUtil.getBean("videoServiceImpl"));
        offLineRegServices.put("video", videoModuleMap);

        //注册报纸模块
        LinkedHashMap<String, IOfflineService> paperModuleMap = new LinkedHashMap<String, IOfflineService>();
        paperModuleMap.put("papercat", (IOfflineService) SpringContextUtil.getBean("newspaperCatServiceImpl"));
        paperModuleMap.put("paper", (IOfflineService) SpringContextUtil.getBean("newspaperServiceImpl"));
        offLineRegServices.put("paper", paperModuleMap);

        //注册期刊模块
        LinkedHashMap<String, IOfflineService> journalModuleMap = new LinkedHashMap<String, IOfflineService>();
        journalModuleMap.put("journalcat", (IOfflineService) SpringContextUtil.getBean("journalCatServiceImpl"));
        journalModuleMap.put("journal", (IOfflineService) SpringContextUtil.getBean("journalServiceImpl"));
        offLineRegServices.put("journal", journalModuleMap);

        //注册广告模块
        LinkedHashMap<String, IOfflineService> advModuleMap = new LinkedHashMap<String, IOfflineService>();
        advModuleMap.put("advcat", (IOfflineService) SpringContextUtil.getBean("advCatServiceImpl"));
        advModuleMap.put("adv", (IOfflineService) SpringContextUtil.getBean("advServiceImpl"));
        offLineRegServices.put("adv", advModuleMap);

        //注册期刊新模块
        LinkedHashMap<String, IOfflineService> periodicalModuleMap = new LinkedHashMap<String, IOfflineService>();
        periodicalModuleMap.put("periodicalcat", (IOfflineService) SpringContextUtil.getBean("periodicalCatServiceImpl"));
        periodicalModuleMap.put("periodical", (IOfflineService) SpringContextUtil.getBean("periodicalRepoServiceImpl"));
        offLineRegServices.put("periodical", periodicalModuleMap);

    }


}
