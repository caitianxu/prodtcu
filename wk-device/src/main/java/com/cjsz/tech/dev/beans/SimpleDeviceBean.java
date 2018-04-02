package com.cjsz.tech.dev.beans;

import java.io.Serializable;

/**
 * Created by shiaihua on 16/12/24.
 */
public class SimpleDeviceBean implements Serializable{

    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
