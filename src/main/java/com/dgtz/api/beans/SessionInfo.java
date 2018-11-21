package com.dgtz.api.beans;

import com.dgtz.db.api.factory.GsonInsta;

import java.io.Serializable;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 6/12/14
 */
public class SessionInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long idUser;
    private String idToken;

    public SessionInfo() {
    }

    public SessionInfo(Long idUser, String idToken) {
        this.idUser = idUser;
        this.idToken = idToken;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @Override
    public String toString() {
        return GsonInsta.getInstance().toJson(this);
    }
}
