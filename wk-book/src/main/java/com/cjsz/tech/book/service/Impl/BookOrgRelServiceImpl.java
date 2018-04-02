package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.*;
import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.book.domain.BookDeviceRel;
import com.cjsz.tech.book.domain.BookOrgRel;
import com.cjsz.tech.book.mapper.BookCatMapper;
import com.cjsz.tech.book.mapper.BookDeviceRelMapper;
import com.cjsz.tech.book.mapper.BookOrgRelMapper;
import com.cjsz.tech.book.mapper.BookTagRelMapper;
import com.cjsz.tech.book.service.BookDeviceRelService;
import com.cjsz.tech.book.service.BookOrgRelService;
import com.cjsz.tech.core.SpringContextUtil;
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
public class BookOrgRelServiceImpl implements BookOrgRelService {

    @Autowired
    private BookOrgRelMapper bookOrgRelMapper;

    @Autowired
    private BookCatMapper bookCatMapper;

    @Autowired
    private BookTagRelMapper bookTagRelMapper;

    @Autowired
    private BookDeviceRelMapper bookDeviceRelMapper;

    @Autowired
    private DeviceMapper deviceMapper;


    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Object pageQuery(Sort sort, FindBookBean bean) {
        String catPath = "";
        //查找机构的分类
        BookCat bookCat = bookCatMapper.selectOrgBookCat(bean.getOrg_id(), bean.getBook_cat_id());
        if (bookCat != null) {
            catPath = bookCat.getBook_cat_path();
        }
        //离线列表排序
        if (bean.getPkg_id() == null && bean.getDevice_id() != null) {
            sort = new Sort(Sort.Direction.DESC, "status").and(new Sort(Sort.Direction.DESC, "order_weight")).and(new Sort(Sort.Direction.DESC, "create_time"));
        }
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        List<BookBean> result = null;
        if (bean.getDevice_id() == null) {
            //机构的图书列表
            result = bookOrgRelMapper.getOrgBookList(bean.getOrg_id(), catPath, bean.getSearchText(), bean.getIs_hot(), bean.getIs_recommend(), bean.getEnabled());
            for(BookBean bookBean:result){
                Long click_count = bookOrgRelMapper.getClickCount(bookBean.getBook_id(),bean.getOrg_id());
                Long collect_count = bookOrgRelMapper.getCollectCount(bookBean.getBook_id(),bean.getOrg_id());
                Long share_count = bookOrgRelMapper.getShareCount(bookBean.getBook_id(),bean.getOrg_id());
                if (click_count != null){
                    bookBean.setClick_count(click_count);
                }else{
                    bookBean.setClick_count(0L);
                }

                if (collect_count != null){
                    bookBean.setClick_count(collect_count);
                }else{
                    bookBean.setCollect_count(0L);
                }

                if (share_count != null){
                    bookBean.setClick_count(share_count);
                }else{
                    bookBean.setShare_count(0L);
                }
            }
        } else {
            //机构的图书离线列表
            result = bookOrgRelMapper.getOrgOffLineBookList(bean.getOrg_id(), catPath, bean.getStatus(), bean.getDevice_id(), bean.getSearchText());
        }
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public Object sitePageQuery(Sort sort, FindBookBean bean) {
        String catPath = "";
        //查找机构的分类
        BookCat bookCat = bookCatMapper.selectOrgBookCat(bean.getOrg_id(), bean.getBook_cat_id());
        if (bookCat != null) {
            catPath = bookCat.getBook_cat_path();
        }
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        List<BookBean> result = bookOrgRelMapper.sitePageQuery(bean.getOrg_id(), catPath, bean.getSearchText(), bean.getTag_name(),bean.getOrder_type());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    //数据包图书、机构图书   排序置顶
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书机构关系")
    public void updateOrderTop(BookBean bookBean) {
        bookOrgRelMapper.updateOrderTop(bookBean.getBook_id(), bookBean.getOrg_id(), bookBean.getBook_cat_id(),
                bookBean.getPkg_id(), bookBean.getOrder_weight());
    }

    @Override
    //根据联合主键查询关系数据
    public BookOrgRel selectBookOrgRelByUnionIds(Long book_id, Long org_id, Long book_cat_id, Long pkg_id) {
        return bookOrgRelMapper.selectBookOrgRelByUnionIds(book_id, org_id, book_cat_id, pkg_id);
    }

    @Override
    //机构图书 是否热门
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书机构关系")
    public void updateRelHot(BookBean bookBean) {
        bookOrgRelMapper.updateRelHot(bookBean.getBook_id(), bookBean.getOrg_id(), bookBean.getBook_cat_id(),
                bookBean.getPkg_id(), bookBean.getIs_hot());
    }

    @Override
    //机构图书 是否推荐
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书机构关系")
    public void updateRelRecommend(BookBean bookBean) {
        bookOrgRelMapper.updateRelRecommend(bookBean.getBook_id(), bookBean.getOrg_id(), bookBean.getBook_cat_id(),
                bookBean.getPkg_id(), bookBean.getIs_recommend());
    }

    @Override
    @Transactional
    //机构图书 是否离线
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书机构关系")
    public void updateOffLine(BookBean bookBean) {
        //更新图书机构关系的离线状态
        bookOrgRelMapper.updateOffLine(bookBean.getBook_id(), bookBean.getOrg_id(), bookBean.getBook_cat_id(),
                bookBean.getPkg_id(), bookBean.getOffline_status());
        //更新机构设备图书的离线状态
        List<Device> devices = deviceMapper.getDevicesByOrgId(bookBean.getOrg_id());
        List<Long> device_ids = new ArrayList<Long>();
        for (Device device : devices) {
            device_ids.add(device.getDevice_id());
        }
        if (device_ids.size() > 0) {
            if (bookBean.getOffline_status() == 1) {
                //更新为离线中
                bookDeviceRelMapper.updateBookDeviceRelStatusByDeviceIds(StringUtils.join(device_ids, ","), 1);
            } else {
                //更新为不离线
                bookDeviceRelMapper.updateBookDeviceRelStatusByDeviceIds(StringUtils.join(device_ids, ","), 4);
            }
        }
    }

    @Override
    //修改图书 启用、停用
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书机构关系")
    public void updateBookRelStatus(Long orgid, List<BookBean> beanList) {
        int count = beanList.size();
        String[] updatesqls = new String[count];
        for (int i = 0; i < count; i++) {
            BookBean bookBean = beanList.get(i);
            Long pkgid = bookBean.getPkg_id();
            Long bkid = bookBean.getBook_id();
            Long catid = bookBean.getBook_cat_id();
            Integer enabled = bookBean.getEnabled();
            updatesqls[i] = "update book_org_rel set enabled=" + enabled + ",update_time = now() where org_id=" + orgid + " and pkg_id=" + pkgid + " and book_cat_id=" + catid + " and book_id=" + bkid;
        }
        jdbcTemplate.batchUpdate(updatesqls);
    }

    @Override
    //删除一批选中图书关系
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "图书机构关系")
    public void deleteBooksRel(Long orgid, List<BookBean> beanList) {
        int count = beanList.size();
        String[] delsqls = new String[count];
        for (int i = 0; i < count; i++) {
            BookBean bookBean = beanList.get(i);
            Long pkgid = bookBean.getPkg_id();
            Long bkid = bookBean.getBook_id();
            Long catid = bookBean.getBook_cat_id();
            delsqls[i] = "delete from book_org_rel where org_id=" + orgid + " and pkg_id=" + pkgid + " and book_cat_id=" + catid + " and book_id=" + bkid;
        }
        jdbcTemplate.batchUpdate(delsqls);
    }

    //查询机构图书详情
    @Override
    public List<BookOrgRel> selectOrgRels(Long orgid, List<Long> catidList) {
        String catids = StringUtils.join(catidList, ",");
        return bookOrgRelMapper.selectOrgRels(orgid, catids);
    }

    //更新组织下某一设备的图书离线状态
    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书机构关系")
    public void updateBookOffLine(Long orgid, Long device_id, List<Long> bookIds, Boolean bool) {
        //bool:true(发送离线到设备);false(删除设备离线)
        List<Long> update_book_ids = new ArrayList<Long>();
        if (bool) {
            BookDeviceRelService bookDeviceRelService = (BookDeviceRelService) SpringContextUtil.getBean("bookDeviceRelServiceImpl");
            BookOrgRelService bookOrgRelService = (BookOrgRelService) SpringContextUtil.getBean("bookOrgRelServiceImpl");

            List<BookDeviceRel> waitSaveRelList = new ArrayList<BookDeviceRel>();
            //更新未离线、取消离线的为离线中：1
            for (Long bkid : bookIds) {
                List<BookDeviceRel> bdrels = bookDeviceRelMapper.selectInOffLineBooksByBookId(orgid, device_id, bkid);
                if (bdrels != null && bdrels.size() > 0) {
                    BookDeviceRel bdrel = bdrels.get(0);
                    if (bdrel.getStatus() == 0 || bdrel.getStatus() == 3) {
                        update_book_ids.add(bdrel.getBook_id());
                    }
                } else {
                    List<BookOrgRel> bookOrgRels = bookOrgRelService.selectOrgRelsByBookId(orgid, bkid);
                    for (BookOrgRel bookOrgRel : bookOrgRels) {
                        BookDeviceRel bookDeviceRel = new BookDeviceRel();
                        bookDeviceRel.setOrg_id(orgid);
                        bookDeviceRel.setBook_id(bkid);
                        bookDeviceRel.setBook_cat_id(bookOrgRel.getBook_cat_id());
                        bookDeviceRel.setDevice_id(device_id);
                        bookDeviceRel.setStatus(1);
                        bookDeviceRel.setCreate_time(new Date());
                        bookDeviceRel.setUpdate_time(new Date());
                        waitSaveRelList.add(bookDeviceRel);
                    }
                }
            }
            if (update_book_ids.size() > 0) {
                bookDeviceRelMapper.updateBookDeviceRelStatus(orgid, device_id, StringUtils.join(update_book_ids, ","), 1);
            }
            if (waitSaveRelList.size() > 0) {
                bookDeviceRelService.saveBookDeviceRels(waitSaveRelList);
            }
        } else {
            //查询该机构该设备的图书
            List<BookDeviceRel> bookList = bookDeviceRelMapper.selectInOffLineBooks(orgid, device_id);
            //更新离线中、已离线状态为取消离线3
            for (BookDeviceRel bookDeviceRel : bookList) {
                if (bookIds.contains(bookDeviceRel.getBook_id())) {
                    if (bookDeviceRel.getStatus() == 1 || bookDeviceRel.getStatus() == 2) {
                        update_book_ids.add(bookDeviceRel.getBook_id());
                    }
                }
            }
            if (update_book_ids.size() > 0) {
                bookDeviceRelMapper.updateBookDeviceRelStatus(orgid, device_id, StringUtils.join(update_book_ids, ","), 3);
            }
        }
    }


//    //更新组织下某一设备的图书离线状态
//    @Override
//    @Transactional
//    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书机构关系")
//    public void updateBookOffLine(Long orgid, Long device_id, List<Long> bookIds, Boolean bool){
//        //bool:true(发送离线到设备);false(删除设备离线)
//        //查询该机构该设备的图书
//        List<BookDeviceRel> bookList = bookDeviceRelMapper.selectInOffLineBooks(orgid, device_id);
//        List<Long> update_book_ids = new ArrayList<Long>();
//        if(bool){
//            //更新未离线、取消离线的为离线中：1
//            for(BookDeviceRel bookDeviceRel : bookList){
//                if(bookIds.contains(bookDeviceRel.getBook_id())){
//                    if(bookDeviceRel.getStatus() == 0 || bookDeviceRel.getStatus() == 3){
//                        update_book_ids.add(bookDeviceRel.getBook_id());
//                    }
//                }
//            }
//            if(update_book_ids.size()>0){
//                bookDeviceRelMapper.updateBookDeviceRelStatus(orgid, device_id, StringUtils.join(update_book_ids, ","), 1);
//            }
//        }else{
//            //更新离线中、已离线状态为取消离线3
//            for(BookDeviceRel bookDeviceRel : bookList){
//                if(bookIds.contains(bookDeviceRel.getBook_id())){
//                    if(bookDeviceRel.getStatus() == 1 || bookDeviceRel.getStatus() == 2){
//                        update_book_ids.add(bookDeviceRel.getBook_id());
//                    }
//                }
//            }
//            if(update_book_ids.size()>0){
//                bookDeviceRelMapper.updateBookDeviceRelStatus(orgid, device_id, StringUtils.join(update_book_ids, ","), 3);
//            }
//        }
//    }

    @Override
    //查询机构所有分类下的某一书
    public List<BookOrgRel> selectOrgRelsByBookId(Long org_id, Long bkid) {
        return bookOrgRelMapper.selectOrgRelsByBookId(org_id, bkid);
    }

    @Override
    public List<Map<String, Object>> selectHotListByOrgIdAndCount(Long org_id, Integer count) {
        return bookOrgRelMapper.selectHotListByOrgIdAndCount(org_id, count);
    }

    @Override
    public List<Map<String, Object>> selectRecommendListByOrgIdAndCatIdAndCount(Long org_id, Long book_cat_id, Integer count) {
        return bookOrgRelMapper.selectRecommendListByOrgIdAndCatIdAndCount(org_id, book_cat_id, count);
    }

    @Override
    public BookBean findByOrgIdAndBookIdAndMemberId(Long org_id, Long bookId, Long member_id) {
        return bookOrgRelMapper.findByOrgIdAndBookIdAndMemberId(org_id, bookId, member_id);
    }

    @Override
    //数据包图书全部基本信息
    public List<Map<String, Object>> getAllBooksBaseInfo(Long org_id, Long book_cat_id) {
        String catPath = "";
        BookCat bookCat = bookCatMapper.selectOrgBookCat(org_id, book_cat_id);
        if (bookCat != null) {
            catPath = bookCat.getBook_cat_path();
        }
        return bookOrgRelMapper.getAllBooksBaseInfo(org_id, catPath);
    }

    @Override
    public Integer getCountByOrgId(Long org_id) {
        if (org_id ==1){
            return bookOrgRelMapper.getCount();
        }else {
            return bookOrgRelMapper.getCountByOrgId(org_id);
        }
    }

    @Override
    public List<BookInfo> findSameAuthorBooks(Long bookId, Long org_id, Integer limit) {
        return bookOrgRelMapper.findSameAuthorBooks(bookId,org_id,limit);
    }

    @Override
    public List<BookInfo> findSameCatalogBooks(Long bookId, Long org_id, Long bookCatId, Integer limit,String bookIds) {
        return bookOrgRelMapper.findSameCatalogBooks(bookId,org_id,bookCatId,limit,bookIds);
    }

    @Override
    public Long getCatId(Long book_id,Long org_id){
        return bookOrgRelMapper.getCatId(book_id,org_id);
    }

    @Override
    public Object getAppBooks(ApiPublicBookBean bean) {
        String catPath = "";
        //查找机构的分类
        BookCat bookCat = bookCatMapper.selectOrgBookCat(189L, bean.getBook_cat_id());
        if (bookCat != null) {
            catPath = bookCat.getBook_cat_path();
        }
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        List<AppBooksBean> result = bookOrgRelMapper.getAppBooks(189L, catPath,bean.getType(),bean.getSearchText(),bean.getOrder());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public PublicBookBean findByMemberIdAndBookId(Long book_id, Long member_id) {
            return bookOrgRelMapper.findByMemberIdAndBookId(book_id, member_id);
    }

    @Override
    public Object findRankList(PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        List<AppBooksBean> result = bookOrgRelMapper.findRankList();
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public List<BookBean> getHotList(Long org_id, Integer limit) {
        return bookOrgRelMapper.getHotList(org_id,limit);
    }

    @Override
    public List<BookBean> getRecommendList(Long org_id, Integer limit) {
        return bookOrgRelMapper.getRecommendList(org_id,limit);
    }
}
