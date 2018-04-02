package com.cjsz.tech.dev.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shiaihua on 16/12/24.
 */
public class IndexCountBean implements Serializable{

    private Integer member_num;
    
    private Integer device_num;
    
    private Integer book_num;
    
    private Integer news_num;
    
    private Integer paper_num;
    
    private Integer journal_num;
    
    private Integer video_num;
    
    private Integer audio_num;
    
    private List<ResCountBean> a_one;
    
    private List<ResCountBean> b_two;
    
    private List<ResCountBean> c_thr;
    
    private List<ResCountBean> d_four;
    
    private List<ResCountBean> e_five;
    
    private List<ResCountBean> f_six;
    
    private List<ResCountBean> g_sev;

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

	public Integer getJournal_num() {
		return journal_num;
	}

	public void setJournal_num(Integer journal_num) {
		this.journal_num = journal_num;
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

	public List<ResCountBean> getA_one() {
		return a_one;
	}

	public void setA_one(List<ResCountBean> a_one) {
		this.a_one = a_one;
	}

	public List<ResCountBean> getB_two() {
		return b_two;
	}

	public void setB_two(List<ResCountBean> b_two) {
		this.b_two = b_two;
	}

	public List<ResCountBean> getC_thr() {
		return c_thr;
	}

	public void setC_thr(List<ResCountBean> c_thr) {
		this.c_thr = c_thr;
	}

	public List<ResCountBean> getD_four() {
		return d_four;
	}

	public void setD_four(List<ResCountBean> d_four) {
		this.d_four = d_four;
	}

	public List<ResCountBean> getE_five() {
		return e_five;
	}

	public void setE_five(List<ResCountBean> e_five) {
		this.e_five = e_five;
	}

	public List<ResCountBean> getF_six() {
		return f_six;
	}

	public void setF_six(List<ResCountBean> f_six) {
		this.f_six = f_six;
	}

	public List<ResCountBean> getG_sev() {
		return g_sev;
	}

	public void setG_sev(List<ResCountBean> g_sev) {
		this.g_sev = g_sev;
	}
}
