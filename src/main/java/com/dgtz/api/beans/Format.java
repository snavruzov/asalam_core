package com.dgtz.api.beans;

import com.google.gson.annotations.Expose;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 9/22/14
 */
public class Format {

    @Expose
    private long taskid;
    @Expose
    private String status;


    public long getTaskid() {
        return taskid;
    }

    public void setTaskid(long taskid) {
        this.taskid = taskid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
