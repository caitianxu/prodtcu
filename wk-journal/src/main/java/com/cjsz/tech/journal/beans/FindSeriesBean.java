package com.cjsz.tech.journal.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/25 0025.
 */
public class FindSeriesBean implements Serializable {

    private String series_name; //期刊系列名称

    private Long org_id;    //机构ID

    private Long periodical_id;   //期刊id


    public Long getPeriodical_id() {
        return periodical_id;
    }

    public void setPeriodical_id(Long periodical_id) {
        this.periodical_id = periodical_id;
    }

    public String getSeries_name() {
        return series_name;
    }

    public void setSeries_name(String series_name) {
        this.series_name = series_name;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }
}
