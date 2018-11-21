package com.dgtz.api.beans;


import com.brocast.riak.api.beans.DcUsersEntity;

import java.io.Serializable;

/**
 * Created by sardor on 1/8/14.
 */
public class MediaProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idMedia;
    private String idHash;
    private transient long idUser;
    private String title;
    private String url;
    private int idCategory;
    private int process;
    private String description;
    private String tags;
    private String location;
    private long channel;
    private boolean showLocation;
    private Integer props;
    private transient boolean notify = true;
    private DcUsersEntity user;

    public DcUsersEntity getUser() {
        return user;
    }

    public void setUser(DcUsersEntity user) {
        this.user = user;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public Integer getProps() {
        return props;
    }

    public void setProps(Integer props) {
        this.props = props;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getIdHash() {
        return idHash;
    }

    public void setIdHash(String idHash) {
        this.idHash = idHash;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getChannel() {
        return channel;
    }

    public void setChannel(long channel) {
        this.channel = channel;
    }

    public boolean isShowLocation() {
        return showLocation;
    }

    public void setShowLocation(boolean showLocation) {
        this.showLocation = showLocation;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    @Override
    public String toString() {
        return "MediaProperties{" +
                "idMedia=" + idMedia +
                ", idHash='" + idHash + '\'' +
                ", idUser=" + idUser +
                ", title='" + title + '\'' +
                ", idCategory=" + idCategory +
                ", description='" + description + '\'' +
                ", tags='" + tags + '\'' +
                ", location='" + location + '\'' +
                ", channel=" + channel +
                ", showLocation=" + showLocation +
                ", notify=" + notify +
                '}';
    }
}
