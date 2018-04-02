package com.cjsz.tech.system.utils;

import org.springframework.web.multipart.MultipartFile;

/**
 * Author:Jason
 * Date:2016/11/28
 */
public interface UploadCallBack {

    Object onSuccess(MultipartFile file, String fileName, String uploadFilePath, String realPath);

    Object onFailure(Exception e);
}
