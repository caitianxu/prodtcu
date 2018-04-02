package com.cjsz.tech.web.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.book.mapper.BookCatMapper;
import com.cjsz.tech.book.mapper.BookIndexMapper;
import com.cjsz.tech.book.mapper.BookOrgRelMapper;
import com.cjsz.tech.cms.mapper.ArticleMapper;
import com.cjsz.tech.count.service.SearchCountService;
import com.cjsz.tech.journal.domain.NewspaperCat;
import com.cjsz.tech.journal.domain.PeriodicalCat;
import com.cjsz.tech.journal.mapper.NewspaperCatMapper;
import com.cjsz.tech.journal.mapper.NewspaperMapper;
import com.cjsz.tech.journal.mapper.PeriodicalCatMapper;
import com.cjsz.tech.journal.mapper.PeriodicalRepoMapper;
import com.cjsz.tech.journal.service.NewspaperCatService;
import com.cjsz.tech.journal.service.PeriodicalCatService;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.domain.AdvCat;
import com.cjsz.tech.system.mapper.AdvCatMapper;
import com.cjsz.tech.system.mapper.AdvMapper;
import com.cjsz.tech.system.utils.CacheUtil;
import com.cjsz.tech.utils.HttpClientUtil;
import com.cjsz.tech.web.beans.WebConstants;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by LuoLi on 2017/4/16 0016.
 */
@Service
public class NjswIndexEhcacheService {

    @Autowired
    private AdvCatMapper advCatMapper;
    @Autowired
    private AdvMapper advMapper;
    @Autowired
    private CmsService cmsService;
    @Autowired
    private BookCatMapper bookCatMapper;
    @Autowired
    private BookOrgRelMapper bookOrgRelMapper;
    @Autowired
    private PeriodicalRepoMapper periodicalRepoMapper;
    @Autowired
    private NewspaperMapper newsPaperMapper;
    @Autowired
    private SearchCountService searchCountService;
    @Autowired
    private PeriodicalCatMapper periodicalCatMapper;
    @Autowired
    private NewspaperCatMapper newspaperCatMapper;
    @Autowired
    private BookIndexMapper bookIndexMapper;
    @Autowired
    private ArticleMapper articleMapper;


    public List<Adv> getSiteAdvList(Long org_id, Boolean overwrite) {
        List<Adv> result = (List<Adv>) CacheUtil.get(WebConstants.NJSW + "_" + "adv_" + org_id);
        if (result == null || overwrite) {
            AdvCat advCat = advCatMapper.selectByCatCode("00004");//农家书屋首页插图广告
            List<Adv> advList = advMapper.selectAdvsByOrgIdAndCatId(advCat.getAdv_cat_id(), org_id);
            result = advList;
            CopyOnWriteArrayList threadSafeList = new CopyOnWriteArrayList<>();
            threadSafeList.addAll(result);
            CacheUtil.set(WebConstants.NJSW + "_" + "adv_" + org_id, threadSafeList);
        }
        return result;
    }

    public List<Adv> getSiteAdvBannerList(Long org_id, Boolean overwrite) {
        List<Adv> result = (List<Adv>) CacheUtil.get(WebConstants.NJSW + "_" + "advBanner_" + org_id);
        if (result == null || overwrite) {
            AdvCat advCat = advCatMapper.selectByCatCode("00003");//农家书屋网站banner
            List<Adv> advList = advMapper.selectAdvsByOrgIdAndCatId(advCat.getAdv_cat_id(), org_id);
            result = advList;
            CopyOnWriteArrayList threadSafeList = new CopyOnWriteArrayList<>();
            threadSafeList.addAll(result);
            CacheUtil.set(WebConstants.NJSW + "_" + "advBanner_" + org_id, threadSafeList);
        }
        return result;
    }

