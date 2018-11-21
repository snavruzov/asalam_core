package com.dgtz.api.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sardor Navruzov CEO, DGTZ.
 */
public class GeoLocation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private List<Address> results;
    private String formatted_address;
    private String geometry;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Address> getResults() {
        return results;
    }

    public void setResults(List<Address> results) {
        this.results = results;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }


}
