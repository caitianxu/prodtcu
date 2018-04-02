package com.cjsz.tech.book.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.*;
import com.cjsz.tech.book.domain.CJZWWBooks;
import com.cjsz.tech.book.domain.RecommendBooks;
import com.cjsz.tech.book.domain.RecommendCat;
import com.cjsz.tech.book.mapper.CJZWWBooksMapper;
import com.cjsz.tech.book.mapper.RecommendBooksMapper;
import com.cjsz.tech.book.mapper.RecommendCatMapper;
import com.cjsz.tech.book.service.RecommendBooksService;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.HttpClientUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Administrator on 2017/9/5 0005.
 */
@Service
public class RecommendBooksServiceImpl implements RecommendBooksService {

    @Autowired
    private RecommendBooksMapper recommendBooksMapper;

    @Autowired
    private RecommendCatMapper recommendCatMapper;

    @Autowired
    private CJZWWBooksMapper cjzwwBooksMapper;


    @Override
    public List<RecommendCat> getList() {
        return recommendCatMapper.getList();
    }

    //长江中文网搜索图书
    @Override
    public CJZWWBookBean searchBook(String key, String page, String limit){
        try{
            Map<String,Object> map = new HashMap<>();
            map.put("key", key);
            map.put("page", page);
            map.put("limit", limit);
            String result = HttpClientUtil.httpPostRequest("http://www.cjzww.com/interface/MobInterface/AppContent.php?act=Search", map);
            CJZWWBookBean book = JSONObject.parseObject(result,CJZWWBookBean.class);
            return book;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "推荐图书")
    public void addBook(RecommendBooks bean) {
        bean.setCreate_time(new Date());
        if (bean.getOrder_weight() == null){
            bean.setOrder_weight(System.currentTimeMillis());
        }
        recommendBooksMapper.insert(bean);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "推荐图书")
    public void removeBook(Long id) {
        recommendBooksMapper.deleteByPrimaryKey(id);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "推荐图书")
    public void saveCat(RecommendCat bean) {
        bean.setCreate_time(new Date());
        bean.setUpdate_time(new Date());
        bean.setIs_delete(2);
        recommendCatMapper.insert(bean);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "推荐图书")
    public void updateCat(RecommendCat bean) {
        bean.setUpdate_time(new Date());
        bean.setIs_delete(2);
        recommendCatMapper.updateByPrimaryKey(bean);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "推荐图书")
    public void deleteCat(String ids) {
        recommendCatMapper.deleteCat(ids);
    }

    @Override
    public Object getBookList(RecommendBookListBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        List<RecommendBooks> list = recommendBooksMapper.getBookList(bean.getRecommend_cat_id(),bean.getSearchText());
        PageList pageList = new PageList(list,null);
        return pageList;
    }

    //长江中文网网文数据同步
    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "网文数据同步")
    public void saveBookByCJZWW(Long time) {
        Map<String ,Object> map = new HashMap<>();
        map.put("timestamp",time.toString());
        List<CJZWWBooks> list = cjzwwBooksMapper.selectAllList();
        String result = "" ;
        try {
            if (list == null || list.size() == 0) {
                result = HttpClientUtil.httpPostRequest("http://www.cjzww.com/interface/MobInterface/AppContent.php?act=BookList");
            } else {
                result = HttpClientUtil.httpPostRequest("http://www.cjzww.com/interface/MobInterface/AppContent.php?act=BookList", map);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        CJZWWBookListBean book = JSONObject.parseObject(result,CJZWWBookListBean.class);
            List<CJZWWBooks> bookList = book.getList();
        if (bookList != null && bookList.size() > 0) {
            Integer count =0;
            for (CJZWWBooks cjzwwBooks:bookList){
                count +=1;
                System.out.println("***"+new Date()+"***==============循环第"+count+"本书==============总数："+bookList.size()+"本书=======");
                //查询图书是否存在网文仓库,不在就新增，在就覆盖更新
                CJZWWBooks books = cjzwwBooksMapper.selectById(cjzwwBooks.getBook_id());
                if (books == null){
                    cjzwwBooksMapper.insert(cjzwwBooks);
                }else {
                    cjzwwBooksMapper.updateByPrimaryKey(cjzwwBooks);
                }

                //查询推荐图书列表是否存在该书，如果在，更新图书信息
                RecommendBooks recommendBooks = recommendBooksMapper.selectById(cjzwwBooks.getBook_id());
                if (recommendBooks != null){
                    recommendBooks.setBook_author(cjzwwBooks.getBook_author());
                    recommendBooks.setBook_cover(cjzwwBooks.getBook_cover());
                    recommendBooks.setBook_remark(cjzwwBooks.getBook_remark());
                    recommendBooks.setBook_name(cjzwwBooks.getBook_name());
                    recommendBooksMapper.updateByPrimaryKey(recommendBooks);
                }
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public RecommendCat selectByCode(Long recommend_code) {
        return recommendCatMapper.selectByCode(recommend_code);
    }

    @Override
    public RecommendCat selectByName(String recommend_type_name) {
        return recommendCatMapper.selectByName(recommend_type_name);
    }

    @Override
    public RecommendCat selectByCodeAndId(Long recommend_code, Long cat_id) {
        return recommendCatMapper.selectByCodeAndId(recommend_code,cat_id);
    }

    @Override
    public RecommendCat selectByNameAndId(String recommend_type_name, Long recommend_cat_id) {
        return recommendCatMapper.selectByNameAndId(recommend_type_name,recommend_cat_id);
    }

    @Override
    public RecommendBooks selectByBookId(Long book_id, Long recommend_cat_id) {
        return recommendBooksMapper.selectByBookId(book_id,recommend_cat_id);
    }

    @Override
    public Object getCJZWWBookList(PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        List<CJZWWBooks> result = cjzwwBooksMapper.getAllBooks(bean.getSearchText());
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public List<RecommendBooks> selectByCatId(Long recommend_cat_id) {
        return recommendBooksMapper.selectByCatId(recommend_cat_id);
    }

    @Override
    public void updateOrder(Long id, Long order_weight) {
        recommendBooksMapper.updateOrder(id,order_weight);
    }

    @Override
    public Object getRecommendBooks(Long recommend_cat_id, PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        List<RecommendBooksBean> result = recommendBooksMapper.getRecommendBooks(recommend_cat_id);
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public CJZWWBooks selectBook(Long book_id) {
        return cjzwwBooksMapper.selectById(book_id);
    }

    @Override
    public List<BookRecommendListBean> getRecommendBooksByCodes(String codes,Integer limit) {
        codes = codes.replaceAll("，", ",");
        String[] codeList = codes.split(",");
        List<BookRecommendListBean> list = new ArrayList<>();
        for(String code : codeList){
            List<BookRecommend> data = recommendBooksMapper.getRecommendBooksByCode(code.trim(),12);
            BookRecommendListBean bean = new BookRecommendListBean();
            bean.setCode(code);
            bean.setData(data);
            list.add(bean);
        }
        return list;
    }
}
