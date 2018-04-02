package com.cjsz.tech.system.service;

/**
 * Created by Administrator on 2017/2/8 0008.
 */
public interface BaseService {

    /**
     * 获取图片压缩后路径
     * @param srcPath
     * @param addtag
     * @param val
     * @return
     */
    public String getImgScale(String srcPath, String addtag, double val);

}
