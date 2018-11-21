package com.dgtz.api.beans;

import com.brocast.riak.api.beans.LiveProps;
import com.brocast.riak.api.beans.MediaStatistics;
import com.dgtz.db.api.beans.DcDebateEntity;
import com.dgtz.db.api.beans.ScreenRotation;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Set;

/**
 * BroCast.
 * Copyright: Sardor Navruzov
 * 2013-2016.
 */
public class MediaVIewInfo {
    public Long idMedia;
    public String title;
    public Integer duration;
    public String dateadded;
    public Integer idCategory = 1;
    public Long idUser;
    public String description;
    public Integer progress;
    public String location;
    public Long idChannel = 0l;
    public String channelTitle;
    public String thumb;
    public String thumb_webp;
    public String lang;
    public LiveProps liveProps;
    public Long lastUpdate;
    public Long rating;
    public String username;
    public String latlong;
    public MediaStatistics stats;
    public String time_start;
    public Boolean verified;
    public String avatar;
    public Boolean followed;
    public Long fcount;
    public Set<String> tags;
    public String ratio = "4:3";
    public String method = "";
    public Boolean access = true;
    public Boolean blocked;
    public Boolean voice;
    public String currentTime;
    public List<DcDebateEntity> debaters;
    public List<ScreenRotation> rotations;
    public String storyboard;

    public String getStoryboard() {
        return storyboard;
    }

    public void setStoryboard(String storyboard) {
        this.storyboard = storyboard;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public List<ScreenRotation> getRotations() {
        return rotations;
    }

    public void setRotations(List<ScreenRotation> rotations) {
        this.rotations = rotations;
    }

    public List<DcDebateEntity> getDebaters() {
        return debaters;
    }

    public void setDebaters(List<DcDebateEntity> debaters) {
        this.debaters = debaters;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Boolean getVoice() {
        return voice;
    }

    public void setVoice(Boolean voice) {
        this.voice = voice;
    }

    public Boolean getAccess() {
        return access;
    }

    public void setAccess(Boolean access) {
        this.access = access;
    }

    public Boolean getVerified() {
        return verified;
    }

    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
    }

    public Long getFcount() {
        return fcount;
    }

    public void setFcount(Long fcount) {
        this.fcount = fcount;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

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

    public Integer getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Integer idCategory) {
        this.idCategory = idCategory;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(Long idChannel) {
        this.idChannel = idChannel;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getThumb_webp() {
        return thumb_webp;
    }

    public void setThumb_webp(String thumb_webp) {
        this.thumb_webp = thumb_webp;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public LiveProps getLiveProps() {
        return liveProps;
    }

    public void setLiveProps(LiveProps liveProps) {
        this.liveProps = liveProps;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public MediaStatistics getStats() {
        return stats;
    }

    public void setStats(MediaStatistics stats) {
        this.stats = stats;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
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

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }
}
