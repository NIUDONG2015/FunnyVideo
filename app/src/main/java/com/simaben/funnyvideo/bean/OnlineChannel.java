package com.simaben.funnyvideo.bean;

import java.io.Serializable;

/**
 * Created by simaben on 11/4/16.
 */
public class OnlineChannel implements Serializable {
    private String title;
    private String address;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
