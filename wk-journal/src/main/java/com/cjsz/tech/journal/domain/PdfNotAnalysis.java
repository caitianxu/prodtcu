package com.cjsz.tech.journal.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 图书无法解析
 * Created by Administrator on 2016/12/19 0019.
 */
@Entity
@Table(name = "pdf_not_analysis")
public class PdfNotAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysis_id;           //图书ID

    private String pdf_url;       //书名

    private String file_name;     //作者

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;       //创建时间

    public Long getAnalysis_id() {
        return analysis_id;
    }

    public void setAnalysis_id(Long analysis_id) {
        this.analysis_id = analysis_id;
    }

    public String getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(String pdf_url) {
        this.pdf_url = pdf_url;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
