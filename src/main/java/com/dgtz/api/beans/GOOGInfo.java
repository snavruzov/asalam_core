package com.dgtz.api.beans;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 3/26/14
 */
public class GOOGInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<GOEmails> emails;
    private String id;
    private String displayName;
    private GONames name;
    private GoImage image;

    public GOOGInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<GOEmails> getEmails() {
        return emails;
    }

    public void setEmails(List<GOEmails> emails) {
        this.emails = emails;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public GONames getName() {
        return name;
    }

    public void setName(GONames name) {
        this.name = name;
    }

    public GoImage getImage() {
        return image;
    }

    public void setImage(GoImage image) {
        this.image = image;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }
}
