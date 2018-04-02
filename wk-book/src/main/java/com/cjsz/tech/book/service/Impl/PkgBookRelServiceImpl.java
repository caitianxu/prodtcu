package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.book.beans.BookBean;
import com.cjsz.tech.book.beans.FindBookBean;
import com.cjsz.tech.book.beans.PkgBookBean;
import com.cjsz.tech.book.beans.SaveBookBean;
import com.cjsz.tech.book.domain.*;
import com.cjsz.tech.book.mapper.*;
import com.cjsz.tech.book.service.BookOrgRelService;
import com.cjsz.tech.book.service.DataPackageService;
import com.cjsz.tech.book.service.PkgBookRelService;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.mapper.DeviceMapper;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
@Service
public class PkgBookRelServiceImpl implements PkgBookRelService {

    @Autowired
    private PkgBookRelMapper pkgBookRelMapper;

    @Autowired
    private PkgBookCatMapper pkgBookCatMapper;

    @Autowired
    private BookTagRelMapper bookTagRelMapper;

    @Autowired
    DataPackageService dataPackageService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Object pageQuery(Sort sort, FindBookBean bean) {
        String catPath = "";
        PkgBookCat bookCat = null;
        //查找包的分类层次路径
        bookCat = pkgBookCatMapper.selectPkgBookCat(bean.getPkg_id(), bean.getOrg_id(), bean.getBook_cat_id());
        if(bookCat != null){
            catPath = bookCat.getBook_cat_path();
        }
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //数据包的图书列表
        List<BookBean> result = pkgBookRelMapper.getPkgBookList(bean.getPkg_id(), bean.getOrg_id(), catPath, bean.getSearchText(),
                bean.getIs_hot(), bean.getIs_recommend(), bean.getOffline_status());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    @Transactional
    //数据包新增图书
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "数据包图书机构关系")
    public void savePkgBookRel(Long org_id, SaveBookBean bookBean){
        List<Long> tagIds = bookBean.getTag_ids();
        List<Long> bookIds = bookTagRelMapper.getBookIdsByTagIds(StringUtils.join(tagIds, ","));
        List<PkgBookRel> pkgBookRels = new ArrayList<PkgBookRel>();
        //查询未添加前数据包的该分类下的图书Id
        List<Long> org_bookids = pkgBookRelMapper.selectPkgRelsByCatId(bookBean.getPkg_id(), org_id, bookBean.getBook_cat_id());
        List<Long> del_bookids = new ArrayList<Long>();//需要删除的图书Id集合
        for(Long book_id : bookIds){
            //图书已存在就不添加
            if(org_bookids.contains(book_id)){
                del_bookids.add(book_id);
            }
            PkgBookRel pkgBookRel = new PkgBookRel();
            pkgBookRel.setBook_id(book_id);
            pkgBookRel.setOrg_id(org_id);
            pkgBookRel.setBook_cat_id(bookBean.getBook_cat_id());
            pkgBookRel.setPkg_id(bookBean.getPkg_id());
            pkgBookRel.setOrder_weight(System.currentTimeMillis());
            pkgBookRel.setIs_hot(2);//是否热门(1:是;2:否)
            pkgBookRel.setIs_recommend(2);//是否推荐(1:是;2:否)
            pkgBookRel.setOffline_status(0);//0:不离线;1:发送离线
            pkgBookRel.setEnabled(2);//是否启用(1:是;2:否)
            pkgBookRel.setCreate_time(new Date());
            pkgBookRel.setUpdate_time(new Date());
            pkgBookRels.add(pkgBookRel);
        }
        if(del_bookids.size() > 0){
            pkgBookRelMapper.deletePkgBookByBookId(bookBean.getPkg_id(), StringUtils.join(del_bookids, ","));
        }
        if(pkgBookRels.size()>0){
            pkgBookRelMapper.insertList(pkgBookRels);
        }
        //更新数据包图书数量
        DataPackage dataPackage = dataPackageService.selectById(bookBean.getPkg_id());
        Long book_count = pkgBookRelMapper.selectBookCountByPkgId(bookBean.getPkg_id());
        Long book_real_count = pkgBookRelMapper.selectBookRealCountByPkgId(bookBean.getPkg_id());
        dataPackage.setBook_count(book_count);
        dataPackage.setBook_real_count(book_real_count);
        dataPackageService.updatePkg(dataPackage);
    }

    @Override
    public PkgBookRel selectById(Long org_id,SaveBookBean bean){
        return pkgBookRelMapper.selectById(org_id,bean.getPkg_id(),bean.getBook_cat_id(),bean.getBook_id());
    }

