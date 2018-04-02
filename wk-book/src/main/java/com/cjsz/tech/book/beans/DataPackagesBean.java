package com.cjsz.tech.book.beans;

import com.cjsz.tech.book.domain.BookTag;
import com.cjsz.tech.book.domain.DataPackage;

import java.util.List;

/**
 * 数据包集合
 * Created by Administrator on 2016/12/19 0019.
 */
public class DataPackagesBean {

    private List<DataPackage> dataPackages;

    public List<DataPackage> getDataPackages() {
        return dataPackages;
    }

    public void setDataPackages(List<DataPackage> dataPackages) {
        this.dataPackages = dataPackages;
    }
}