    public List<Map<String, Object>> getSiteCmsList(Long org_id, Boolean overwrite) {
        List<Map<String, Object>> result = (List<Map<String, Object>>) CacheUtil.get(WebConstants.NJSW + "_" + "cms_" + org_id);
        if (result == null || overwrite) {
            List<Map<String, Object>> cmsList = cmsService.getCmsListByOrgIdAndCount(Long.valueOf(org_id), 7);
            result = cmsList;
            CopyOnWriteArrayList threadSafeList = new CopyOnWriteArrayList<>();
            threadSafeList.addAll(result);
            CacheUtil.set(WebConstants.NJSW + "_" + "cms_" + org_id, threadSafeList);
        }
        return result;
    }

    public List<Map<String, Object>> getSiteArtList(Long org_id, Boolean overwrite) {
        List<Map<String, Object>> result = (List<Map<String, Object>>) CacheUtil.get(WebConstants.NJSW + "_" + "cms_" + org_id);
        if (result == null || overwrite) {
            List<Map<String, Object>> cmsList = cmsService.getArtListByOrgIdAndCount(Long.valueOf(org_id), 7);
            result = cmsList;
            CopyOnWriteArrayList threadSafeList = new CopyOnWriteArrayList<>();
            threadSafeList.addAll(result);
            CacheUtil.set(WebConstants.NJSW + "_" + "cms_" + org_id, threadSafeList);
        }
        return result;
    }

    public List<Map<String, Object>> getRecommendCmsList(Long org_id, Boolean overwrite) {
        List<Map<String, Object>> result = (List<Map<String, Object>>) CacheUtil.get(WebConstants.NJSW + "_" + "recommendCms_" + org_id);
        if (result == null || overwrite) {
            List<Map<String, Object>> cmsList = articleMapper.getRecommendArtListByOrgIdAndCount(Long.valueOf(org_id), 3);
            result = cmsList;
            CopyOnWriteArrayList threadSafeList = new CopyOnWriteArrayList<>();
            threadSafeList.addAll(result);
            CacheUtil.set(WebConstants.NJSW + "_" + "recommendCms_" + org_id, threadSafeList);
        }
        return result;
    }

    //图书推荐
    public Map<String, Object> getSiteRecommendBookList(Long org_id, Boolean overwrite) {
        Map<String, Object> dataList = new HashMap<>();
        //查询前5个分类
        List<Map<String, Object>> bookCatList = bookCatMapper.selectFirstCatsByIdAndCount(org_id, 5);
        Map<String,Object> newCat = new HashMap<>();
        newCat.put("book_cat_id",0);
        newCat.put("book_cat_name","最新推荐");
        bookCatList.add(0,newCat);
        List<List<Map<String, Object>>> bookComList = new ArrayList<>();
        dataList.put("cats", bookCatList);
        for (Object cat : bookCatList) {
            if (bookCatList.indexOf(cat) == 0){
                continue;
            }
            List<Map<String, Object>> recommendBooks = bookOrgRelMapper.selectRecommendListByOrgIdAndCatId(Long.valueOf(org_id), ((HashMap) cat).get("book_cat_path").toString(), 5);
            bookComList.add(recommendBooks);
        }
        List<Map<String,Object>> newCatBookList = bookOrgRelMapper.selectNewCatBookList(Long.valueOf(org_id),5);
        bookComList.add(0,newCatBookList);
        dataList.put("books", bookComList);
        return dataList;



    }

    public Map<String, Object> getSiteHotBookList(Long org_id, Boolean overwrite) {
        Map<String, Object> result = (Map<String, Object>) CacheUtil.get(WebConstants.NJSW + "_" + "hotBook_" + org_id);
        if (result == null || overwrite) {
            List<Map<String,Object>> weekList = bookIndexMapper.getIndexListByWeek(org_id);
            List<Map<String,Object>> monthList = bookIndexMapper.getIndexListByMonth(org_id);
            List<Map<String,Object>> yearList = bookIndexMapper.getIndexListByYear(org_id);
            Map<String,Object> hotBookList = new HashMap<>();
            hotBookList.put("week",weekList);
            hotBookList.put("month",monthList);
            hotBookList.put("year",yearList);
            return hotBookList;

        }
        return null;
    }

