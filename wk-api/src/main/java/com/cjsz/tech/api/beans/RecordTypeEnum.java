package com.cjsz.tech.api.beans;

/**
 * Author:Jason
 * Date:2017/7/6
 * 记录类型枚举
 */
public enum RecordTypeEnum {
    /**
     * 点击详情
     */
    CLICK(1),
    /**
     * 收藏
     */
    FAVORITE(2),
    /**
     * 分享
     */
    SHARE(3),
    /**
     * 评论
     */
    COMMENT(4),
    /**
     * 阅读
     */
    READ(5);
    private Integer code;

    RecordTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer code() {
        return this.code;
    }
}
