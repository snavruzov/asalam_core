package com.dgtz.api.beans;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 9/21/14
 */
public class EncodingResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Expose
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
