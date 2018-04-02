package com.cjsz.tech.api.beans;

/**
 * Author:Jason
 * Date:2017/7/6
 * 终端类型枚举
 */
public enum ClientTypeEnum {
    /**
     * 大屏
     */
    LARGE_SCREEN("000001"),
    /**
     * ios
     */
    IOS("000002"),
    /**
     * 安卓
     */
    ANDROID("000003"),
    /**
     * 网站
     */
    WEB("000004");
    private String code;

    ClientTypeEnum(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }
}
