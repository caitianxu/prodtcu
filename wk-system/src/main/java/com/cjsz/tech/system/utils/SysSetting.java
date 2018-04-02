package com.cjsz.tech.system.utils;

import com.cjsz.tech.core.SpringContextUtil;

import java.io.File;

/**
 * 系统设置
 * Created by shiaihua on 16/12/21.
 */
public class SysSetting {

    /**
     * 获取默认的离线文件存储路径
     * @return
     */
    public static String getOffLine_BaseUrl() {
        try {
            return SpringContextUtil.getApplicationContext().getResource("").getFile().getPath();
        }catch (Exception e) {
            e.printStackTrace();
        }
        String def_dir = System.getProperty("user.dir")+File.separator+"offline";
        File defdirFile = new File(def_dir);
        if(!defdirFile.exists()) {
            defdirFile.mkdir();
        }
        return def_dir;
    }
}
