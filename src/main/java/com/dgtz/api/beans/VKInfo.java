package com.dgtz.api.beans;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sardor on 2/29/16.
 */
public class VKInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<VKResponse> response;

    public VKInfo() {
    }

    public List<VKResponse> getResponse() {
        return response;
    }

    public void setResponse(List<VKResponse> response) {
        this.response = response;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }

}




