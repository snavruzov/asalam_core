package com.dgtz.api.beans;

import java.io.Serializable;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 6/11/14
 */
public class AbuseJson implements Serializable{
    private static final long serialVersionUID = 1L;

    private boolean response;

    public AbuseJson() {
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }
}
