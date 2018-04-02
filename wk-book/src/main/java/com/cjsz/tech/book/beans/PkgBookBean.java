package com.cjsz.tech.book.beans;

import java.util.List;

/**
 * Created by LuoLi on 2017/4/25 0025.
 */
public class PkgBookBean {

    private Long pkg_id;

    private String pkg_name;

    private Long[] book_id;

    private List<PkgBook> books;

    public Long getPkg_id() {
        return pkg_id;
    }

    public void setPkg_id(Long pkg_id) {
        this.pkg_id = pkg_id;
    }

    public String getPkg_name() {
        return pkg_name;
    }

    public void setPkg_name(String pkg_name) {
        this.pkg_name = pkg_name;
    }

    public Long[] getBook_id() {
        return book_id;
    }

    public void setBook_id(Long[] book_id) {
        this.book_id = book_id;
    }

    public List<PkgBook> getBooks() {
        return books;
    }

    public void setBooks(List<PkgBook> books) {
        this.books = books;
    }

    public class PkgBook{
        private Long book_cat_id;

        private Long book_id;

        public Long getBook_cat_id() {
            return book_cat_id;
        }

        public void setBook_cat_id(Long book_cat_id) {
            this.book_cat_id = book_cat_id;
        }

        public Long getBook_id() {
            return book_id;
        }

        public void setBook_id(Long book_id) {
            this.book_id = book_id;
        }
    }
}
