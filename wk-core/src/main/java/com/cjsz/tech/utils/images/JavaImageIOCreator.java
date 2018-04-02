package com.cjsz.tech.utils.images;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 使用javax image io生成缩略图
 *
 * 2010-7-10下午11:43:05
 */
public class JavaImageIOCreator implements IThumbnailCreator {
	private String srcFile;
	private String destFile;

	private static Map<String, String> extMap;
	static {
		extMap = new HashMap<String, String>(5);
		extMap.put("jpg", "JPEG");
		extMap.put("jpeg", "JPEG");
		extMap.put("gif", "GIF");
		extMap.put("png", "PNG");
		extMap.put("bmp", "BMP");

	}

	public JavaImageIOCreator(){}

	public JavaImageIOCreator(String sourcefile, String targetFile) {
		this.srcFile =sourcefile;
		this.destFile = targetFile;
	}

	@Override
    public void scale(double scale) {
		//TODO 图片压缩
		String ext = this.getFileExt(srcFile).toLowerCase();
		BufferedImage image;
		try {
			image = ImageIO.read(new File(srcFile));
			ImageIO.write(Lanczos.resizeImage(image, scale), ext, new File(destFile));
			System.out.println("scale over");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("生成缩略图错误",e);
		}
	}

	@Override
    public void resize(int w, int h) {
		String ext = this.getFileExt(srcFile).toLowerCase();

		BufferedImage image;
		try {
			image = ImageIO.read(new File(srcFile));
			ImageIO.write(Lanczos.resizeImage(image, w, h), ext, new File(destFile));
			System.out.println("resize over");
		} catch (IOException e) {
			e.printStackTrace();
			 throw new RuntimeException("生成缩略图错误",e);
		}
	}

	@Override
    public void cut(int x, int y, int width, int height) {
		FileInputStream is = null;
		ImageInputStream iis = null;
		try {
			String ext = this.getFileExt(srcFile).toLowerCase();
			is = new FileInputStream(srcFile);
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(ext);
			ImageReader reader = it.next();
			iis = ImageIO.createImageInputStream(is);
			reader.setInput(iis, true);
			ImageReadParam param = reader.getDefaultReadParam();
			Rectangle rect = new Rectangle(x, y, width, height);
			param.setSourceRegion(rect);
			BufferedImage bi = reader.read(0, param);
			ImageIO.write(bi, ext, new File(destFile));
		} catch (Exception e) {
			throw new RuntimeException("生成缩略图错误",e);
		}finally {
			try {
				if (is != null) {
                    is.close();
                }
				if (iis != null) {
                    iis.close();
                }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 得到文件的扩展名
	 *
	 * @param fileName
	 * @return
	 */
	public String getFileExt(String fileName) {
		int potPos = fileName.lastIndexOf('.') + 1;
		String type = fileName.substring(potPos, fileName.length());
		return type;
	}
//	public static void main(String args[]){
//		JavaImageIOCreator creator = new JavaImageIOCreator("d:/1.jpg", "d:/1_j_180.jpg");
//		creator.resize(180, 180);
//	}


	@Override
    public byte[] getImageByte(String srcFile) {
		File file = null;
		BufferedImage bi;
		try {
			file = new File(srcFile);
			bi = ImageIO.read(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String ext = this.getFileExt(srcFile).toLowerCase();
			ImageIO.write(bi, ext, baos);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
    public byte[] getImageByteByScale(double scale, byte[] bytes) {
		//TODO 图片压缩
		InputStream inputStream;
		BufferedImage image;
		try {
			inputStream = new ByteArrayInputStream(bytes);
			image = ImageIO.read(inputStream);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(Lanczos.resizeImage(image, scale), "jpg", baos);
			ImageIO.write(image, "jpg", baos);
			bytes = baos.toByteArray();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
