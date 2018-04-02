package com.cjsz.tech.utils.zip;


import com.cjsz.tech.core.SpringContextUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * 通过Java的Zip输入输出流实现压缩和解压文件
 * <p/>
 * Created by shiaihua on 16/10/19.
 */
public final class ZipUtil {

    private ZipUtil() {
        // empty
    }


    /**
     * 压缩文件
     *
     * @param filePath 待压缩的文件路径
     * @param extname
     * @return 压缩后的文件
     */
    public static File zip(String filePath, String extname) {
        File target = null;
        File source = new File(filePath);
        if (source.exists()) {
            // 压缩文件名=源文件名.zip
            String zipName = source.getName() + extname;
            target = new File(source.getParent(), zipName);
            if (target.exists()) {
                target.delete(); // 删除旧的文件
            }
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            try {
                fos = new FileOutputStream(target);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                // 添加对应的文件Entry
                addEntry("/", source, zos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtil.closeQuietly(zos, fos);
            }
        }
        return target;
    }

    /**
     * 压缩文件
     *
     * @param filePath 待压缩的文件路径
     * @param extname
     * @return 压缩后的文件
     */
    public static File zipDirectory(String filePath, String extname) {
        File target = null;
        File source = new File(filePath);
        if (source.exists()) {
            if (!source.isDirectory()) {
                throw new RuntimeException("file is not a directory!");
            }
            // 压缩文件名=源文件名.zip
            String zipName = source.getName() + extname;
            target = new File(source.getParent(), zipName);
            if (target.exists()) {
                target.delete(); // 删除旧的文件
            }
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            try {
                fos = new FileOutputStream(target);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                File[] fileList = source.listFiles();
                for (int fi = 0; fi < fileList.length; fi++) {
                    // 添加对应的文件Entry
                    addEntry("", fileList[fi], zos);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtil.closeQuietly(zos, fos);
            }
        }
        return target;
    }


    /**
     * 扫描添加文件Entry
     *
     * @param base   基路径
     * @param source 源文件
     * @param zos    Zip文件输出流
     * @throws IOException
     */
    private static void addEntry(String base, File source, ZipOutputStream zos)
            throws IOException {
        // 按目录分级，形如：/aaa/bbb.txt
        String entry = base + source.getName();
        if (source.isDirectory()) {
            for (File file : source.listFiles()) {
                // 递归列出目录下的所有文件，添加文件Entry
                addEntry(entry + "/", file, zos);
            }
        } else {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                byte[] buffer = new byte[1024 * 10];
                fis = new FileInputStream(source);
                bis = new BufferedInputStream(fis, buffer.length);
                int read = 0;
                zos.putNextEntry(new ZipEntry(entry));
//                if(source.getName().equals("mimetype")) {
//                    zos.setMethod(0);
//                }
                while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
                    zos.write(buffer, 0, read);
                }
                zos.closeEntry();
            } finally {
                IOUtil.closeQuietly(bis, fis);
            }
        }
    }

    /**
     * 解压文件(资讯模板)
     *
     * @param filePath 压缩文件路径
     */
    public static void unzip(String filePath, String tempdir) {
        try {
            File source = new File(filePath);
            BufferedOutputStream bos = null;
            ZipFile zipFile = new ZipFile(source, ZipFile.OPEN_READ);
            Enumeration<?> zipFileEntries = zipFile.entries();
            String parentdir = tempdir;
            while (zipFileEntries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File target = new File(parentdir, currentEntry);
                if (!target.getParentFile().exists()) {
                    // 创建文件父目录
                    target.getParentFile().mkdirs();
                }
                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
                    // 写入文件
                    bos = new BufferedOutputStream(new FileOutputStream(target));
                    int read = 0;
                    byte[] buffer = new byte[1024 * 10];
                    while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                        bos.write(buffer, 0, read);
                    }
                    bos.flush();
                    bos.close();
                    is.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void unzipEpub(String inputZip, String destinationDirectory) {
        try {
            destinationDirectory = destinationDirectory.trim();
            int BUFFER = 10240;
            List<String> zipFiles = new ArrayList<String>();
            File sourceZipFile = new File(inputZip);
            File unzipDestinationDirectory = new File(destinationDirectory);
            unzipDestinationDirectory.mkdir();


            ZipFile zipFile;
            // Open Zip file for reading
            zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
            // Create an enumeration of the entries in the zip file
            Enumeration<?> zipFileEntries = zipFile.entries();
            // Process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();


                File destFile = new File(unzipDestinationDirectory, currentEntry);
                if (currentEntry.endsWith(".epub")) {
                    zipFiles.add(destFile.getAbsolutePath());
                }
                // grab file's parent directory structure
                File destinationParent = destFile.getParentFile();
                // create the parent directory structure if needed
                destinationParent.mkdirs();

                // extract file if not a directory
                if (!entry.isDirectory()) {
                    BufferedInputStream is =
                            new BufferedInputStream(zipFile.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];
                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest =
                            new BufferedOutputStream(fos, BUFFER);
                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }

                    dest.flush();
                    dest.close();
                    is.close();
                }

            }
            zipFile.close();
            for (Iterator<String> iter = zipFiles.iterator(); iter.hasNext(); ) {
                String zipName = iter.next();
                ZipUtil.unzipEpub(zipName, destinationDirectory + File.separatorChar +
                        zipName.substring(0, zipName.lastIndexOf(".epub")));
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(inputZip + "===" + e.getMessage());
        }
    }
//    public static void main(String[] args) {
//        ZipUtil.unzip("E:\\model1.zip");
//        ZipUtil.unzip("E:\\model2.zip");
//
//    }

}