    //期刊推荐
    public Map<String, Object> getSitePeriodicalList(Long org_id, Boolean overwrite) {
        Map<String, Object> dataList = new HashMap<>();
        //查询前5个分类
        List<Map<String, Object>> perCatList = periodicalCatMapper.selectFirstCatsByOrgIdAndCount(org_id);
        List<List<Map<String, Object>>> perComList = new ArrayList<>();
        Map<String,Object> newCat = new HashMap<>();
        newCat.put("periodical_cat_id",0L);
        newCat.put("periodical_cat_name","最新推荐");
        perCatList.add(0,newCat);
        dataList.put("cats", perCatList);
        for (Object cat : perCatList) {
            if (perCatList.indexOf(cat) == 0){
                continue;
            }
            List<Map<String, Object>> recommendPers = periodicalRepoMapper.selectRecommendListByCount(Long.valueOf(org_id), ((HashMap) cat).get("periodical_cat_path").toString(), 5);
            perComList.add(recommendPers);
        }
        List<Map<String,Object>> newCatPerList = periodicalRepoMapper.selectNewCatPerList(Long.valueOf(org_id),5);
        perComList.add(0,newCatPerList);
        dataList.put("pers", perComList);
        return dataList;
    }

    //报纸推荐
    public Map<String, Object> getSiteNewspaperList(Long org_id, Boolean overwrite) {
        Map<String, Object> dataList = new HashMap<>();
        //查询前5个分类
        List<Map<String, Object>> paperCatList =  newspaperCatMapper.selectFirstCatsByOrgIdAndCount(org_id);
        List<List<Map<String, Object>>> paperComList = new ArrayList<>();
        dataList.put("cats", paperCatList);
        for (Object cat : paperCatList) {
            List<Map<String, Object>> recommendPapers = newsPaperMapper.selectRecommendListByCount(Long.valueOf(org_id), ((HashMap) cat).get("newspaper_cat_path").toString(), 5);
            paperComList.add(recommendPapers);
        }
        dataList.put("papers", paperComList);
        return dataList;

    }

    public Object getSiteKeys(Long org_id, Boolean overwrite) {
        List<Object> result = (List<Object>) CacheUtil.get(WebConstants.NJSW + "_" + "keys_" + org_id);
        if (result == null || overwrite) {
            JSONObject jsonObject = new JSONObject();
            CopyOnWriteArrayList threadSafeList = new CopyOnWriteArrayList<>();
            try {
                String res = HttpClientUtil.httpGetRequest("http://www.cjzww.com/interface/MobInterface/AppContent.php?act=hotSearch");
                res = StringEscapeUtils.unescapeJava(res);
                jsonObject = JSONObject.parseObject(res);
                Object keys = jsonObject.get("keys");
                List<Object> list = ((JSONArray) keys);
                result = list;
            } catch (Exception e) {
                e.printStackTrace();
                result = new ArrayList<Object>();
            }
            threadSafeList.addAll(result);
            CacheUtil.set(WebConstants.NJSW + "_" + "keys_" + org_id, threadSafeList);
        }
        return result;
    }

    public List<Map<String, Object>> getSiteKeysList(Long org_id, Boolean overwrite) {
        List<Map<String, Object>> result = (List<Map<String, Object>>) CacheUtil.get(WebConstants.NJSW + "_" + "keys" + org_id);
        if (result == null || overwrite) {
            List<Map<String, Object>> keysList = searchCountService.selectByOrgId(Long.valueOf(org_id), 20);
            result = keysList;
            CopyOnWriteArrayList threadSafeList = new CopyOnWriteArrayList<>();
            threadSafeList.addAll(result);
            CacheUtil.set(WebConstants.NJSW + "_" + "keys" + org_id, threadSafeList);
        }
        return result;
    }

