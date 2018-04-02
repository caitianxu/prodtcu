package com.cjsz.tech.system.beans;


import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/25.
 */
public class DelAdvBean implements Serializable {
	
	private Long[]  adv_ids;

	public Long[] getAdv_ids() {
		return adv_ids;
	}

	public void setAdv_ids(Long[] adv_ids) {
		this.adv_ids = adv_ids;
	}
}
