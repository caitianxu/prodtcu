package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.book.mapper.BookCatMapper;
import com.cjsz.tech.book.mapper.BookOrgRelMapper;
import com.cjsz.tech.book.service.BookCatService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.IDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
@Service
public class BookCatServiceImpl implements BookCatService {

    @Autowired
    private BookCatMapper bookCatMapper;

    @Autowired
    private BookOrgRelMapper bookOrgRelMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    //获取机构图书分类
    @Override
    public List<BookCat> getOrgCats(Long org_id) {
        return bookCatMapper.getOrgCats(org_id);
    }

    @Override
    //将资讯分类转化为树形结构
    public List<BookCat> selectTree(List<BookCat> cats1) {
        List<BookCat> rstList  = new ArrayList<BookCat>();
        while(cats1.size()>0){
            BookCat curMenu = cats1.get(0);
            if(!rstList.contains(curMenu)) {
                rstList.add(curMenu);
            }
            cats1.remove(0);
            List<BookCat> children = this.getChildren(cats1, curMenu.getBook_cat_id());
            curMenu.setChildren(children);
        }
        return rstList;
    }
    private List<BookCat> getChildren(List<BookCat> allList, Long pid){
        List<BookCat> children =new ArrayList<BookCat>();
        List<BookCat> copyList = new ArrayList<BookCat>();
        copyList.addAll(allList);
        for(BookCat curMenu :copyList){
            if(curMenu.getBook_cat_pid().equals(pid)){
                curMenu.setChildren(this.getChildren(allList, curMenu.getBook_cat_id()));
                children.add(curMenu);
                allList.remove(curMenu);
            }
        }
        return children;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "机构图书分类")
    @Transactional
    //保存分类
    public BookCat saveCat(BookCat catBean, Long org_id) {
        Long book_cat_id = Long.valueOf(IDUtil.createId());
        Long pkg_id = catBean.getPkg_id();
        String cat_path = "";	//分类的层次路径
        if(catBean.getBook_cat_pid() != null && catBean.getBook_cat_pid() != 0 ){
            pkg_id = 0L;
            //给机构添加分类时查询上级分类:是否数据包（1:是;2:否）
            BookCat p_cat = bookCatMapper.selectOrgBookCat(org_id, catBean.getBook_cat_pid());
            cat_path = p_cat.getBook_cat_path();
        }else{
            if(pkg_id == null){
                pkg_id = 0L;
            }
            catBean.setBook_cat_pid(0L);
            cat_path = "0|";
        }
        cat_path = cat_path + book_cat_id + "|";
        catBean.setBook_cat_path(cat_path);
        catBean.setCreate_time(new Date());
        //是否自建(1:自建;2:分配)
        catBean.setSource_type(1);
        catBean.setUpdate_time(new Date());

        String remark = catBean.getBook_cat_remark();
        String insertsql = "";
        if(StringUtils.isEmpty(remark)){
            insertsql = "insert into book_cat(book_cat_id,org_id,book_cat_pid,book_cat_path," +
                    "pkg_id,book_cat_name,order_weight,book_cat_remark,create_time,source_type,update_time) " +
                    "values ('" +book_cat_id+"',"+org_id+","+catBean.getBook_cat_pid()+",'"+cat_path+"',"+
                    pkg_id+",'"+catBean.getBook_cat_name()+"'," +catBean.getOrder_weight()+"," + null +",now(),1,now() )";
        }else{
            insertsql = "insert into book_cat(book_cat_id,org_id,book_cat_pid,book_cat_path," +
                    "pkg_id,book_cat_name,order_weight,book_cat_remark,create_time,source_type,update_time) " +
                    "values ('" +book_cat_id+"',"+org_id+","+catBean.getBook_cat_pid()+",'"+cat_path+"',"+
                    pkg_id+",'"+catBean.getBook_cat_name()+"'," +catBean.getOrder_weight()+",'" +catBean.getBook_cat_remark()+"',now(),1,now() )";
        }
        jdbcTemplate.update(insertsql);
        return catBean;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "机构图书分类")
    @Transactional
    //修改分类
    public BookCat updateCat(BookCat catBean) {
        //查找机构的分类
        BookCat p_cat = bookCatMapper.selectOrgBookCat(catBean.getOrg_id(), catBean.getBook_cat_id());
        String remark = catBean.getBook_cat_remark();
        String updatesql = "";
        if(StringUtils.isEmpty(remark)){
            updatesql = "update book_cat set book_cat_name = '" + catBean.getBook_cat_name() +
                    "',book_cat_pid = " + catBean.getBook_cat_pid() + ",order_weight = " + catBean.getOrder_weight() + ",update_time = now()" + ",book_cat_remark = " + null +
                    " where book_cat_id = " + catBean.getBook_cat_id() + " and org_id = " + catBean.getOrg_id() + " and pkg_id = " + catBean.getPkg_id();
        }else{
            updatesql = "update book_cat set book_cat_name = '" + catBean.getBook_cat_name() +
                    "',book_cat_pid = " + catBean.getBook_cat_pid() + ",order_weight = " + catBean.getOrder_weight() + ",update_time = now()" + ",book_cat_remark = '" + catBean.getBook_cat_remark() +
                    "' where book_cat_id = " + catBean.getBook_cat_id() + " and org_id = " + catBean.getOrg_id() + " and pkg_id = " + catBean.getPkg_id();
        }
        jdbcTemplate.update(updatesql);
        if(!catBean.getBook_cat_pid().equals(p_cat.getBook_cat_pid())){
            String cat_path = "";	//分类的层次路径
            if(catBean.getBook_cat_pid() != null && catBean.getBook_cat_pid() != 0 ){
                catBean.setPkg_id(0L);
                //给机构添加分类时查询上级分类:是否数据包（1:是;2:否）
                BookCat  p_cat1 = bookCatMapper.selectOrgBookCat(catBean.getOrg_id(), catBean.getBook_cat_pid());
                cat_path = p_cat1.getBook_cat_path();
            }else{
                catBean.setBook_cat_pid(0L);
                cat_path = "0|";
            }
            String old_full_path = catBean.getBook_cat_path();        //修改前层次路径
            String new_full_path = cat_path + catBean.getBook_cat_id() + "|";	//修改后层次路径
            //更新当前修改的分类以及下级所有分类的层次路径：将层次路径中包含old_full_path的，更新为new_full_path
            //修改机构的分类层次路径
            bookCatMapper.updateOrgFullPath(old_full_path, new_full_path, catBean.getOrg_id());
        }
        return catBean;
    }

