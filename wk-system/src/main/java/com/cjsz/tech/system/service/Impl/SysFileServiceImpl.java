package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.conditions.PageAndSortCondition;
import com.cjsz.tech.system.domain.SysFile;
import com.cjsz.tech.system.mapper.SysFileMapper;
import com.cjsz.tech.system.service.SysFileService;
import com.cjsz.tech.system.utils.FileTypeEnum;
import com.cjsz.tech.system.wrappers.SysFileWrapper;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author:Jason
 * Date:2016/11/28
 * 系统文件服务实现类
 */
@Service
public class SysFileServiceImpl implements SysFileService {
    @Autowired
    private SysFileMapper sysFileMapper;


    public void addFile(SysFile file){
        sysFileMapper.insert(file);
    }

    @Override
    public int update(SysFile file) {
        return sysFileMapper.updateByPrimaryKey(file);
    }

    @Override
    public SysFile findById(Long id) {
        return  sysFileMapper.selectById(id);
    }


    public static void  deleteFile(String realBasePath,String... paths){
        for(String path:paths){
            File file =new File(realBasePath+path);
            if(file.exists()){
                file.delete();
            }
        }
    }

    @Override
    public void delFiles(String[] ids,String realBasePath) {
        String inStr = StringUtils.join(ids, ",");
        List<SysFile> sysFiles = sysFileMapper.selectByIds(inStr);
        List<String> delFiles = new ArrayList<String>();//待删除的物理文件的路径
        for (SysFile sysFile : sysFiles) {
            Integer fileType = sysFile.getFile_type();
            String savePath  = sysFile.getSavePath();
            if (fileType == FileTypeEnum.FOLDER.code()) {//文件夹
                String fullPath = sysFile.getFullPath();
                //从数据库中删除指定文件夹以及所有下级
                sysFileMapper.deleteByFullPath(fullPath);
                List<String> paths = sysFileMapper.selectAllFileOfFolder(fullPath);
                delFiles.addAll(paths);
            }else {
                //从数据库中删除指定的文件记录
                sysFileMapper.delete(sysFile);
                delFiles.add(savePath);
            }
        }
        deleteFile(realBasePath,delFiles.toArray(new String[]{}));
    }


    @Override
    public void renameFile(Long id, String newFileName) {
        SysFile sysFile = sysFileMapper.selectByPrimaryKey(id);
        sysFile.setFile_name(newFileName);
        sysFile.setUpdate_time(new Date());
        sysFileMapper.updateByPrimaryKey(sysFile);
    }

    @Override
    public Object pageQuery(PageAndSortCondition pageCondition, Long root_org_id) {
        String sortField = pageCondition.getSort_field();
        String sortOrder = pageCondition.getSort_order();
        if(sortField==null){
            sortField="create_time";
        }
        if(sortOrder==null){
            sortOrder="desc";
        }
        Sort sort=new Sort(Sort.Direction.fromString(sortOrder),sortField);
        PageHelper.startPage(pageCondition.getPageNum(), pageCondition.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<SysFile> result = sysFileMapper.findLike(root_org_id,pageCondition.getSearchText() );
        PageList pageList = new PageList(result, new SysFileWrapper());
        return pageList;
    }

    @Override
    public Object pageQueryOfDir(PageAndSortCondition condition, Long dirId, Long root_org_id) {
        String sortField = condition.getSort_field();
        String sortOrder = condition.getSort_order();
        if(sortField==null){
            sortField="create_time";
        }
        if(sortOrder==null){
            sortOrder="desc";
        }
        Sort sort=new Sort(Sort.Direction.fromString(sortOrder),sortField);
        PageHelper.startPage(condition.getPageNum(), condition.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<SysFile> result = sysFileMapper.findFilesOfDir(root_org_id,dirId);
        PageList pageList = new PageList(result, new SysFileWrapper());
        return pageList;
    }




}
