package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.domain.DataPackage;
import com.cjsz.tech.book.provider.DataPackageProvider;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface DataPackageMapper extends BaseMapper<DataPackage>{

    //数据包分页列表
    @SelectProvider(type = DataPackageProvider.class, method = "getPkgList")
    public List<DataPackage> getPkgList(@Param("searchText") String searchText);

    //数据包机构数变更
    @Update("update pkg p set p.org_count = (p.org_count + (#{1})),p.update_time = now() where p.pkg_id = #{0}")
    public void updateCountByPkgId(Long pkg_id, Integer count);

    //数据包删除
    @Delete("delete from pkg where pkg_id in(${pkgIds})")
    public void deleteByPkgIds(@Param("pkgIds") String pkgIds);

    //更新数据包图书数量(包括重复)
    @Update("update pkg set book_count = book_count + #{1}, update_time = now() where pkg_id = #{0}")
    public void updatePkgBookCount(Long pkg_id, Long book_count);


    //更新数据包图书数量(去重复)
    @Update("update pkg set book_real_count = book_real_count + #{1}, update_time = now() where pkg_id = #{0}")
    void updatePkgBookRealCount(Long pkg_id, Long book_real_count);

    //通过pkg_id查询数据包
    @Select("select * from pkg where pkg_id = #{0}")
    DataPackage selectById(Long pkg_id);
}
