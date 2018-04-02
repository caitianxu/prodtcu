package com.cjsz.tech.book.service;

import com.cjsz.tech.book.domain.BookDeviceRel;

import java.util.List;

/**
 * Created by Administrator on 2016/12/25 0025.
 */
public interface BookDeviceRelService {

    /**
     * 更新设备的图书离线状态
     * @param orgid
     * @param device_id
     * @param bookIds
     * @param status
     */
    public void updateBookDeviceRelStatus(Long orgid, Long device_id, String bookIds, Integer status);

    /**
     * 查找关系
     * @param device_id
     * @param bkid
     * @param org_id
     * @return
     */
    List<BookDeviceRel> selectBookDeviceRel(Long device_id, Long bkid, Long org_id);

    /**
     * 保存关系
     * @param bookDeviceRel
     * @return
     */
    public void saveBookDeviceRel(BookDeviceRel bookDeviceRel);

    /**
     * 保存关系
     * @param bookDeviceRels
     * @return
     */
    public void saveBookDeviceRels(List<BookDeviceRel> bookDeviceRels);

    /**
     * 所有图书设备关系的设备Id(不重复)
     * @return
     */
    public List<Long> getAllDeviceIds();
}
