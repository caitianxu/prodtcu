package com.cjsz.tech.core.page;

import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 *  该类主要用于对分页的数据进行处理
 * @param <T>
 */
public class PageList<T> implements Serializable {
  private static final long serialVersionUID = 1L;
  //当前页
  private int pageNum;
  //每页的数量
  private int pageSize;
  //当前页的数量
  private int size;
  //总记录数
  private long total;
  //总页数
  private int pages;
  //结果集
  private List<T> rows;

  /**
   * 包装Page对象
   * --未用
   * @param list page结果
   */
  public PageList(List list, BaseWrapper assembler) {
    if (list instanceof Page) {
      Page page = (Page) list;
      this.pageNum = page.getPageNum();
      this.pageSize = page.getPageSize();

      this.pages = page.getPages();
      if(assembler != null){
        this.rows = assembler.toDTOList(page);
      }else {
        this.rows = page;
      }
      this.size = page.size();
      this.total = page.getTotal();
    } else if (list instanceof Collection) {
      this.pageNum = 1;
      this.pageSize = list.size();

      this.pages = 1;
      if(assembler != null){
        this.rows = assembler.toDTOList(list);
      }else {
        this.rows = list;
      }
      this.size = list.size();
      this.total = list.size();
    }
  }

  public int getPageNum() {
    return pageNum;
  }

  public int getPageSize() {
    return pageSize;
  }

  public int getSize() {
    return size;
  }

  public long getTotal() {
    return total;
  }

  public int getPages() {
    return pages;
  }

  public List<T> getRows() {
    return rows;
  }

  public void setPageNum(int pageNum) {
    this.pageNum = pageNum;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public void setPages(int pages) {
    this.pages = pages;
  }
}
