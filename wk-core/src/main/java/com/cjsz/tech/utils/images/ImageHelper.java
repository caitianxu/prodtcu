package com.cjsz.tech.utils.images;

/**
 * Created by Administrator on 2016/11/10 0010.
 */
public class ImageHelper {

//    public static void getTransferImageByScale(String srcPath, String addtag, double val) {
//        IThumbnailCreator creator = ThumbnailCreatorFactory.getCreatorByTag(srcPath, addtag);
//        creator.scale(val);
//    }

    public static void getTransferImageByScale(String srcPath, String addtag, double val) {
        ImageCompress imageCompress = new ImageCompress();
        int potPos = srcPath.lastIndexOf('.');
        String type = srcPath.substring(potPos, srcPath.length());
        String targetPath = srcPath.substring(0, potPos) + "_" + addtag + type;
        imageCompress.imageScale(srcPath, targetPath, val);
    }

    /*public static void getTransferImageByResize(String srcPath, String addtag, int w, int h) {
        IThumbnailCreator creator1 = ThumbnailCreatorFactory.getCreatorByTag(srcPath, addtag);
        creator1.resize(w, h);
    }*/

    public static void getTransferImageByResize(String srcPath, String addtag, int w, int h) {
        ImageCompress imageCompress = new ImageCompress();
        int potPos = srcPath.lastIndexOf('.');
        String type = srcPath.substring(potPos, srcPath.length());
        String targetPath = srcPath.substring(0, potPos) + "_" + addtag + type;
        imageCompress.imageScale(srcPath, targetPath, w, h);
    }

    public static void getTransferImageByCut(String srcPath, String addtag, int x, int y, int width, int height) {
        IThumbnailCreator creator1 = ThumbnailCreatorFactory.getCreatorByTag(srcPath, addtag);
        creator1.cut(x, y, width, height);
    }

    public static byte[] getTransferImageByte(String srcPath) {
        IThumbnailCreator creator1 = new JavaImageIOCreator();
        return creator1.getImageByte(srcPath);
    }

    /*public static byte[] getTransferImageByteScale(double scale, byte[] bytes) {
        IThumbnailCreator creator1 = new JavaImageIOCreator();
        return creator1.getImageByteByScale(scale, bytes);
    }*/

	/*public static void main(String args[]){
        getTransferImageByScale("g:/111.png", "scale", 0.5f);
        getTransferImageByScale("g:/111.png", "1scale", 1.0f);
        getTransferImageByResize("g:/111.png", "resize", 180, 180);
        getTransferImageByCut("g:/111.png", "cut", 0, 0, 360, 500);
//        getTransferImageByte("g:/111.png");
        *//*getTransferImageByteScale(0.5f, getTransferImageByte("f:/1.jpg"));
        getFile(getTransferImageByte("f:/1.jpg"), "f:/a.jpg");
        getFile(getTransferImageByteScale(1f, getTransferImageByte("f:/1.jpg")),"f:/b.jpg");*//*


	}*/

    /*public static void getFile(byte[] bytes, String file_path){
        File file = new File(file_path);
        try {
            OutputStream output = new FileOutputStream(file);
            BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
            bufferedOutput.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


}
