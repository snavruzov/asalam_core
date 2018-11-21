package com.dgtz.api.beans;

import com.brocast.riak.api.beans.LiveProps;
import com.brocast.riak.api.beans.MediaStatistics;
import com.dgtz.db.api.beans.DcDebateEntity;
import com.dgtz.db.api.beans.ScreenRotation;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Set;

/**
 * Created by sardor on 3/18/17.
 */
public class MediaFeaturedBean {
    public Long idMedia;
    public String title;
    public Integer duration;
    public String dateadded;
    public Long idUser;
    public String location;
    public String thumb;
    public LiveProps liveProps;
    public String username;
    public MediaStatistics stats;
    public String avatar;
    public String ratio = "4:3";
    public String method = "";
    public List<DcDebateEntity> debaters;
    public List<ScreenRotation> rotations;

    public Long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(Long idMedia) {
        this.idMedia = idMedia;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDateadded() {
        return dateadded;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public LiveProps getLiveProps() {
        return liveProps;
    }

    public void setLiveProps(LiveProps liveProps) {
        this.liveProps = liveProps;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MediaStatistics getStats() {
        return stats;
    }

    public void setStats(MediaStatistics stats) {
        this.stats = stats;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<DcDebateEntity> getDebaters() {
        return debaters;
    }

    public void setDebaters(List<DcDebateEntity> debaters) {
        this.debaters = debaters;
    }

    public List<ScreenRotation> getRotations() {
        return rotations;
    }

    public void setRotations(List<ScreenRotation> rotations) {
        this.rotations = rotations;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }
}
