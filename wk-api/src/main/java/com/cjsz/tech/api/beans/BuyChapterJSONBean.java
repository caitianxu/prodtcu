package com.cjsz.tech.api.beans;

/**
 * Created by Administrator on 2017/10/20 0020.
 */
public class BuyChapterJSONBean {
    private Long code;
    private BuyChapter data;
    private String msg;

    public class BuyChapter{
        private Integer buyed;  //总共购买章节
        private Integer amount;  //总共花费金额

        public Integer getBuyed() {
            return buyed;
        }

        public void setBuyed(Integer buyed) {
            this.buyed = buyed;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public BuyChapter getData() {
        return data;
    }

    public void setData(BuyChapter data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
