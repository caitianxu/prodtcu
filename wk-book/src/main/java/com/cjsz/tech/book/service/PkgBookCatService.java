package com.cjsz.tech.book.service;


import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.book.domain.PkgBookCat;

import java.util.List;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface PkgBookCatService {

    /**
     * 获取数据包图书分类
     * @param org_id
     * @param pkg_id
     * @return
     */
    public List<PkgBookCat> getPkgCats(Long org_id, Long pkg_id);

    /**
     * 获取图书分类——树结构
     * @param cats
     * @return
     */
    public List<PkgBookCat> selectTree(List<PkgBookCat> cats);

    /**
     * 保存分类
     * @param cat
     * @param org_id
     */
    public PkgBookCat saveCat(PkgBookCat cat, Long org_id);

    /**
     * 修改分类
     * @param cat
     */
    public PkgBookCat updateCat(PkgBookCat cat);

    /**
     * 查找包的分类集合
     * @param pkg_id
     * @param orgid
     * @param book_cat_ids
     * @return
     */
    public List<Long> getPkgAllCat(Long pkg_id, Long orgid, Long[] book_cat_ids);

    /**
     * 删除数据包的图书分类和详情
     * @param pkg_id
     * @param orgid
     * @param catidList
     */
    public void deletePkgAll(Long pkg_id, Long orgid, List<Long> catidList);

    /**
     * 获取当前分类本身和下级分类
     * @param org_id
     * @param pkg_id
     * @param book_cat_path
     * @return
     */
    public List<BookCat> getOwnerCats(Long org_id, Long pkg_id, String book_cat_path);
}
