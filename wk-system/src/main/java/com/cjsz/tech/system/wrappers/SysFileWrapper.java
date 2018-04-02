package com.cjsz.tech.system.wrappers;

import com.cjsz.tech.core.page.BaseWrapper;
import com.cjsz.tech.system.domain.SysFile;
import com.cjsz.tech.system.utils.FileTypeEnum;
import com.cjsz.tech.utils.DateUtils;
import com.cjsz.tech.utils.PrettyMemoryUtils;

import java.util.*;

/**
 * Author:Jason
 * Date:2016/11/29
 */
public class SysFileWrapper implements BaseWrapper<Map, SysFile> {

    @Override
    public List<Map> toDTOList(Collection<SysFile> objectList) {
        if (objectList == null) {
            return new ArrayList<Map>();
        }
        List<Map> results = new ArrayList<Map>();
        for (SysFile sysFile : objectList) {
            results.add(toDTO(sysFile));
        }
        return results;
    }

    @Override
    public Map toDTO(SysFile o) {
        if (o == null) {
            return null;
        }
        Map<String, Object> r = new HashMap<String, Object>();
        Integer fileType = o.getFile_type();
        String size="";
        if(fileType== FileTypeEnum.FOLDER.code()){
            size="-";
        }else{
            size=PrettyMemoryUtils.prettyByteSize(o.getFile_size());
        }
        r.put("file_id", o.getFile_id());
        r.put("create_userid", o.getCreate_userid());
        r.put("file_format", o.getFile_format());
        r.put("file_type", o.getFile_type());
        r.put("file_name", o.getFile_name());
        r.put("file_size", size);
        r.put("file_cover",o.getFile_cover());
        r.put("org_id", o.getOrg_id());
        r.put("savePath", o.getSavePath());
        Date create_time = o.getCreate_time();
        Date update_time = o.getUpdate_time();
        if(create_time != null) {
            r.put("create_time", DateUtils.getDateTime(create_time));
        }
        if(update_time != null) {
            r.put("update_time", DateUtils.getDateTime(update_time));
        }
        return r;
    }
}
