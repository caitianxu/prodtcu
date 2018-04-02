package com.cjsz.tech.utils.images;

public class ImageRuntimeException extends RuntimeException {
	public ImageRuntimeException(String path,String proesstype){
		super("对图片"+path +"进行"  + proesstype +"出错" );
	}
}
