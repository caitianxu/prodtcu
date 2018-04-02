package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.book.domain.BookDeviceRel;
import com.cjsz.tech.book.mapper.BookDeviceRelMapper;
import com.cjsz.tech.book.service.BookDeviceRelService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2016/12/25 0025.
 */
@Service
public class BookDeviceRelServiceImpl implements BookDeviceRelService {

    @Autowired
    private BookDeviceRelMapper bookDeviceRelMapper;

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书设备关系")
    public void updateBookDeviceRelStatus(Long orgid, Long device_id, String bookIds, Integer status){
        bookDeviceRelMapper.updateBookDeviceRelStatus(orgid, device_id, bookIds, status);
    }

    @Override
    public List<BookDeviceRel> selectBookDeviceRel(Long device_id, Long bkid, Long org_id) {
        return bookDeviceRelMapper.selectBookDeviceRel(device_id, bkid, org_id);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "图书设备关系")
    public void saveBookDeviceRel(BookDeviceRel bookDeviceRel) {
        bookDeviceRelMapper.insert(bookDeviceRel);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "图书设备关系")
    public void saveBookDeviceRels(List<BookDeviceRel> bookDeviceRels) {
        for (BookDeviceRel bookDeviceRel:bookDeviceRels){
            bookDeviceRelMapper.insert(bookDeviceRel);
        }
    }

    @Override
    public List<Long> getAllDeviceIds() {
        return bookDeviceRelMapper.getAllDeviceIds();
    }

}
