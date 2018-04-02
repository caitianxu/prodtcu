package com.cjsz.tech.meb.domain;


import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  会员
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "member_pay_record")
public class MemberPayRecord implements Serializable {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    pay_record_id;
    private Long  member_id;      //会员id
    private BigDecimal amount; //金额
    private String  pay_type;       //支付类型
	@JSONField(format = "yyyy-MM-dd")
	private Date    create_time;    //创建时间

    public Long getPay_record_id() {
        return pay_record_id;
    }

    public void setPay_record_id(Long pay_record_id) {
        this.pay_record_id = pay_record_id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
