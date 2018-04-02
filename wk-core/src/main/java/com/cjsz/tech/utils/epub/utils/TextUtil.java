package com.cjsz.tech.utils.epub.utils;

/**
 *
 */
public class TextUtil {

    public static boolean isEmpty(String str){
		return str == null || str.equals("");
	}

    public static boolean isString2Int(String str){
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
    
    public static int obj2int(Object object){
    	try {
    		return Integer.parseInt(object.toString());
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
    }
}
