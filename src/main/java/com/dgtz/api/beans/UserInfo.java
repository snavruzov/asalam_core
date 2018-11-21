package com.dgtz.api.beans;

import com.brocast.riak.api.beans.DcUsersEntity;
import com.brocast.riak.api.beans.PrivateInfo;
import com.brocast.riak.api.dao.RiakAPI;
import com.brocast.riak.api.dao.RiakTP;
import com.brocast.riak.api.factory.IRiakQueryFactory;
import com.brocast.riak.api.factory.RiakQueryFactory;
import com.dgtz.api.enums.EnumAuthErrors;
import com.dgtz.db.api.factory.GsonInsta;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/19/13
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUser;
    private String username;
    private String fullName;
    private transient String secWord;
    private String email;
    private String avatar;
    private String wallpic;
    private String hash;
    private String token;
    private Integer type; /*1- BROCAST, 2- FB, 3- GOOGLE, 4- VK, 5- TWITTER*/
    private Integer status; /*1- WAIT EMAIL APPROVE, 0- NOTHING*/
    private String location;
    private String city;
    private String country;

    private String about;
    private long fcount;
    private long flwnum;
    private long mcount;
    private long chcount;
    private PrivateInfo pInfo;
    private Boolean newUser;
    private String idInbox;
    private EnumAuthErrors error_msg = EnumAuthErrors.OK;

    public UserInfo() {
    }
    public UserInfo(DcUsersEntity entity, PrivateInfo pInfo, long idUser, String avatar, int type) {

        this.username = entity.username;
        this.fullName = entity.fullname;
        this.idUser = idUser;
        this.email = entity.email;
        this.hash = entity.hash;
        this.pInfo = pInfo;
        this.type = type;
        this.idInbox = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY+idUser, "inbox");
        String last_auth = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY+idUser, "last_auth");
        this.newUser = last_auth == null;
        RMemoryAPI.getInstance()
                .pushHashToMemory(Constants.USER_KEY + idUser, "last_auth", RMemoryAPI.getInstance().currentTimeMillis()+"");

        if (entity.email.contains("brocast.com")) {
            this.status = 1; //Need an valid email to resotre or update password
        } else {
            this.status = 0;
        }
        String wallpic = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "wallpic");
        if(wallpic != null && !wallpic.isEmpty() && !wallpic.contains("empty")) {
            wallpic = Constants.STATIC_URL + idUser + "/image" + wallpic + ".jpg";
        } else {
            wallpic = Constants.STATIC_URL + "defaults/profile-cover.jpg";
        }
        this.avatar = avatar;
        this.wallpic = wallpic;

        this.location = entity.country+" "+entity.city;

        this.fcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWS + idUser);
        this.flwnum = RMemoryAPI.getInstance().checkSetElemCount(Constants.FOLLOWERS + idUser);
        this.mcount = getUserVideosCount(idUser);
    }

    public void setFlwnum(long flwnum) {
        this.flwnum = flwnum;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWallpic() {
        return wallpic;
    }

    public void setWallpic(String wallpic) {
        this.wallpic = wallpic;
    }

    public Boolean getNewUser() {
        return newUser;
    }

    public void setNewUser(Boolean newUser) {
        this.newUser = newUser;
    }

    public String getIdInbox() {
        return idInbox;
    }

    public void setIdInbox(String idInbox) {
        this.idInbox = idInbox;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public EnumAuthErrors getError_msg() {
        return error_msg;
    }

    public void setError_msg(EnumAuthErrors error_msg) {
        this.error_msg = error_msg;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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

    public String getSecWord() {
        return secWord;
    }

    public void setSecWord(String secWord) {
        this.secWord = secWord;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PrivateInfo getpInfo() {
        return pInfo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public long getFcount() {
        return fcount;
    }

    public void setFcount(long fcount) {
        this.fcount = fcount;
    }

    public long getMcount() {
        return mcount;
    }

    public void setMcount(long mcount) {
        this.mcount = mcount;
    }

    public long getChcount() {
        return chcount;
    }

    public void setChcount(long chcount) {
        this.chcount = chcount;
    }

    public long getFlwnum() {
        return flwnum;
    }

    public void setpInfo(PrivateInfo pInfo) {
        this.pInfo = pInfo;
    }

    private Long getUserVideosCount(long idUser) {
        Long count = 0L;

        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            count = queryFactory.queryUsersVideoCountByIDUser(idUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    @Override
    public String toString() {
        return GsonInsta.getInstance().toJson(this);
    }
}
