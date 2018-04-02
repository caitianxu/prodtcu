package com.cjsz.tech.utils;

import com.cjsz.tech.beans.FileForm;
import com.cjsz.tech.beans.ImagesBean;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.utils.images.ImageHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeleilei on 2017/6/28.
 */
public class PdfToImageUtils {

    /**
     * 解决图片失真，并依次全部解析PDF文件(PDF文件的每一页都解析成一张png格式的图片)
     *
     * @param fileForm
     * @throws Exception
     */
    public static List<ImagesBean> pdfToImage(FileForm fileForm) {
        List<ImagesBean> list = new ArrayList<>();
        List<ImagesBean> list_sift = new ArrayList<>();
        try {
            String rootpath = SpringContextUtil.getApplicationContext().getResource("").getFile().getPath();
            File file = new File(rootpath + fileForm.getUrl());
            PDDocument doc = PDDocument.load(file);
            if (null != doc) {
                PDFRenderer renderer = new PDFRenderer(doc);
                if (null != renderer) {
                    int pageCount = doc.getNumberOfPages();
                    String imgpath = file.getPath().replace(file.getName(), "") + "unfile";
                    //创建加压目录
                    File unFile = new File(imgpath);
                    if (!unFile.exists()) {
                        unFile.mkdirs();
                    }
                    String child_file_name = IDUtil.createId();
                    imgpath = imgpath + "/" + child_file_name;
                    File savefile = new File(imgpath);
                    if (!savefile.exists()) {
                        savefile.mkdirs();

                    } else {
                        //删除内容
                        File[] save_files = savefile.listFiles();
                        if (null != save_files && save_files.length > 0) {
                            for (File image : save_files) {
                                image.delete();
                            }
                        }
                    }
                    imgpath = imgpath.replace(rootpath, "").replaceAll("\\\\", "/");
                    for (int i = 0; i < pageCount; i++) {
                        ImagesBean bean = new ImagesBean();
                        BufferedImage image = renderer.renderImage(i, 2.0f);
                        String imgName = "img" + (i + 1) + ".jpg";
                        String srcPath = rootpath + imgpath + "/" + imgName;
                        File imgfile = new File(srcPath);
                        System.out.println("开始创建第" + (i + 1) + "张:" + imgfile.getPath());
                        ImageIO.write(image, "jpg", imgfile);
                        ImageHelper.getTransferImageByScale(srcPath, "small", 0.5); // 将图片的尺寸大小缩小一半
                        bean.setPage(i + 1);
                        bean.setImg(imgpath + "/" + imgName);
                        bean.setImg_small(imgpath + "/img" + (i + 1) + "_small.jpg");
                        list.add(bean);
                    }

                    if (list.size() > 1) {
                        //判断是否需要拆分
                        File sourceFile = new File(rootpath + list.get(1).getImg());
                        BufferedImage bufferedImg = ImageIO.read(sourceFile);
                        int imgWidth = bufferedImg.getWidth();
                        int imgHeight = bufferedImg.getHeight();
                        ImagesBean lastBean = null;
                        //允许拆分
                        if (imgWidth >= imgHeight) {
                            for (int i = 0; i < list.size(); i++) {
                                File imgUrl = new File(rootpath + list.get(i).getImg());
                                System.out.println("开始拆分第" + (i + 1) + "张:" + list.get(i).getImg());
                                BufferedImage image = null;
                                try {
                                    image = ImageIO.read(imgUrl);
                                } catch (Exception e) {
                                    System.out.print("图片读取失败！" + imgUrl);
                                }

                                if (image.getWidth() >= image.getHeight()) {
                                    int rows = 1;
                                    int cols = 2;
                                    int chunks = rows * cols;
                                    int chunkWidth = image.getWidth() / cols;
                                    int chunkHeight = image.getHeight() / rows;
                                    int count = 0;
                                    BufferedImage imgs[] = new BufferedImage[chunks];
                                    for (int x = 0; x < rows; x++) {
                                        for (int y = 0; y < cols; y++) {
                                            //设置小图的大小和类型
                                            imgs[count] = new BufferedImage(chunkWidth,
                                                    chunkHeight, image.getType());
                                            //写入图像内容
                                            Graphics2D gr = imgs[count++].createGraphics();
                                            gr.drawImage(image, 0, 0,
                                                    chunkWidth, chunkHeight,
                                                    chunkWidth * y, chunkHeight * x,
                                                    chunkWidth * y + chunkWidth,
                                                    chunkHeight * x + chunkHeight, null);
                                            gr.dispose();
                                        }
                                    }
                                    String imgpathnew = null;
                                    if (imgUrl.getPath().indexOf("/") != -1) {
                                        imgpathnew = StringUtils.substringBeforeLast(imgUrl.getPath(), "/");
                                    } else {
                                        imgpathnew = StringUtils.substringBeforeLast(imgUrl.getPath(), "\\");
                                    }
                                    // 输出小图
                                    for (int ii = 0; ii < imgs.length; ii++) {
                                        String imgName = "img" + (i + 1) + "_sp" + (ii + 1) + ".jpg";
                                        String srcPath = imgpathnew + "/" + imgName;
                                        System.out.println(srcPath);
                                        File imgfile = new File(srcPath);
                                        ImageIO.write(imgs[ii], "jpg", new File(srcPath));
                                        ImageHelper.getTransferImageByScale(srcPath, "small", 0.5); // 将图片的尺寸大小缩小一半
                                        ImagesBean bean = new ImagesBean();
                                        bean.setPage(list_sift.size() + 1);
                                        bean.setImg(imgpath + "/" + imgName);
                                        bean.setImg_small(imgpath + "/img" + (i + 1) + "_sp" + (ii + 1) + "_small.jpg");
                                        if (i == 0 && ii == 0) {
                                            System.out.println("封面只保留一半");
                                            lastBean = bean;
                                        } else {
                                            list_sift.add(bean);
                                        }
                                    }
                                } else {
                                    System.out.println("不用拆分");
                                    ImagesBean bean = new ImagesBean();
                                    bean.setPage(list_sift.size() + 1);
                                    bean.setImg(list.get(i).getImg());
                                    bean.setImg_small(list.get(i).getImg_small());
                                    list_sift.add(bean);
                                }
                            }

                            //判断是否有最后一张图
                            if (list_sift.size() % 2 != 0 && lastBean != null) {
                                list_sift.add(lastBean);
                                System.out.println("补全期刊底面图");
                            }
                        }
                    }
                    doc.close();
                } else {
                    System.out.println("文件格式异常：" + fileForm.getUrl());
                    list_sift.clear();
                    list.clear();
                }
            } else {
                System.out.println("文件格式异常：" + fileForm.getUrl());
                list_sift.clear();
                list.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            list_sift.clear();
            list.clear();
            System.out.println("文件格式异常：" + fileForm.getUrl() + "///" + e.getMessage());
        }
        if (list_sift.size() > 0) {
            return list_sift;
        }
        return list;
    }


}
