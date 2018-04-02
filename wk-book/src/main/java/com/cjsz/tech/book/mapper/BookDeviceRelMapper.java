package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.domain.BookDeviceRel;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Administrator on 2016/12/25 0025.
 */
public interface BookDeviceRelMapper extends BaseMapper<BookDeviceRel> {

    //删除机构的图书设备离线关系表
    @Delete("delete from book_device_rel where org_id = #{0}")
    public void deleteByOrgId(Long org_id);

    //查询该机构该设备的图书
    @Select("select * from book_device_rel where org_id = #{0} and device_id = #{1}")
    public List<BookDeviceRel> selectInOffLineBooks(Long orgid, Long device_id);

    //查询该机构该设备的指定的图书
    @Select("select * from book_device_rel where org_id = #{0} and device_id = #{1} and book_id=#{2}")
    public List<BookDeviceRel> selectInOffLineBooksByBookId(Long orgid, Long device_id,Long book_id);

    //更新设备图书的离线状态
    @Update("update book_device_rel set status = #{status},update_time = now() where org_id = #{orgid} and device_id = #{device_id} and book_id in(${bookIds}) ")
    public void updateBookDeviceRelStatus(@Param("orgid") Long orgid, @Param("device_id") Long device_id, @Param("bookIds") String bookIds, @Param("status") Integer status);

    //查找关系
    @Select("select * from book_device_rel where device_id = #{0} and book_id = #{1} and org_id = #{2}")
    List<BookDeviceRel> selectBookDeviceRel(Long device_id, Long bkid, Long org_id);

    //更新设备的所有图书离线状态
    @Update("update book_device_rel set status = #{status}, update_time = now() where status != #{status} and device_id in(${device_ids})")
    public void updateBookDeviceRelStatusByDeviceIds(@Param("device_ids") String device_ids, @Param("status") Integer status);

    //所有图书设备关系的设备Id(不重复)
    @Select("SELECT DISTINCT device_id FROM `book_device_rel`")
    public List<Long> getAllDeviceIds();

}
