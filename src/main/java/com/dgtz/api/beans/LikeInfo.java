package com.dgtz.api.beans;

import com.google.gson.GsonBuilder;

/**
 * BroCast.
 * Copyright: Sardor Navruzov
 * 2013-2016.
 */
public class LikeInfo {
    public String type;
    public String color;
    public String num;

    public LikeInfo() {
    }

    public LikeInfo(String type, String color, String num) {
        this.type = type;
        this.color = color;
        this.num = num;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }
}
