package com.cjsz.tech.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class JsonResult<T> implements Serializable {

	public static Integer SUCCESS = 0; 

	public static Integer ERROR = 1;

	public static Integer OTHER = 2;	//其他情况

	public static Integer EXCEPTION = 500; //异常
	
	public static Integer EXPIRE = 600;   //token失效

	public Integer code;

	public String message;

	public T data;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static JsonResult getSuccess(String message) {
		JsonResult result = new JsonResult();
		result.setCode(SUCCESS);
		result.setMessage(message);
		return result;
	}

	public static JsonResult getError(String message) {
		JsonResult result = new JsonResult();
		result.setCode(ERROR);
		result.setMessage(message);
		result.setData(new ArrayList());
		return result;
	}

	public static JsonResult getObjError(String message) {
		JsonResult result = new JsonResult();
		result.setCode(ERROR);
		result.setMessage(message);
		result.setData(null);
		return result;
	}

	public static JsonResult getException(String message) {
		JsonResult result = new JsonResult();
		result.setCode(EXCEPTION);
		result.setMessage(message);
		result.setData(new ArrayList());
		return result;
	}
	
	public static JsonResult getExpire(String message) {
		JsonResult result = new JsonResult();
		result.setCode(EXPIRE);
		result.setMessage(message);
		result.setData(new ArrayList());
		return result;
	}

	public static JsonResult getOther(String message) {
		JsonResult result = new JsonResult();
		result.setCode(OTHER);
		result.setMessage(message);
		result.setData(new ArrayList());
		return result;
	}
}