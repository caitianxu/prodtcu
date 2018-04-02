package com.cjsz.tech.dev.beans;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shiaihua on 16/12/24.
 */
public class IndexCountNewBean implements Serializable {
	@JSONField(ordinal = 1)
	private Integer member_num;
	@JSONField(ordinal = 2)
	private Integer device_num;
	@JSONField(ordinal = 3)
	private Integer book_num;
	@JSONField(ordinal = 4)
	private Integer news_num;
	@JSONField(ordinal = 5)
	private Integer paper_num;
	@JSONField(ordinal = 6)
	private Integer periodical_num;
	@JSONField(ordinal = 7)
	private Integer video_num;
	@JSONField(ordinal = 8)
	private Integer audio_num;
	@JSONField(ordinal = 9)
	private List<BookCountBean> books;
	@JSONField(ordinal = 10)
	private List<MemberCountBean> members;


	public Integer getMember_num() {
		return member_num;
	}

	public void setMember_num(Integer member_num) {
		this.member_num = member_num;
	}

	public Integer getDevice_num() {
		return device_num;
	}

	public void setDevice_num(Integer device_num) {
		this.device_num = device_num;
	}

	public Integer getBook_num() {
		return book_num;
	}

	public void setBook_num(Integer book_num) {
		this.book_num = book_num;
	}

	public Integer getNews_num() {
		return news_num;
	}

	public void setNews_num(Integer news_num) {
		this.news_num = news_num;
	}

	public Integer getPaper_num() {
		return paper_num;
	}

	public void setPaper_num(Integer paper_num) {
		this.paper_num = paper_num;
	}

	public Integer getPeriodical_num() {
		return periodical_num;
	}

	public void setPeriodical_num(Integer periodical_num) {
		this.periodical_num = periodical_num;
	}

	public Integer getVideo_num() {
		return video_num;
	}

	public void setVideo_num(Integer video_num) {
		this.video_num = video_num;
	}

	public Integer getAudio_num() {
		return audio_num;
	}

	public void setAudio_num(Integer audio_num) {
		this.audio_num = audio_num;
	}

	public List<BookCountBean> getBooks() {
		return books;
	}

	public void setBooks(List<BookCountBean> books) {
		this.books = books;
	}

	public List<MemberCountBean> getMembers() {
		return members;
	}

	public void setMembers(List<MemberCountBean> members) {
		this.members = members;
	}
}