    @Override
    //查找机构的分类集合
    public List<Long> getOrgAllCat(Long orgid, Long[] book_cat_ids){
        List<Long> resultList = new ArrayList<>();
        for(int i=0;i<book_cat_ids.length;i++) {
            //是否数据包数据（1:是;2:否）
            List<Long> curCatList = bookCatMapper.getOrgAllCatTree(orgid, book_cat_ids[i]);
            if(curCatList!=null && curCatList.size()>0) {
                resultList.addAll(curCatList);
            }
        }
        return resultList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "机构图书分类")
    @Transactional
    //删除机构的图书分类和详情
    public void deleteOrgAll(Long orgid, List<Long> catidList){
        String catids = StringUtils.join(catidList, ",");
        //删除图书
        bookOrgRelMapper.deleteOrgBookOrgRel(orgid, catids);
        //删除图书分类
        bookCatMapper.deleteOrgBookCat(orgid, catids);
    }

    @Override
    //获取当前分类本身和下级分类
    public List<BookCat> getOwnerCats(Long org_id, String book_cat_path) {
        return bookCatMapper.getOwnerCats(org_id, book_cat_path);
    }

    @Override
    public List<BookCat> selectFirstCatsByOrgId(Long org_id) {
        return bookCatMapper.selectFirstCatsByOrgId(org_id);
    }

    @Override
    public List<BookCat> selectFirstCatsByOrgIdAndCount(Long org_id, Integer count) {
        return bookCatMapper.selectFirstCatsByOrgIdAndCount(org_id, count);
    }

    @Override
    public List<BookCat> getList() {
        return bookCatMapper.getCatList();
    }

    @Override
    public List<BookCat> getListByPid(Long pid) {
        return bookCatMapper.getListByPid(pid);
    }

    @Override
    public List<BookCat> selectAllCats(Long org_id) {
        return bookCatMapper.getAllCats(org_id);
    }

    @Override
    public BookCat selectByCatId(Long book_cat_id,Long org_id) {
        return bookCatMapper.selectByCatId(book_cat_id,org_id);
    }

    @Override
    public List<BookCat> getOffLineNumList(Long orgid, String oldtimestamp, Object... otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return bookCatMapper.getOffLineNumList(orgid, oldtimestamp, num, size);
    }

    @Override
    public Integer hasOffLine(Long orgid, String oldtimestamp, Object... otherparam) {
        Integer checknum= bookCatMapper.hasOffLine(orgid, oldtimestamp);
        if (checknum ==null ) {
            checknum =0;
        }
        return checknum;
    }
}
