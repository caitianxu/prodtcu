package com.cjsz.tech.book.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Administrator on 2017/9/6 0006.
 */
public class RecommendBookListBean extends PageConditionBean{

    private Long recommend_cat_id;

    public Long getRecommend_cat_id() {
        return recommend_cat_id;
    }

    public void setRecommend_cat_id(Long recommend_cat_id) {
        this.recommend_cat_id = recommend_cat_id;
    }
}
