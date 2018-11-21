package com.dgtz.api.feature;

import com.brocast.riak.api.beans.RoomUsersInfo;
import com.brocast.riak.api.dao.RiakAPI;
import com.brocast.riak.api.dao.RiakTP;
import com.brocast.riak.api.factory.IRiakSaveFactory;
import com.brocast.riak.api.factory.RiakSaveFactory;
import com.dgtz.api.beans.BasicMessageBody;
import com.dgtz.api.settings.ISystemDelivery;
import com.dgtz.db.api.domain.Notification;
import com.dgtz.db.api.enums.EnumNotification;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * Created by sardor on 7/24/17.
 */
public class SystemDelivery implements ISystemDelivery {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SystemDelivery.class);
    private static final String PUSH_NOTIFCATION_URL = "http://localhost:28443";
    private static final String EMAIL_NOTIFCATION_URL = "http://localhost:28443";

    private String hash;
    private Notification notif;

    protected SystemDelivery(Notification notif) {
        hash = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY+notif.getIdFrom(), "hash");
        this.notif = notif;
    }

    public static SystemDelivery builder(Notification notif) {
        return new SystemDelivery(notif);
    }

    @Override
    public ISystemDelivery inbox() {
        RiakTP transport = RiakAPI.getInstance();
        IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
        String endpoint = "room/publish";
        if(notif.getType()== EnumNotification.START_FOLLOWED.value){
            endpoint = "room/system/publish";
            hash = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY+notif.getIdUser(), "hash");
        }
        RoomUsersInfo rinf = saveFactory.bldConversation(notif.getIdFrom()+"", notif.getIdUser()+"");

        BasicMessageBody messageBody =
                new BasicMessageBody(hash, (long)notif.getType(), notif.getIdMedia()+":"+notif.getIdChannel(), rinf.id_room, "");
        Future<HttpResponse<JsonNode>> rStatus = Unirest.post(Constants.INBOX_URL + endpoint)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(messageBody)
                .asJsonAsync(new Callback<JsonNode>() {
                    public void failed(UnirestException e) {
                        log.debug("The request has failed");
                    }

                    public void completed(HttpResponse<JsonNode> response) {
                        log.debug("Completed {}", response.getBody());
                    }

                    public void cancelled() {
                        log.debug("The request has been cancelled");
                    }
                });

        return this;
    }

    @Override
    public ISystemDelivery system() {
        hash = RMemoryAPI.getInstance()
                .pullHashFromMemory(Constants.USER_KEY+notif.getIdUser(), "hash");

        BasicMessageBody messageBody =
                new BasicMessageBody(hash, (long)notif.getType(), notif.getIdMedia()+":"+notif.getIdChannel()+":"+notif.getIdFrom(), "", "");
        Future<HttpResponse<JsonNode>> rStatus = Unirest.post(Constants.INBOX_URL + "room/system/publish")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(messageBody)
                .asJsonAsync(new Callback<JsonNode>() {
                    public void failed(UnirestException e) {
                        log.debug("The request has failed");
                    }

                    public void completed(HttpResponse<JsonNode> response) {
                        log.debug("Completed {}", response.getBody());
                    }

                    public void cancelled() {
                        log.debug("The request has been cancelled");
                    }
                });

        return this;
    }

    @Override
    public ISystemDelivery socket() {
        Long hoster = notif.getIdHoster()==0?notif.getIdMedia():notif.getIdHoster();
        JsonNode node =
                new JsonNode("{\"time\":\""+RMemoryAPI.getInstance().currentTimeMillis()+"\",\"idHash\":\""+hash+"\"," +
                        "\"text\":\""+notif.getText()+"\",\"idMedia\":\"" + hoster + "\"," +
                        "\"wsType\":"+notif.getType()+",\"ratingType\":"+notif.getRatingType()+"," +
                        "\"colorType\":"+notif.getColorType()+"," +
                        "\"rotate\":"+notif.getRotation()+",\"duration\":"+notif.getDuration()+"" +
                        ",\"commentType\":"+notif.getCommentType()+"}");
        Future<HttpResponse<JsonNode>> rStatus =
                Unirest.post(Constants.WEBSOCKET_URL + "ws/media/" + hoster)
                        .header("Content-Type", "application/json")
                        .body(node)
                        .asJsonAsync(new Callback<JsonNode>() {
                            public void failed(UnirestException e) {
                                log.debug("The WS request has failed");
                            }

                            public void completed(HttpResponse<JsonNode> response) {
                                int code = response.getStatus();
                                log.debug("Completed {}", code);
                            }

                            public void cancelled() {
                                log.debug("The request has been cancelled");
                            }
                        });

        return this;
    }

    @Override
    public ISystemDelivery push() {
        try {
            String partUrl = "/private/push/notice";
            long key = System.nanoTime();
            String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + notif.getIdFrom(), "avatar");
            avatar = Constants.STATIC_URL + notif.getIdFrom() + "/image" + avatar + ".jpg";
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "type", notif.getType() + "", 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "title", notif.getText(), 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "avatar", avatar, 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idChannel", notif.getIdChannel() + "", 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idMedia", notif.getIdMedia() + "", 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "username", notif.getUsername(), 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idfrom", notif.getIdFrom() + "", 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idto", notif.getIdUser() + "", 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "email", notif.getEmail(), 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "id_room", notif.getIdRoom(), 300);

            String commentDelay = RMemoryAPI.getInstance()
                    .pullElemFromMemory(Constants.ACTIVITY + "push_comment:" + notif.getIdMedia());
            if (notif.getType() == 0 && commentDelay == null) {
                RMemoryAPI.getInstance()
                        .pushElemToMemory(Constants.ACTIVITY + "push_comment:" + notif.getIdMedia(), 5, "1");
                Unirest.get(PUSH_NOTIFCATION_URL + partUrl + "?key=" + key).asJson();
            } else {
                Unirest.get(PUSH_NOTIFCATION_URL + partUrl + "?key=" + key).asJson();
            }
        }
        catch(Exception e){
            log.error("Error while sending system inbox", e);
        }

        return this;
    }

    @Override
    public ISystemDelivery email() {
        try {
            String partUrl = "/private/email/notice";
            long key = System.nanoTime();
            String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + notif.getIdFrom(), "avatar");
            avatar = Constants.STATIC_URL + notif.getIdFrom() + "/image" + avatar + ".jpg";
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "type", notif.getType() + "", 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "title", notif.getText() + "", 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "avatar", avatar, 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idChannel", notif.getIdChannel() + "", 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idMedia", notif.getIdMedia() + "", 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "username", notif.getUsername() + "", 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idfrom", notif.getIdFrom() + "", 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "activation", notif.getActivation(), 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idto", notif.getIdUser() + "", 300);
            RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "email", notif.getEmail(), 300);

            Unirest.get(PUSH_NOTIFCATION_URL + partUrl + "?key=" + key).asJson();
        } catch (Exception e){
            log.error("Error while sending system inbox", e);
        }

        return this;
    }
}
