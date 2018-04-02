package com.cjsz.tech.system.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 模板
 * Created by Administrator on 2017/1/22 0022.
 */
@Entity
@Table(name = "sys_temple")
public class SysTemple implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long temple_id;       //广告分类Id

    private String temple_name;       //项目code

    private String temple_remark;        //广告位code

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;       //创建时间

    public Long getTemple_id() {
        return temple_id;
    }

    public void setTemple_id(Long temple_id) {
        this.temple_id = temple_id;
    }

    public String getTemple_name() {
        return temple_name;
    }

    public void setTemple_name(String temple_name) {
        this.temple_name = temple_name;
    }

    public String getTemple_remark() {
        return temple_remark;
    }

    public void setTemple_remark(String temple_remark) {
        this.temple_remark = temple_remark;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
