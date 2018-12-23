package com.project.jaijite.entity;

import java.io.Serializable;

public class Light implements Serializable {
    private static final long serialVersionUID = 1L;
    private int imageId;
    private String name;
    private String price;
    private String url;

    public Light() {
    }

    public Light(int paramInt, String paramString1, String paramString2, String paramString3) {
        this.imageId = paramInt;
        this.name = paramString1;
        this.price = paramString2;
        this.url = paramString3;
    }

    public int getImageId() {
        return this.imageId;
    }

    public String getName() {
        return this.name;
    }

    public String getPrice() {
        return this.price;
    }

    public String getUrl() {
        return this.url;
    }

    public void setImageId(int paramInt) {
        this.imageId = paramInt;
    }

    public void setName(String paramString) {
        this.name = paramString;
    }

    public void setPrice(String paramString) {
        this.price = paramString;
    }

    public void setUrl(String paramString) {
        this.url = paramString;
    }
}
