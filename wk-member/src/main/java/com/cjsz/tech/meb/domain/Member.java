package com.cjsz.tech.meb.domain;


import javax.persistence.*;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 *  会员
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "member")
public class Member implements Serializable {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    member_id;
    private String  member_name;      //会员帐号
    private String  member_real_name; //真实姓名
    private String  member_pwd;       //密码
    private String  nick_name;			//昵称
    private String  source;			//来源（qq/weixin/weibo）
	private String openid;
    private Long    org_id;			//机构id（没有来源的统一归到长江科技）
    private String  sex;            //性别
    @JSONField(format = "yyyy-MM-dd")
    private Date    birth;          //会员生日
    private String  icon;           //用户头像
    private String  email;          //用户邮箱
    private String  phone;          //联系电话
    private String  address;        //联系地址
    private String  identity_card;        //身份证
    private Integer enabled;        //1: 启用  2: 停用
    private Integer is_delete;      //是否删除（1：是  2：否）

	@JSONField(format = "yyyy-MM-dd")
	private Date    update_time;    //修改时间
	@JSONField(format = "yyyy-MM-dd")
	private Date    create_time;    //创建时间

	private Integer is_sys_icon;//是否使用系统头像(1:是;2:否)
	private Long grade_point;//会员积分
	private Long grade_id;//会员等级Id
	private Long next_grade_point;//会员下一级积分

	@Transient
	private Integer grade_name;//会员等级
	@Transient
	private String grade_title;//会员头衔
	@Transient
	private String member_header;//会员头像
	@Transient
	private String  token;


	public Long getMember_id() {
		return member_id;
	}
	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
	public String getMember_real_name() {
		return member_real_name;
	}
	public void setMember_real_name(String member_real_name) {
		this.member_real_name = member_real_name;
	}
	public String getMember_pwd() {
		return member_pwd;
	}
	public void setMember_pwd(String member_pwd) {
		this.member_pwd = member_pwd;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public Long getOrg_id() {
		return org_id;
	}
	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIdentity_card() {
		return identity_card;
	}
	public void setIdentity_card(String identity_card) {
		this.identity_card = identity_card;
	}
	public Integer getEnabled() {
		return enabled;
	}
	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}
	public Integer getIs_delete() {
		return is_delete;
	}
	public void setIs_delete(Integer is_delete) {
		this.is_delete = is_delete;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
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
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public Integer getIs_sys_icon() {
		return is_sys_icon;
	}

	public void setIs_sys_icon(Integer is_sys_icon) {
		this.is_sys_icon = is_sys_icon;
	}

	public Long getGrade_point() {
		return grade_point;
	}

	public void setGrade_point(Long grade_point) {
		this.grade_point = grade_point;
	}

	public Long getGrade_id() {
		return grade_id;
	}

	public void setGrade_id(Long grade_id) {
		this.grade_id = grade_id;
	}

	public Long getNext_grade_point() {
		return next_grade_point;
	}

	public void setNext_grade_point(Long next_grade_point) {
		this.next_grade_point = next_grade_point;
	}

	public Integer getGrade_name() {
		return grade_name;
	}

	public void setGrade_name(Integer grade_name) {
		this.grade_name = grade_name;
	}

	public String getGrade_title() {
		return grade_title;
	}

	public void setGrade_title(String grade_title) {
		this.grade_title = grade_title;
	}

	public String getMember_header() {
		return member_header;
	}

	public void setMember_header(String member_header) {
		this.member_header = member_header;
	}
}
