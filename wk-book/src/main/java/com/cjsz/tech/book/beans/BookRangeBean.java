package com.cjsz.tech.book.beans;

/**
 * 图书仓库图书序号、ISBN
 * Created by Administrator on 2016/12/19 0019.
 */
public class BookRangeBean {

    private String book_num;

    private String book_isbn;

    public BookRangeBean(){}

    public BookRangeBean(String book_num, String book_isbn){
        this.book_num = book_num;
        this.book_isbn = book_isbn;
    }

    public String getBook_num() {
        return book_num;
    }

    public void setBook_num(String book_num) {
        this.book_num = book_num;
    }

    public String getBook_isbn() {
        return book_isbn;
    }

    public void setBook_isbn(String book_isbn) {
        this.book_isbn = book_isbn;
    }
}
