package com.cjsz.tech.system.annotation;

import com.cjsz.tech.system.utils.SysActionLogType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Bruce on 2016/11/15.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SysActionLogAnnotation {
    public SysActionLogType action_type(); // 操作类型

    public String action_log_module_name(); // 模板名
}
