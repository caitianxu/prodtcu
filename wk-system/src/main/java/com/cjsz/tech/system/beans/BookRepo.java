package com.cjsz.tech.system.beans;

import com.cjsz.tech.beans.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by yunke on 16/3/3.
 * 图书库
 */
@Table(name = "bookitem_repo")
public class BookRepo extends BaseEntity implements Serializable {

    @Id
    @Column(name = "bk_id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bk_id;

    /**
     * 图书名称
     */
    private String bk_name;

    /**
     * 图书所属分类编码
     */
    private Long bk_cat_id;

    /**
     * 图书所属分类
     */
    private String bk_cat_name;

    /**
     *国际标准书号
     */
    private String bk_isbn;
    

    /**
     * 创建日期
     */
    private Date create_time;

    /**
     * 图片地址
     */
    private String img_url;
    
    /**
     * 图书路径
     */
    private String bk_url;
    
    /**
     * 图书简介
     */
    private String bk_intro;
    /**
     * 出版日期
     */
    private String bk_publishdate;

    /**
     * 作者
     */
    private String bk_author;

    /**
     * 图书格式
     */
    private String bk_fmt;

    /**
     * 出版社
     */
    private String bk_publisher;

    /**
     * 图书原始ID
     */
    private Integer bk_origid;
    /**
     * 图书原始分类编码
     */
    private Integer bk_origcatid;

    /**
     * 图书原始分类名称
     */
    private String bk_origcatname;
    
    private Integer recomment =0;

    private Integer enabled = 0;
    
    private Long topnum;

    private Integer hot =0;
    
    @Transient
    private Long order_weight;
    
    @Transient
    private Long bk_filesize;
    
    @Transient
    private String bk_coversave;
    
    @Transient
    private String bk_contentsave;
    
    @Transient
    private Integer bk_downstatus;
    
    

    public Long getBk_id() {
        return bk_id;
    }

    public void setBk_id(Long bk_id) {
        this.bk_id = bk_id;
    }

    public String getBk_name() {
        return bk_name;
    }

    public void setBk_name(String bk_name) {
        this.bk_name = bk_name;
    }

    public Long getBk_cat_id() {
        return bk_cat_id;
    }

    public void setBk_cat_id(Long bk_cat_id) {
        this.bk_cat_id = bk_cat_id;
    }

    public String getBk_cat_name() {
        return bk_cat_name;
    }

    public void setBk_cat_name(String bk_cat_name) {
        this.bk_cat_name = bk_cat_name;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getBk_author() {
        return bk_author;
    }

    public void setBk_author(String bk_author) {
        this.bk_author = bk_author;
    }

    public String getBk_fmt() {
        return bk_fmt;
    }

    public void setBk_fmt(String bk_fmt) {
        this.bk_fmt = bk_fmt;
    }

    public String getBk_publisher() {
        return bk_publisher;
    }

    public void setBk_publisher(String bk_publisher) {
        this.bk_publisher = bk_publisher;
    }

    public Integer getBk_origid() {
        return bk_origid;
    }

    public void setBk_origid(Integer bk_origid) {
        this.bk_origid = bk_origid;
    }

    public Integer getBk_origcatid() {
        return bk_origcatid;
    }

    public void setBk_origcatid(Integer bk_origcatid) {
        this.bk_origcatid = bk_origcatid;
    }

    public String getBk_origcatname() {
        return bk_origcatname;
    }

    public void setBk_origcatname(String bk_origcatname) {
        this.bk_origcatname = bk_origcatname;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getBk_isbn() {
        return bk_isbn;
    }

    public void setBk_isbn(String bk_isbn) {
        this.bk_isbn = bk_isbn;
    }

	public Integer getRecomment() {
		return recomment;
	}

	public void setRecomment(Integer recomment) {
		this.recomment = recomment;
	}

	public Long getTopnum() {
		return topnum;
	}

	public void setTopnum(Long topnum) {
		this.topnum = topnum;
	}


	public String getBk_publishdate() {
		return bk_publishdate;
	}

	public void setBk_publishdate(String bk_publishdate) {
		this.bk_publishdate = bk_publishdate;
	}

	public String getBk_url() {
		return bk_url;
	}

	public void setBk_url(String bk_url) {
		this.bk_url = bk_url;
	}

	public String getBk_intro() {
		return bk_intro;
	}

	public void setBk_intro(String bk_intro) {
		this.bk_intro = bk_intro;
	}

	public Long getBk_filesize() {
		return bk_filesize;
	}

	public void setBk_filesize(Long bk_filesize) {
		this.bk_filesize = bk_filesize;
	}

	public String getBk_coversave() {
		return bk_coversave;
	}

	public void setBk_coversave(String bk_coversave) {
		this.bk_coversave = bk_coversave;
	}

	public String getBk_contentsave() {
		return bk_contentsave;
	}

	public void setBk_contentsave(String bk_contentsave) {
		this.bk_contentsave = bk_contentsave;
	}

	public Integer getBk_downstatus() {
		return bk_downstatus;
	}

	public void setBk_downstatus(Integer bk_downstatus) {
		this.bk_downstatus = bk_downstatus;
	}

	public Long getOrder_weight() {
		return order_weight;
	}

	public void setOrder_weight(Long order_weight) {
		this.order_weight = order_weight;
	}

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }
}
