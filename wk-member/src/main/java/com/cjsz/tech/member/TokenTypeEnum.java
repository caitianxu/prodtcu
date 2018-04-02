package com.cjsz.tech.member;

/**
 * Author:Jason
 * Date:2017/7/4
 */
public enum TokenTypeEnum {
    PC(1),
    MOBILE(2);
    private Integer code;

    TokenTypeEnum(Integer code) {
        this.code = code;
    }
    public Integer code(){
        return code;
    }
}
