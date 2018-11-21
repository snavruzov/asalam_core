package com.dgtz.api.beans;

import java.util.List;

/**
 * Created by Sardor Navruzov CEO, DGTZ.
 */
public class Address {

    private List<Components> address_components;

    public List<Components> getAddress_components() {
        return address_components;
    }

    public void setAddress_components(List<Components> address_components) {
        this.address_components = address_components;
    }
}
