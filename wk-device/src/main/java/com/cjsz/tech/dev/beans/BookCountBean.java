package com.cjsz.tech.dev.beans;

import java.io.Serializable;

/**
 * Created by shiaihua on 16/12/24.
 */
public class BookCountBean implements Serializable{

    private Long book_id;
    
    private String book_name;
    
    private Long count;

	public Long getBook_id() {
		return book_id;
	}

	public void setBook_id(Long book_id) {
		this.book_id = book_id;
	}

	public String getBook_name() {
		return book_name;
	}

	public void setBook_name(String book_name) {
		this.book_name = book_name;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
