package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.cjsz.tech.beans.BaseEntity;

import javax.persistence.*;
import java.util.List;

/**
 * @Title: BookChapter
 * @Description: 目录表
 * @author shiaihua
 * @date 2016-10-24 13:52:09
 * @version V1.0
 *
 */
@Entity
@Table(name = "book_chapter")
@SuppressWarnings("serial")
public class BookChapter extends BaseEntity implements java.io.Serializable {
	/**章节ID*/
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**所属图书*/
	private Long book_id;
	/**章节名称*/
	private String name;
	/**章节层次*/
	private String path;
	/**章节编码*/
	private String code;
	/**章节资源路径*/
	private String url;
    private String purl;
	/**章节起始页*/
	private Long start_page;
	/**章节终止页*/
	private Long end_page;
	/**书籍格式*/
	private String format;
	/**章节父ID*/
	private Long pid;
	/**上级章节名*/
	private String pname;
	/**创建时间*/
	@JSONField(format = "yyyy-MM-dd")
	private java.util.Date create_time;

	@Transient
	private Integer pdf_page;
	@Transient
	private List<BookChapter> child;
	@Transient
	private Integer is_free;

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    /**
	 *方法: 取得java.lang.Long
	 *@return: java.lang.Long  章节ID
	 */
	@Column(name ="ID",nullable=false,precision=10,scale=0)
	public Long getId(){
		return this.id;
	}

	/**
	 *方法: 设置java.lang.Long
	 *@param: java.lang.Long  章节ID
	 */
	public void setId(Long id){
		this.id = id;
	}
	/**
	 *方法: 取得java.lang.Long
	 *@return: java.lang.Long  所属图书
	 */
	@Column(name ="BOOK_ID",nullable=false,precision=10,scale=0)
	public Long getBook_id(){
		return this.book_id;
	}

	/**
	 *方法: 设置java.lang.Long
	 *@param: java.lang.Long  所属图书
	 */
	public void setBook_id(Long book_id){
		this.book_id = book_id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  章节名称
	 */
	@Column(name ="NAME",nullable=true,length=200)
	public String getName(){
		return this.name;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  章节名称
	 */
	public void setName(String name){
		this.name = name;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  章节层次
	 */
	@Column(name ="PATH",nullable=true,length=255)
	public String getPath(){
		return this.path;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  章节层次
	 */
	public void setPath(String path){
		this.path = path;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  章节编码
	 */
	@Column(name ="CODE",nullable=true,length=20)
	public String getCode(){
		return this.code;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  章节编码
	 */
	public void setCode(String code){
		this.code = code;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  章节资源路径
	 */
	@Column(name ="URL",nullable=true,length=255)
	public String getUrl(){
		return this.url;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  章节资源路径
	 */
	public void setUrl(String url){
		this.url = url;
	}
	/**
	 *方法: 取得java.lang.Long
	 *@return: java.lang.Long  章节起始页
	 */
	@Column(name ="START_PAGE",nullable=true,precision=10,scale=0)
	public Long getStart_page(){
		return this.start_page;
	}

	/**
	 *方法: 设置java.lang.Long
	 *@param: java.lang.Long  章节起始页
	 */
	public void setStart_page(Long start_page){
		this.start_page = start_page;
	}
	/**
	 *方法: 取得java.lang.Long
	 *@return: java.lang.Long  章节终止页
	 */
	@Column(name ="END_PAGE",nullable=true,precision=10,scale=0)
	public Long getEnd_page(){
		return this.end_page;
	}

	/**
	 *方法: 设置java.lang.Long
	 *@param: java.lang.Long  章节终止页
	 */
	public void setEnd_page(Long end_page){
		this.end_page = end_page;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  书籍格式
	 */
	@Column(name ="FORMAT",nullable=true,length=20)
	public String getFormat(){
		return this.format;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  书籍格式
	 */
	public void setFormat(String format){
		this.format = format;
	}
	/**
	 *方法: 取得java.lang.Long
	 *@return: java.lang.Long  章节父ID
	 */
	@Column(name ="PID",nullable=true,precision=10,scale=0)
	public Long getPid(){
		return this.pid;
	}

	/**
	 *方法: 设置java.lang.Long
	 *@param: java.lang.Long  章节父ID
	 */
	public void setPid(Long pid){
		this.pid = pid;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  上级章节名
	 */
	@Column(name ="PNAME",nullable=true,length=200)
	public String getPname(){
		return this.pname;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  上级章节名
	 */
	public void setPname(String pname){
		this.pname = pname;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  创建时间
	 */
	@Column(name ="CREATE_TIME",nullable=true)
	public java.util.Date getCreate_time(){
		return this.create_time;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  创建时间
	 */
	public void setCreate_time(java.util.Date create_time){
		this.create_time = create_time;
	}

	public Integer getPdf_page() {
		return pdf_page;
	}

	public void setPdf_page(Integer pdf_page) {
		this.pdf_page = pdf_page;
	}

	public List<BookChapter> getChild() {
		return child;
	}

	public void setChild(List<BookChapter> child) {
		this.child = child;
	}

	public Integer getIs_free() {
		return is_free;
	}

	public void setIs_free(Integer is_free) {
		this.is_free = is_free;
	}
}