    @Override
    @Transactional
    //数据包新增图书
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "数据包图书机构关系")
    public void pkgInsertBook(Long org_id, SaveBookBean bookBean){
        PkgBookRel pkgBookRel = new PkgBookRel();
        pkgBookRel.setBook_id(bookBean.getBook_id());
        pkgBookRel.setOrg_id(org_id);
        pkgBookRel.setBook_cat_id(bookBean.getBook_cat_id());
        pkgBookRel.setPkg_id(bookBean.getPkg_id());
        pkgBookRel.setOrder_weight(System.currentTimeMillis());
        pkgBookRel.setIs_hot(2);//是否热门(1:是;2:否)
        pkgBookRel.setIs_recommend(2);//是否推荐(1:是;2:否)
        pkgBookRel.setOffline_status(0);//0:不离线;1:发送离线
        pkgBookRel.setEnabled(2);//是否启用(1:是;2:否)
        pkgBookRel.setCreate_time(new Date());
        pkgBookRel.setUpdate_time(new Date());
        pkgBookRelMapper.insert(pkgBookRel);

        //更新数据包图书数量
        DataPackage dataPackage = dataPackageService.selectById(bookBean.getPkg_id());
        Long book_count = pkgBookRelMapper.selectBookCountByPkgId(bookBean.getPkg_id());
        Long book_real_count = pkgBookRelMapper.selectBookRealCountByPkgId(bookBean.getPkg_id());
        dataPackage.setBook_count(book_count);
        dataPackage.setBook_real_count(book_real_count);
        dataPackageService.updatePkg(dataPackage);
    }

    @Override
    //数据包移除图书
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "数据包图书机构关系")
    public void removePkgBookRel(BookBean bookBean){
        List<PkgBookRel> bookRels = pkgBookRelMapper.selectByPkgIdAndBookId(bookBean.getPkg_id(), bookBean.getBook_id());
        //同一本书在多个分类下，减少book_count
        dataPackageService.updatePkgBookCount(bookBean.getPkg_id(), -1L);
        //同一本书仅在一个分类下，减少book_count和book_real_count
        if(bookRels.size() == 1){
            dataPackageService.updatePkgBookRealCount(bookBean.getPkg_id(), -1L);
        }
        pkgBookRelMapper.removePkgBookRel(bookBean.getBook_id(), bookBean.getOrg_id(), bookBean.getBook_cat_id(), bookBean.getPkg_id());
    }

    @Override
    //数据包批量移除图书
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "数据包图书机构关系")
    public void removePkgBookRels(PkgBookBean pkgBookBean){
        List<PkgBookBean.PkgBook> pkgBooks = pkgBookBean.getBooks();
        List<String> id_arr = new ArrayList<String>();
        for(PkgBookBean.PkgBook pkgBook : pkgBooks){
            if(pkgBook.getBook_cat_id() != null && pkgBook.getBook_id() != null){
                id_arr.add("(" + pkgBook.getBook_cat_id() + "," + pkgBook.getBook_id() + ")");
            }
        }
        String ids = StringUtils.join(id_arr, ",");
        pkgBookRelMapper.removePkgBookRels(pkgBookBean.getPkg_id(), ids);
        //更新数据包图书数量
        DataPackage dataPackage = dataPackageService.selectById(pkgBookBean.getPkg_id());
        Long book_count = pkgBookRelMapper.selectBookCountByPkgId(pkgBookBean.getPkg_id());
        Long book_real_count = pkgBookRelMapper.selectBookRealCountByPkgId(pkgBookBean.getPkg_id());
        dataPackage.setBook_count(book_count);
        dataPackage.setBook_real_count(book_real_count);
        dataPackageService.updatePkg(dataPackage);
    }

    @Override
    //根据联合主键查询关系数据
    public PkgBookRel selectPkgBookRelByUnionIds(Long book_id, Long org_id, Long book_cat_id, Long pkg_id){
        return pkgBookRelMapper.selectPkgBookRelByUnionIds(book_id, org_id, book_cat_id, pkg_id);
    }

    @Override
    //数据包图书 是否热门
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "数据包图书机构关系")
    public void updateRelHot(BookBean bookBean){
        pkgBookRelMapper.updateRelHot(bookBean.getBook_id(), bookBean.getOrg_id(), bookBean.getBook_cat_id(),
                bookBean.getPkg_id(), bookBean.getIs_hot());
    }

    @Override
    //数据包图书 是否推荐
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "数据包图书机构关系")
    public void updateRelRecommend(BookBean bookBean){
        pkgBookRelMapper.updateRelRecommend(bookBean.getBook_id(), bookBean.getOrg_id(), bookBean.getBook_cat_id(),
                bookBean.getPkg_id(), bookBean.getIs_recommend());
    }

    @Override
    //数据包图书 是否离线
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "数据包图书机构关系")
    public void updateOffLine(BookBean bookBean) {
        //更新图书机构关系的离线状态
        pkgBookRelMapper.updateOffLine(bookBean.getBook_id(), bookBean.getOrg_id(), bookBean.getBook_cat_id(),
                bookBean.getPkg_id(), bookBean.getOffline_status());
    }

    @Override
    //数据包图书  排序置顶
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "数据包图书机构关系")
    public void updateOrderTop(BookBean bookBean){
        pkgBookRelMapper.updateOrderTop(bookBean.getBook_id(), bookBean.getOrg_id(), bookBean.getBook_cat_id(),
                bookBean.getPkg_id(), bookBean.getOrder_weight());
    }

    @Override
    //查找数据包的包与图书关系
    public List<PkgBookRel> findByPkgId(Long pkg_id) {
        return pkgBookRelMapper.findByPkgId(pkg_id);
    }

    @Override
    //数据包图书全部基本信息
    public List<Map<String, Object>> getAllBooksBaseInfo(Long pkg_id) {
        return pkgBookRelMapper.getAllBooksBaseInfo(pkg_id);
    }
}
