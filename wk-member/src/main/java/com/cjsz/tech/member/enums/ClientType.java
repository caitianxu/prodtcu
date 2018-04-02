package com.cjsz.tech.member.enums;

/**
 * Created by Administrator on 2017/10/18 0018.
 */
public enum  ClientType {

    /**
     * 企业版
     */
    ORG_VERSION("QY"),
    /**
     * 大众版
     */
    PUBLIC_VERSION("DZ");


    private String code;

    ClientType(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }



}
