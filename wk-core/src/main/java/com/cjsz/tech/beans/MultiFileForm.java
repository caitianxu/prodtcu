package com.cjsz.tech.beans;

import java.util.List;

public class MultiFileForm {

	private List<FileForm> data;

	private Integer create_userid;

	public List<FileForm> getData() {
		return data;
	}

	public void setData(List<FileForm> data) {
		this.data = data;
	}

	public Integer getCreate_userid() {
		return create_userid;
	}

	public void setCreate_userid(Integer create_userid) {
		this.create_userid = create_userid;
	}
}
