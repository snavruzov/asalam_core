package com.dgtz.api.beans;

import com.google.gson.annotations.Expose;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 9/22/14
 */
public class Result {

    @Expose
    private long mediaid;
    @Expose
    private String status;


    public long getMediaid() {
        return mediaid;
    }

    public void setMediaid(long mediaid) {
        this.mediaid = mediaid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
