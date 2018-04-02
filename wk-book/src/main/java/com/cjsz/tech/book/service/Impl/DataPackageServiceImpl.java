package com.cjsz.tech.book.service.Impl;


import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.AllortedOrgBean;
import com.cjsz.tech.book.beans.CopyPkgBean;
import com.cjsz.tech.book.beans.DataPackagesBean;
import com.cjsz.tech.book.domain.*;
import com.cjsz.tech.book.mapper.*;
import com.cjsz.tech.book.service.DataPackageService;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.mapper.DeviceMapper;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
@Service
public class DataPackageServiceImpl implements DataPackageService {

    @Autowired
    private DataPackageMapper dataPackageMapper;

    @Autowired
    private BookCatMapper bookCatMapper;

    @Autowired
    private PkgBookCatMapper pkgBookCatMapper;

    @Autowired
    private BookOrgRelMapper bookOrgRelMapper;

    @Autowired
    private PkgBookRelMapper pkgBookRelMapper;

    @Autowired
    private PkgOrgRelMapper pkgOrgRelMapper;

    @Autowired
    private BookDeviceRelMapper bookDeviceRelMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    //数据包分页列表
    @Override
    public Object pageQuery(Sort sort, PageConditionBean bean) {
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        List<DataPackage> result = dataPackageMapper.getPkgList(bean.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    //新增数据包
    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "图书数据包")
    public void savePkg(DataPackage dataPackage) {
        dataPackage.setCreate_time(new Date());
        dataPackage.setUpdate_time(new Date());
        dataPackageMapper.insert(dataPackage);
    }

    //修改数据包
    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书数据包")
    public void updatePkg(DataPackage dataPackage) {
        dataPackage.setUpdate_time(new Date());
        dataPackageMapper.updateByPrimaryKey(dataPackage);
    }

    //分配数据包
    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "图书机构关系")
    public void allotPkg(Long org_id, Long pkg_id, List<Organization> orgs) {
        List<BookCat> allBookCats = new ArrayList<BookCat>();
        List<BookOrgRel> allBookOrgRels = new ArrayList<BookOrgRel>();
        List<BookDeviceRel> allBookDeviceRels = new ArrayList<BookDeviceRel>();
        for(Organization org : orgs){
            //查询分配机构所在数据包，数量减少1
            PkgOrgRel rel = pkgOrgRelMapper.selectByOrgId(org.getOrg_id());
            if(rel != null){
                dataPackageMapper.updateCountByPkgId(rel.getPkg_id(), -1);
            }
            //删除数据包机构关系表
            pkgOrgRelMapper.deleteByOrgId(org.getOrg_id());
            //删除机构的图书设备离线关系表
            bookDeviceRelMapper.deleteByOrgId(org.getOrg_id());
            //删除机构的图书表
            bookOrgRelMapper.deleteByOrgId(org.getOrg_id());
            //删除机构的图书分类表
            bookCatMapper.deleteByOrgId(org.getOrg_id());
            //数据包机构关系表
            PkgOrgRel pkgOrgRel = new PkgOrgRel(org.getOrg_id(), pkg_id, new Date());
            pkgOrgRelMapper.insert(pkgOrgRel);
            //图书分类表
            List<PkgBookCat> bookCats = pkgBookCatMapper.getPkgCats(org_id, pkg_id);
            for(PkgBookCat cat : bookCats){
                BookCat bookCat = new BookCat();
                bookCat.setBook_cat_id(cat.getBook_cat_id());
                bookCat.setOrg_id(org.getOrg_id());
                bookCat.setPkg_id(pkg_id);
                bookCat.setBook_cat_pid(cat.getBook_cat_pid());
                bookCat.setBook_cat_path(cat.getBook_cat_path());
                bookCat.setBook_cat_name(cat.getBook_cat_name());
                bookCat.setOrder_weight(cat.getOrder_weight());
                bookCat.setBook_cat_remark(cat.getBook_cat_remark());
                bookCat.setCreate_time(new Date());
                bookCat.setSource_type(2);//是否自建(1:自建;2:分配)
                bookCat.setUpdate_time(new Date());
                allBookCats.add(bookCat);
            }
            //图书表
            List<PkgBookRel> pkgBookRels = pkgBookRelMapper.selectAllPkgRels(pkg_id, org_id);
            for(PkgBookRel pkgBookRel : pkgBookRels){
                BookOrgRel bookOrgRel = new BookOrgRel();
                bookOrgRel.setBook_id(pkgBookRel.getBook_id());
                bookOrgRel.setOrg_id(org.getOrg_id());
                bookOrgRel.setBook_cat_id(pkgBookRel.getBook_cat_id());
                bookOrgRel.setPkg_id(pkg_id);
                bookOrgRel.setOrder_weight(pkgBookRel.getOrder_weight());
                bookOrgRel.setIs_hot(pkgBookRel.getIs_hot());
                bookOrgRel.setIs_recommend(pkgBookRel.getIs_recommend());
                bookOrgRel.setOffline_status(pkgBookRel.getOffline_status());
                bookOrgRel.setClick_count(0L);//点击量
                bookOrgRel.setCollect_count(0L);//收藏数
                bookOrgRel.setShare_count(0L);//分享数
                bookOrgRel.setReview_count(0L);//评论数
                bookOrgRel.setRead_count(0L);//阅读数
                bookOrgRel.setEnabled(1);//是否启用(1: 启用；2:停用)
                bookOrgRel.setCreate_time(new Date());
                bookOrgRel.setUpdate_time(new Date());
                allBookOrgRels.add(bookOrgRel);
            }
            //机构设备图书关系表
            List<Device> devices = deviceMapper.getDevicesByOrgId(org.getOrg_id());
            //机构的每个设备添加机构的离线图书关系
            for(Device device : devices){
                for(PkgBookRel pkgBookRel : pkgBookRels){
                    //0:不离线;1:发送离线
                    if(pkgBookRel.getOffline_status() == 1){
                        BookDeviceRel bookDeviceRel = new BookDeviceRel(device.getDevice_id(), pkgBookRel.getBook_id(), pkgBookRel.getBook_cat_id(),
                                org.getOrg_id(), new Date(), 1, new Date());//1:离线中
                        allBookDeviceRels.add(bookDeviceRel);
                    }else{
                        BookDeviceRel bookDeviceRel = new BookDeviceRel(device.getDevice_id(), pkgBookRel.getBook_id(), pkgBookRel.getBook_cat_id(),
                                org.getOrg_id(), new Date(), 0, new Date());//0:未离线;1:离线中;2:已离线;3:取消离线
                        allBookDeviceRels.add(bookDeviceRel);
                    }
                }
            }

        }
        //数据包机构数修改（通过查询）
        dataPackageMapper.updateCountByPkgId(pkg_id, orgs.size());
        if(allBookCats.size()>0){
            bookCatMapper.insertList(allBookCats);
        }
        if(allBookOrgRels.size()>0){
            bookOrgRelMapper.insertList(allBookOrgRels);
        }
        if(allBookDeviceRels.size()>0){
            bookDeviceRelMapper.insertList(allBookDeviceRels);
        }
    }

