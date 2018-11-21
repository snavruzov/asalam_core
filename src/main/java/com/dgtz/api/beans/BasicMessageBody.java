package com.dgtz.api.beans;

import java.io.Serializable;

/**
 * BroCast.
 * Copyright: Sardor Navruzov
 * 2013-2016.
 */
public class BasicMessageBody implements Serializable {

    public String hash;
    public Long msg_type; //1-text, 2-share, 3-video, 4-picture, 5-voice, 6-system, 7-channel, 8-debate
    public String body;
    public String id_room;
    public String id_msg;

    public BasicMessageBody() {
    }

    public BasicMessageBody(String hash, Long msg_type, String body, String id_room, String id_msg) {
        this.hash = hash;
        this.msg_type = msg_type;
        this.body = body;
        this.id_room = id_room;
        this.id_msg = id_msg;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(Long msg_type) {
        this.msg_type = msg_type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId_room() {
        return id_room;
    }

    public void setId_room(String id_room) {
        this.id_room = id_room;
    }

    public String getId_msg() {
        return id_msg;
    }

    public void setId_msg(String id_msg) {
        this.id_msg = id_msg;
    }
}
