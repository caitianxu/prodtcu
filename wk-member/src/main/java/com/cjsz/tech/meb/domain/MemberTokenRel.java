package com.cjsz.tech.meb.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 会员token
 * Created by LuoLi on 2017/4/26 0026.
 */
@Entity
@Table(name = "member_token_rel")
public class MemberTokenRel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rel_id;

    private Long member_id;

    private String token;

    private String token_type;

    public MemberTokenRel(){}

    public MemberTokenRel(Long member_id, String token, String token_type){
        this.member_id = member_id;
        this.token = token;
        this.token_type = token_type;
    }

    public Long getRel_id() {
        return rel_id;
    }

    public void setRel_id(Long rel_id) {
        this.rel_id = rel_id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
}
