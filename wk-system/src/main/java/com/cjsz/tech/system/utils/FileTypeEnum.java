package com.cjsz.tech.system.utils;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Author:Jason
 * Date:2016/11/28
 * 文件分类枚举
 */
public enum FileTypeEnum {

    FOLDER(-1),

    OTHER(0),
    BOOK(10),
    IMAGE(20),
    VIDEO(30),
    AUDIO(40),
    DOC(50);


    public static final String[] BOOK_EXTENSION = {
            "epub",
    };

    public static final String[] IMAGE_EXTENSION = {
            "bmp", "gif", "jpg", "jpeg", "png"
    };


    public static final String[] VIDEO_EXTENTION={

          "mp4", "avi","mpg","asf","rm","rmvb", "swf","flv"
    };

    public static final String[] AUDIO_EXTENTION={
            "mp3","wav","wma","wmv","mid"
    };

    public static final String[] DOC_EXTENTION={
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt", "pdf"
    };


    private Integer code;
    FileTypeEnum(int code) {
        this.code=code;
    }


    public Integer code() {
        return code;
    }



    public static FileTypeEnum getFileType(String extName){
        if(ArrayUtils.contains(IMAGE_EXTENSION,extName)){
           return IMAGE;
        }
        if(ArrayUtils.contains(VIDEO_EXTENTION,extName)){
           return  VIDEO;
        }
        if(ArrayUtils.contains(AUDIO_EXTENTION,extName)){
            return  AUDIO;
        }
        if(ArrayUtils.contains(BOOK_EXTENSION,extName)){
            return  BOOK;
        }
        if(ArrayUtils.contains(DOC_EXTENTION,extName)){
            return  DOC;
        }
        return OTHER;
    }
}
