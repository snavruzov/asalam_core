package com.dgtz.api.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 9/22/14
 */

@XmlRootElement
@XmlType(propOrder = {"message", "mediaID", "errors"})
public class Response {

    private String message;
    private long mediaID;
    private String errors;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @XmlElement(name = "MediaID")
    public long getMediaID() {
        return mediaID;
    }

    public void setMediaID(long mediaID) {
        this.mediaID = mediaID;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
