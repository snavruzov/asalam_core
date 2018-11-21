package com.dgtz.api.beans;

import com.dgtz.db.api.factory.GsonInsta;

import java.io.Serializable;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 6/8/14
 */
public class SimpleMcParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String key;
    private String value;

    public SimpleMcParam() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return GsonInsta.getInstance().toJson(this);
    }
}
