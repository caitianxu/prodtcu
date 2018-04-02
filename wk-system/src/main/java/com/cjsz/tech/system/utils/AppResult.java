package com.cjsz.tech.system.utils;

import java.io.Serializable;

/**
 * Created by yunke on 16/3/3.
 */

/**
 * APP消息返回
 */
public class AppResult<T> implements Serializable {

    public static Integer ERROR = 1;

    public static Integer WARN = 2;

    public static Integer SUCCESS = 0;

    private Integer code;

    private String msg;

    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 获取成功消息
     * @param message
     * @return
     */
    public static AppResult getSuccess(String message) {
        AppResult msg = new AppResult();
        msg.setCode(SUCCESS);
        msg.setMsg(message);
        return msg;
    }

    /**
     * 获取ERROR消息
     */
    public static AppResult getError(String message) {
        AppResult msg = new AppResult();
        msg.setCode(ERROR);
        msg.setMsg(message);
        return msg;
    }

    /**
     * 获取提示信息
     */
    public static AppResult getWarn(String message) {
        AppResult msg = new AppResult();
        msg.setCode(WARN);
        msg.setMsg(message);
        return msg;
    }

}
