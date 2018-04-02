package com.cjsz.tech.utils.images;

public interface IThumbnailCreator {

	/**
	 * 改变图片大小
	 * @param w 新宽度
	 * @param h 新高度
	 */
	public void resize(int w, int h) ;

	/**
	 * 图片缩放
	 * @param scale >0
     */
	public void scale(double scale);

	/**
	 *图片裁剪
	 * @param x 剪切点x坐标		>0 && <max_x
	 * @param y 剪切点y坐标		>0 && <max_y
	 * @param width 剪切点宽度		>0
	 * @param height 剪切点高度		>0
	 */
	public void cut(int x, int y, int width, int height);

	/**
	 * 通过图片的绝对路径转化图片为二进制
	 * @param file_path
	 * @return
	 */
	public byte[] getImageByte(String file_path) ;

	/**
	 * 通过图片的二进制压缩为新的二进制
	 * @param scale
	 * @param bytes
	 * @return
	 */
	public byte[] getImageByteByScale(double scale, byte[] bytes);
}
