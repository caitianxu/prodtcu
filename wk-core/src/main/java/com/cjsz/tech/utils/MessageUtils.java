/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.cjsz.tech.utils;

import com.cjsz.tech.core.SpringContextUtil;
import org.springframework.context.MessageSource;

/**
 * 消息国际化类
 */
public class MessageUtils {

    private static MessageSource messageSource;

    /**
     * 根据消息键和参数 获取消息
     * 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return
     */
    public static String message(String code, Object... args) {
        if (messageSource == null) {
            messageSource = (MessageSource) SpringContextUtil.getBean("messageSource");
        }
        return messageSource.getMessage(code, args, null);
    }

}
