package com.dgtz.api.beans;

import com.dgtz.db.api.factory.GsonInsta;

import java.io.Serializable;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 4/15/14
 */
public class MediaSolrBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String channel;

    public MediaSolrBean(){

    }

    public MediaSolrBean(Long id, String username, String channel) {
        this.id = id;
        this.username = username;
        this.channel = channel;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return GsonInsta.getInstance().toJson(this);
    }
}
