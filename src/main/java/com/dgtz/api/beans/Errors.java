package com.dgtz.api.beans;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 9/21/14
 */
public class Errors implements Serializable {
    private static final long serialVersionUID = 1L;

    @Expose
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
