package com.dgtz.api.beans;


import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by sardor on 1/4/14.
 */
public class MediaListInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private long idMedia;
    private String title;
    private String description;
    private String url;
    private String url_hls_lo;
    private java.lang.String url_mp4_low;
    private java.lang.String url_rtmp;
    private java.lang.String url_webm_hi;
    private java.lang.String url_webm_low;
    private java.lang.String url_thumb;
    private java.lang.String url_hls_vod;
    private String contentType;
    private Integer duration;
    private Long rootComment;
    private String dateadded;
    private int idCategory;
    private boolean isLive;
    private long idUser;
    private String username;
    private String ctitle;
    private String location;
    private long amount;
    private long idChannel;
    private Set<String> tags;
    private String city;
    private String avatar;

    private Integer progress;
    private Integer props;
    private String alt_user;

    private long liked;

    private long disliked;
    private String currenTime;
    private int width;
    private int height;

    private String ratio;
    private String method;

    private boolean blocked;
    private boolean voice;
    private boolean verified = false;

    private UserActivities activities;

    public MediaListInfo() {
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isVoice() {
        return voice;
    }

    public void setVoice(boolean voice) {
        this.voice = voice;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl_hls_lo() {
        return url_hls_lo;
    }

    public void setUrl_hls_lo(String url_hls_lo) {
        this.url_hls_lo = url_hls_lo;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getCurrenTime() {
        return currenTime;
    }

    public void setCurrenTime(String currenTime) {
        this.currenTime = currenTime;
    }

    public String getUrl_thumb() {
        return url_thumb;
    }

    public String getUrl_rtmp() {
        return url_rtmp;
    }

    public void setUrl_rtmp(String url_rtmp) {
        this.url_rtmp = url_rtmp;
    }

    public void setUrl_thumb(String url_thumb) {
        this.url_thumb = url_thumb;
    }

    public String getUrl_mp4_low() {
        return url_mp4_low;
    }

    public void setUrl_mp4_low(String url_mp4_low) {
        this.url_mp4_low = url_mp4_low;
    }

    public String getUrl_webm_hi() {
        return url_webm_hi;
    }

    public void setUrl_webm_hi(String url_webm_hi) {
        this.url_webm_hi = url_webm_hi;
    }

    public String getUrl_hls_vod() {
        return url_hls_vod;
    }

    public void setUrl_hls_vod(String url_hls_vod) {
        this.url_hls_vod = url_hls_vod;
    }

    public String getUrl_webm_low() {
        return url_webm_low;
    }

    public void setUrl_webm_low(String url_webm_low) {
        this.url_webm_low = url_webm_low;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCity() {
        return city;
    }

    public Integer getProps() {
        return props;
    }

    public void setProps(Integer props) {
        this.props = props;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public MediaListInfo(long idMedia, String title, String url, Integer duration, Long rootComment, String dateadded, int idCategory, boolean isLive, long idUser) {
        this.idMedia = idMedia;
        this.title = title;
        this.url = url;
        this.duration = duration;
        this.rootComment = rootComment;
        this.dateadded = dateadded;
        this.idCategory = idCategory;
        this.isLive = isLive;
        this.idUser = idUser;
    }


    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String getAlt_user() {
        return alt_user;
    }

    public void setAlt_user(String alt_user) {
        this.alt_user = alt_user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(long idChannel) {
        this.idChannel = idChannel;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public long getLiked() {
        return liked;
    }

    public void setLiked(long liked) {
        this.liked = liked;
    }

    public long getDisliked() {
        return disliked;
    }

    public void setDisliked(long disliked) {
        this.disliked = disliked;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(long idMedia) {
        this.idMedia = idMedia;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getRootComment() {
        return rootComment;
    }

    public void setRootComment(Long rootComment) {
        this.rootComment = rootComment;
    }

    public String getDateadded() {
        return dateadded;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean isLive) {
        this.isLive = isLive;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserActivities getActivities() {
        return activities;
    }

    public void setActivities(UserActivities activities) {
        this.activities = activities;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }


}