    @Override
    //数据包分配给机构的机构列表
    public List<AllortedOrgBean> getAllortOrgs(Long pkg_id) {
        return pkgOrgRelMapper.getAllortOrgs(pkg_id);
    }

    //删除数据包
    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "图书数据包")
    public void deletePkgs(DataPackagesBean bean) {
        List<DataPackage> pkgs = bean.getDataPackages();
        List<Long> pkgIds = new ArrayList<Long>();
        for(DataPackage dataPackage : pkgs){
            pkgIds.add(dataPackage.getPkg_id());
        }
        String pkgIds_str = StringUtils.join(pkgIds, ",");
        //删除数据包图书关系
        pkgBookRelMapper.deleteByPkgIds(pkgIds_str);
        //删除数据包图书分类
        pkgBookCatMapper.deleteByPkgIds(pkgIds_str);
        //删除数据包机构关系
        pkgOrgRelMapper.deleteByPkgIds(pkgIds_str);
        //删除数据包
        dataPackageMapper.deleteByPkgIds(pkgIds_str);
    }


    //复制数据包
    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "图书数据包")
    public DataPackage copyPkg(Long user_id, Long org_id, CopyPkgBean bean) {
        DataPackage dataPackage = bean.getDataPackage();
        //新增数据包
        DataPackage pkg = new DataPackage();
        pkg.setPkg_name(bean.getPkg_name());
        pkg.setOrg_count(0L);
        pkg.setBook_count(dataPackage.getBook_count());
        pkg.setBook_real_count(dataPackage.getBook_real_count());
        pkg.setCreate_user_id(user_id);
        pkg.setCreate_time(new Date());
        pkg.setUpdate_time(new Date());
        dataPackageMapper.insert(pkg);
        List<PkgBookCat> allBookCats = new ArrayList<PkgBookCat>();
        List<PkgBookRel> allPkgBookRels = new ArrayList<PkgBookRel>();
        //数据包图书分类表
        List<PkgBookCat> bookCats = pkgBookCatMapper.getPkgCats(org_id, dataPackage.getPkg_id());
        for(PkgBookCat bookCat : bookCats) {
            bookCat.setPkg_id(pkg.getPkg_id());
            bookCat.setCreate_time(new Date());
            bookCat.setUpdate_time(new Date());
            allBookCats.add(bookCat);
        }
        //数据包图书关系表
        List<PkgBookRel> pkgBookRels = pkgBookRelMapper.selectAllPkgRels(dataPackage.getPkg_id(), org_id);
        for(PkgBookRel pkgBookRel : pkgBookRels){
            pkgBookRel.setPkg_id(pkg.getPkg_id());
            pkgBookRel.setCreate_time(new Date());
            pkgBookRel.setUpdate_time(new Date());
            allPkgBookRels.add(pkgBookRel);
        }
        pkgBookCatMapper.insertList(allBookCats);
        pkgBookRelMapper.insertList(allPkgBookRels);
        return pkg;
    }

    @Override
    //更新数据包图书数量(包括重复)
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书数据包")
    public void updatePkgBookCount(Long pkg_id, Long book_count) {
        dataPackageMapper.updatePkgBookCount(pkg_id, book_count);
    }

    @Override
    //更新数据包图书数量(去重复)
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书数据包")
    public void updatePkgBookRealCount(Long pkg_id, Long book_real_count) {
        dataPackageMapper.updatePkgBookRealCount(pkg_id, book_real_count);
    }

    @Override
    //通过pkg_id查询数据包
    public DataPackage selectById(Long pkg_id) {
        return dataPackageMapper.selectById(pkg_id);
    }


}
