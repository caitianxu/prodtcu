package com.cjsz.tech.book.beans;

import com.cjsz.tech.book.domain.DataPackage;

/**
 * 复制数据包
 * Created by Administrator on 2016/12/19 0019.
 */
public class CopyPkgBean {

    private String pkg_name;    //新的数据包名

    private DataPackage dataPackage;        //复制数据包

    public String getPkg_name() {
        return pkg_name;
    }

    public void setPkg_name(String pkg_name) {
        this.pkg_name = pkg_name;
    }

    public DataPackage getDataPackage() {
        return dataPackage;
    }

    public void setDataPackage(DataPackage dataPackage) {
        this.dataPackage = dataPackage;
    }
}
