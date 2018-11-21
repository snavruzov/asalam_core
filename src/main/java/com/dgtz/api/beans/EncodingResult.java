package com.dgtz.api.beans;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 9/21/14
 */
public class EncodingResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @Expose
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
