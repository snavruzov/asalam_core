package com.dgtz.api.beans;

import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * BroCast.
 * Copyright: Sardor Navruzov
 * 2013-2017.
 */
public class RatingInfo {
    public String time;
    public List<LikeInfo> data;

    public RatingInfo() {
    }

    public RatingInfo(String time, List<LikeInfo> data) {
        this.time = time;
        this.data = data;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }
}
