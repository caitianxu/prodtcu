package com.cjsz.tech.system.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author:Jason
 * Date:2016/11/28
 * 系统文件实体类
 */
@Entity
@Table(name = "sys_file")
public class SysFile  implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long file_id;
    private String file_name;//文件名称
    private Long file_size;//文件大小
    private String file_format;//文件格式
    private Integer file_type;//文件类型(图书，图片，视频，音频，文档，其他，文件夹)
    private String savePath;//文件存储类型
    private Long pid;
    private Date create_time;//文件创建时间
    private Date update_time;//文件最后修改时间
    private byte[] file_cover;//文件封面(供预览模式时使用,压缩之后)
    private Long create_userid;//创建人
    private Long org_id;//所属组织

    private String fullPath;

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public Long getCreate_userid() {
        return create_userid;
    }

    public void setCreate_userid(Long create_userid) {
        this.create_userid = create_userid;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getFile_id() {
        return file_id;
    }

    public void setFile_id(Long file_id) {
        this.file_id = file_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Long getFile_size() {
        return file_size;
    }

    public void setFile_size(Long file_size) {
        this.file_size = file_size;
    }

    public String getFile_format() {
        return file_format;
    }

    public void setFile_format(String file_format) {
        this.file_format = file_format;
    }

    public Integer getFile_type() {
        return file_type;
    }

    public void setFile_type(Integer file_type) {
        this.file_type = file_type;
    }



    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	public byte[] getFile_cover() {
        return file_cover;
    }

    public void setFile_cover(byte[] file_cover) {
        this.file_cover = file_cover;
    }
}
