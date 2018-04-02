package com.cjsz.tech.book.beans;

/**
 * 图书基本信息
 * Created by LuoLi on 2017/4/21 0021.
 */
public class BookBaseInfo {

    private Long book_id;           //图书ID

    private String book_name;       //书名

    private String book_author;     //作者

    private String book_cover;      //图书封面

    private String book_cover_small;      //图书封面压缩的

    private String book_url;

    private String schedule;    //阅读进度

    private Double price;

    private Double discount_price;

    private Long balance;

    public Double getPrice() {
        if (discount_price != null){
            return discount_price;
        }else{
            return price;
        }
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(Double discount_price) {
        this.discount_price = discount_price;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getBook_url() {
        return book_url;
    }

    public void setBook_url(String book_url) {
        this.book_url = book_url;
    }

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

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    public String getBook_cover() {
        return book_cover;
    }

    public void setBook_cover(String book_cover) {
        this.book_cover = book_cover;
    }

    public String getBook_cover_small() {
        return book_cover_small;
    }

    public void setBook_cover_small(String book_cover_small) {
        this.book_cover_small = book_cover_small;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}
