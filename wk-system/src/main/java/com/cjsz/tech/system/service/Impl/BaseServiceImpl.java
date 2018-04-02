package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.system.service.BaseService;
import com.cjsz.tech.utils.images.ImageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by Administrator on 2017/2/8 0008.
 */
@Service
public class BaseServiceImpl implements BaseService {
    @Override
    public String getImgScale(String srcPath, String addtag, double val) {
        //图片处理，压缩图片
        String base_path = null;
        try {
            base_path = SpringContextUtil.getApplicationContext().getResource("").getFile().getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageHelper.getTransferImageByScale(base_path + srcPath, "small", 0.5);
        int i = srcPath.lastIndexOf(".");
        String img_small = srcPath.substring(0,i) + "_small" + srcPath.substring(i);
        return img_small;
    }
}
