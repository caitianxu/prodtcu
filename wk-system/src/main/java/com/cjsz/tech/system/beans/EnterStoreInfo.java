package com.cjsz.tech.system.beans;

/**
 * Created by shiaihua on 16/7/1.
 */

import javax.persistence.Table;

/**
 * 进店人数店面信息
 * Created by shiaihua on 16/7/1.
 */
@Table(name = "org_intostore_shop")
public class EnterStoreInfo {


    private Long org_id;

    private String shop_id;

    private String shop_name;

    private String shop_kpis;


    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_kpis() {
        return shop_kpis;
    }

    public void setShop_kpis(String shop_kpis) {
        this.shop_kpis = shop_kpis;
    }
}
