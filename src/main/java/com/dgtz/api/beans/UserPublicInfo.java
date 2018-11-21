package com.dgtz.api.beans;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by sardor on 1/11/16.
 */
public class UserPublicInfo implements Serializable{
    private static final long serialVersionUID = 1L;

    private Long idUser;
    private String username;
    private String fullName;
    private String avatar;
    private String location;
    private String about;
    private Long frcount = 0l;
    private Long flwnum = 0l;
    private Long fcount = 0l;
    private Long mcount = 0l;
    private Long chcount = 0l;
    private Boolean verified = false;
    private Boolean reFollow;


    public UserPublicInfo() {
    }

    public Long getFlwnum() {
        return flwnum;
    }

    public void setFlwnum(Long flwnum) {
        this.flwnum = flwnum;
    }

    public Boolean getReFollow() {
        return reFollow;
    }

    public void setReFollow(Boolean reFollow) {
        this.reFollow = reFollow;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Long getFrcount() {
        return frcount;
    }

    public void setFrcount(Long frcount) {
        this.frcount = frcount;
    }

    public Long getFcount() {
        return fcount;
    }

    public void setFcount(Long fcount) {
        this.fcount = fcount;
    }

    public Long getMcount() {
        return mcount;
    }

    public void setMcount(Long mcount) {
        this.mcount = mcount;
    }

    public Long getChcount() {
        return chcount;
    }

    public void setChcount(Long chcount) {
        this.chcount = chcount;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }

}
