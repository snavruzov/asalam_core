package com.dgtz.api.beans;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by root on 1/23/14.
 */
public class UserActivities implements Serializable {

    private static final long serialVersionUID = 1L;

    private int rated;
    private long flagged;
    private long followed;
    private long numberOfFollowers;

    public UserActivities() {
    }

    public long getNumberOfFollowers() {
        return numberOfFollowers;
    }

    public void setNumberOfFollowers(long numberOfFollowers) {
        this.numberOfFollowers = numberOfFollowers;
    }

    public int getRated() {
        return rated;
    }

    public void setRated(int rated) {
        this.rated = rated;
    }

    public long getFlagged() {
        return flagged;
    }

    public void setFlagged(long flagged) {
        this.flagged = flagged;
    }

    public long getFollowed() {
        return followed;
    }

    public void setFollowed(long followed) {
        this.followed = followed;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }
}
