package com.cjsz.tech.utils.images;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.imageio.plugins.common.ImageUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/2/21 0021.
 */


public class ImageCompress {

    /**

     * Logger for this class

     */

    private static final Logger logger = Logger.getLogger(ImageUtil.class);

    public void imageScale(String srcFilePath, String targetFilePath,

                           int width, int height) {

        this.imageScale(new File(srcFilePath), new File(targetFilePath), width,

                height);

    }

    public void imageScale(String srcFilePath, String targetFilePath,

                           double val) {

        this.imageScale(new File(srcFilePath), new File(targetFilePath), val);

    }



    public void imageScale(File srcFile, File targetFile, int width, int height) {

        try {

            Image image = javax.imageio.ImageIO.read(srcFile);


            image = image.getScaledInstance(width, height,
                    Image.SCALE_AREA_AVERAGING);
            // Make a BufferedImage from the Image.
            BufferedImage mBufferedImage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = mBufferedImage.createGraphics();

            g2.drawImage(image, 0, 0, width, height, Color.white, null);
            g2.dispose();

            float[] kernelData2 = { -0.125f, -0.125f, -0.125f, -0.125f, 2,
                    -0.125f, -0.125f, -0.125f, -0.125f };
            Kernel kernel = new Kernel(3, 3, kernelData2);
            ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
            mBufferedImage = cOp.filter(mBufferedImage, null);

            File targetDir = targetFile.getParentFile();
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            FileOutputStream out = new FileOutputStream(targetFile);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(mBufferedImage);
            out.close();
        } catch (Exception e) {
            logger.error(
                    "imageScale(String, String, int, int) - 图片压缩出错 - srcFilePath="
                            + srcFile.getPath() + ", targeFilePath="
                            + targetFile.getPath() + ", width=" + width
                            + ", height=" + height, e);
        }
    }

    public void imageScale(File srcFile, File targetFile, double val) {
        try {

            Image image = javax.imageio.ImageIO.read(srcFile);

            int width = (int)(((BufferedImage) image).getWidth()*val + 0.5);
            int height = (int)(((BufferedImage) image).getHeight()*val + 0.5);
            image = image.getScaledInstance(width, height,
                    Image.SCALE_AREA_AVERAGING);
            // Make a BufferedImage from the Image.
            BufferedImage mBufferedImage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = mBufferedImage.createGraphics();

            g2.drawImage(image, 0, 0, width, height, Color.white, null);
            g2.dispose();

            float[] kernelData2 = { -0.125f, -0.125f, -0.125f, -0.125f, 2,
                    -0.125f, -0.125f, -0.125f, -0.125f };
            Kernel kernel = new Kernel(3, 3, kernelData2);
            ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
            mBufferedImage = cOp.filter(mBufferedImage, null);

            File targetDir = targetFile.getParentFile();
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            FileOutputStream out = new FileOutputStream(targetFile);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(mBufferedImage);
            out.close();
        } catch (Exception e) {
            logger.error(
                    "imageScale(String, String, double) - 图片压缩出错 - srcFilePath="
                            + srcFile.getPath() + ", targeFilePath="
                            + targetFile.getPath() + ", val=" + val, e);
        }
    }









    public void imageScale(FileItem fileItem, String targetFilePath, int width,
                           int height) {
        try {
            InputStream input = fileItem.getInputStream();
            Image image = javax.imageio.ImageIO.read(input);

            image = image.getScaledInstance(width, height,
                    Image.SCALE_AREA_AVERAGING);
            BufferedImage mBufferedImage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = mBufferedImage.createGraphics();

            g2.drawImage(image, 0, 0, width, height, Color.white, null);
            g2.dispose();

            float[] kernelData2 = { -0.125f, -0.125f, -0.125f, -0.125f, 2,
                    -0.125f, -0.125f, -0.125f, -0.125f };
            Kernel kernel = new Kernel(3, 3, kernelData2);
            ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
            mBufferedImage = cOp.filter(mBufferedImage, null);

            File target = new File(targetFilePath);
            File targetDir = target.getParentFile();
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            FileOutputStream out = new FileOutputStream(targetFilePath);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(mBufferedImage);
            out.close();
            input.close();
        } catch (Exception e) {
            logger.error(
                    "imageScale(String, String, int, int) - 图片压缩出错 - fileItem="
                            + fileItem.getName() + ", targetFilePath="
                            + targetFilePath + ", width=" + width + ", height="
                            + height, e);
        }
    }


    /*public static void main(String[] args) {

        ImageResize iu=new ImageResize();

        iu.imageScale("g:/9787208108417.jpg", "g:/9787208108417_1.jpg", 600, 800);

    }*/

}