    public List<Adv> advDataList(Long org_id, Boolean overwrite) {
        List<Adv> result = (List<Adv>) CacheUtil.get(WebConstants.NJSW + "_" + "advData_" + org_id);
        if (result == null || overwrite) {
            AdvCat advCat = advCatMapper.selectByCatCode("00005");//农家书屋网站banner
            List<Adv> advList = advMapper.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), org_id);
            result = advList;
            CopyOnWriteArrayList threadSafeList = new CopyOnWriteArrayList<>();
            threadSafeList.addAll(result);
            CacheUtil.set(WebConstants.NJSW + "_" + "advData_" + org_id, threadSafeList);
        }
        return result;
    }

    public List<Adv> underPeriodicalAdv(Long org_id, Boolean overwrite) {
        List<Adv> result = (List<Adv>) CacheUtil.get(WebConstants.NJSW + "_" + "underPeriodicalAdv" + org_id);
        if (result == null || overwrite) {
            AdvCat advCat = advCatMapper.selectByCatCode("00009");//农家书屋网站banner
            List<Adv> advList = advMapper.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), org_id);
            result = advList;
            CopyOnWriteArrayList threadSafeList = new CopyOnWriteArrayList<>();
            threadSafeList.addAll(result);
            CacheUtil.set(WebConstants.NJSW + "_" + "underPeriodicalAdv" + org_id, threadSafeList);
        }
        return result;
    }

    public List<Adv> underPaperAdv(Long org_id, Boolean overwrite) {
        List<Adv> result = (List<Adv>) CacheUtil.get(WebConstants.NJSW + "_" + "underPaperAdv" + org_id);
        if (result == null || overwrite) {
            AdvCat advCat = advCatMapper.selectByCatCode("00010");//农家书屋网站banner
            List<Adv> advList = advMapper.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), org_id);
            result = advList;
            CopyOnWriteArrayList threadSafeList = new CopyOnWriteArrayList<>();
            threadSafeList.addAll(result);
            CacheUtil.set(WebConstants.NJSW + "_" + "underPaperAdv" + org_id, threadSafeList);
        }
        return result;
    }

    public List<Adv> getAdvList(Long org_id) {
        return getSiteAdvList(org_id, false);
    }

    public List<Adv> getAdvBannerList(Long org_id) {
        return getSiteAdvBannerList(org_id, false);
    }

    public List<Map<String, Object>> getCmsList(Long org_id) {
        return getSiteArtList(org_id, false);
    }

    public Map<String, Object> getRecommendBookList(Long org_id) {
        return getSiteRecommendBookList(org_id, false);
    }

    public Map<String, Object> getHotBookList(Long org_id) {
        return getSiteHotBookList(org_id, false);
    }

    public Map<String, Object> getPeriodicalList(Long org_id) {
        return getSitePeriodicalList(org_id, false);
    }

    public Map<String, Object> getNewspaperList(Long org_id) {
        return getSiteNewspaperList(org_id, false);
    }

    public Object getKeys(Long org_id) {
        return getSiteKeysList(org_id, false);
    }

    public List<Adv> advDataList(Long org_id){
        return advDataList(org_id,false);
    }

    public Adv underPeriodicalAdv(Long org_id) {
        List<Adv> list =  underPeriodicalAdv(org_id,false);
        if (list.size()>0) {
            return list.get(0);
        }else {
            return null;
        }
    }

    public Adv underPaperAdv(Long org_id) {
        List<Adv> list =  underPaperAdv(org_id,false);
        if (list.size()>0) {
            return list.get(0);
        }else{
            return null;
        }
    }

    public List<Map<String, Object>> getRecommendCmsList(Long org_id) {
        return getRecommendCmsList(org_id, false);
    }
}
