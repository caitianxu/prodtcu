package com.cjsz.tech.utils.images;


/**
 * 缩略图生成器工厂<br>
 * 
 * 
 * 2010-7-10下午11:40:28
 */
public  class ThumbnailCreatorFactory {

	/**
	 * 返回缩略图生成器
	 * @param source 图片原文件路径
	 * @param target 图片缩略图路径
	 * @return 
	 */
	public static final IThumbnailCreator getCreator(String source,String target){
		return new JavaImageIOCreator(source, target);
	}


	/**
	 * 返回缩略图生成器
	 * 説明：
	 *  最终文件格式:filename_tag_fileext
	 *  例如1.jpg -> 1_tag.jpg
	 * @param source 图片原文件路径
	 * @param addtag 文件名TAG
	 * @return
	 */
	public static final IThumbnailCreator getCreatorByTag(String source,String addtag){
		String target = "";
		int inx = source.lastIndexOf(".");
		target = source.substring(0,inx)+"_"+addtag+ source.substring(inx);
		System.out.println("target:"+target);
		return new JavaImageIOCreator(source, target);
	}
}
