package com.cjsz.tech.book.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.BookRecommendListBean;
import com.cjsz.tech.book.beans.CJZWWBookBean;
import com.cjsz.tech.book.beans.RecommendBookListBean;
import com.cjsz.tech.book.domain.CJZWWBooks;
import com.cjsz.tech.book.domain.RecommendBooks;
import com.cjsz.tech.book.domain.RecommendCat;

import java.util.List;

/**
 * Created by Administrator on 2017/9/5 0005.
 */
public interface RecommendBooksService {

    //获取推荐类型列表
    List<RecommendCat> getList();

    CJZWWBookBean searchBook(String key, String page, String limit);

    //推荐列表添加图书
    void addBook(RecommendBooks bean);

    //推荐列表移除图书
    void removeBook(Long id);

    //新增图书推荐分类
    void saveCat(RecommendCat bean);

    //修改图书推荐分类
    void updateCat(RecommendCat bean);

    //删除推荐分类
    void deleteCat(String ids);

    //获取推荐分类下图书列表
    Object getBookList(RecommendBookListBean bean);

    //长江中文网网文数据同步
    void saveBookByCJZWW(Long time);

    //根据code查询分类
    RecommendCat selectByCode(Long recommend_code);

    //根据名称查找分类
    RecommendCat selectByName(String recommend_type_name);

    //根据code查找除自己以外的分类
    RecommendCat selectByCodeAndId(Long recommend_code, Long cat_id);

    //根据name查找除自己以外的分类
    RecommendCat selectByNameAndId(String recommend_type_name, Long recommend_cat_id);

    //查询该分类是否重复添加图书
    RecommendBooks selectByBookId(Long book_id, Long recommend_cat_id);

    //模糊搜索长江中文网网文图书（分页列表）
    Object getCJZWWBookList(PageConditionBean bean);

    //查询推荐分类下是否有图书
    List<RecommendBooks> selectByCatId(Long recommend_cat_id);

    //修改排序
    void updateOrder(Long id, Long order_weight);

    //app获取推荐模块图书列表
    Object getRecommendBooks(Long recommend_cat_id, PageConditionBean bean);

    //根据id查找网文
    CJZWWBooks selectBook(Long book_id);

    List<BookRecommendListBean> getRecommendBooksByCodes(String codes,Integer limit);
}
