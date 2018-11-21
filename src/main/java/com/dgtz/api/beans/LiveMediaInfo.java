package com.dgtz.api.beans;

import com.brocast.riak.api.beans.DcUsersEntity;
import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by sardor on 1/4/14.
 */
public class LiveMediaInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idLive;
    private DcUsersEntity user;
    private long idUser;
    private Long idInUser;
    private String description;
    private String rtmpUrl = "";
    private String tags;
    private String[] parsedRtmpUrl;
    private String dateadded;
    private Long idCateg;
    private Long idMedia;
    private String httpUrl;
    private Boolean stop;
    private String title;
    private String location;
    private String latlong;
    private int process;
    private String idHash;
    private long idChannel;
    private String idKey;
    private String header;
    private String chName;
    private Boolean debate;
    private String position;
    private String lang = "en";
    private String eventTime;
    private Integer download = 0;
    private int platformType; //1 -mobile, 2- web
    private String method;
    private int defclip = -1;

    public int getDefclip() {
        return defclip;
    }

    public void setDefclip(int defclip) {
        this.defclip = defclip;
    }

    public Integer getDownload() {
        return download;
    }

    public void setDownload(Integer download) {
        this.download = download;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public Long getIdInUser() {
        return idInUser;
    }

    public void setIdInUser(Long idInUser) {
        this.idInUser = idInUser;
    }

    public Boolean getDebate() {
        return debate;
    }

    public void setDebate(Boolean debate) {
        this.debate = debate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public int getPlatformType() {
        return platformType;
    }

    public void setPlatformType(int platformType) {
        this.platformType = platformType;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public String getIdHash() {
        return idHash;
    }

    public void setIdHash(String idHash) {
        this.idHash = idHash;
    }

    public String[] getParsedRtmpUrl() {
        return parsedRtmpUrl;
    }

    public void setParsedRtmpUrl(String[] parsedRtmpUrl) {
        this.parsedRtmpUrl = parsedRtmpUrl;
    }

    public long getIdLive() {
        return idLive;
    }

    public void setIdLive(long idLive) {
        this.idLive = idLive;
    }

    public DcUsersEntity getUser() {
        return user;
    }

    public void setUser(DcUsersEntity user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }

    public String getDateadded() {
        return dateadded;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }

    public Long getIdCateg() {
        return idCateg;
    }

    public void setIdCateg(Long idCateg) {
        this.idCateg = idCateg;
    }

    public Long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(Long idMedia) {
        this.idMedia = idMedia;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public Boolean getStop() {
        return stop;
    }

    public void setStop(Boolean stop) {
        this.stop = stop;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdKey() {
        return idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }


    public long getIdChannel() {
        return idChannel;
    }

    public void setIdChannel(long idChannel) {
        this.idChannel = idChannel;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }
}
