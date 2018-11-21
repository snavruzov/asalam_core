package com.dgtz.api.contents;

import com.brocast.riak.api.beans.*;
import com.brocast.riak.api.dao.RiakAPI;
import com.brocast.riak.api.dao.RiakTP;
import com.brocast.riak.api.factory.IRiakQueryFactory;
import com.brocast.riak.api.factory.IRiakSaveFactory;
import com.brocast.riak.api.factory.RiakQueryFactory;
import com.brocast.riak.api.factory.RiakSaveFactory;
import com.dgtz.api.beans.BasicMessageBody;
import com.dgtz.api.beans.LikeInfo;
import com.dgtz.api.beans.RatingInfo;
import com.dgtz.api.beans.SessionInfo;
import com.dgtz.api.constants.Formula;
import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.enums.UserActivitiesLogEnum;
import com.dgtz.api.feature.*;
import com.dgtz.api.settings.ISystemDelivery;
import com.dgtz.api.utils.AbuseFilter;
import com.dgtz.api.utils.LanguageDetector;
import com.dgtz.api.utils.TagTokenizer;
import com.dgtz.db.api.beans.ChangeLogs;
import com.dgtz.db.api.domain.*;
import com.dgtz.db.api.enums.EnumNotification;
import com.dgtz.db.api.enums.EnumSQLErrors;
import com.dgtz.mcache.api.dao.RedisAPI;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Created by sardor on 1/8/14.
 */
public class UsersShelf extends DBConnector {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(UsersShelf.class);
    private static final String PUSH_NOTIFCATION_URL = "http://localhost:8443";
    private static final String EMAIL_NOTIFCATION_URL = "http://localhost:8443";
    private String lang;
    private static final int LIMIT = 12;


    public UsersShelf() {
        super();
    }

    public UsersShelf(String lang) {
        super();
        this.lang = LanguageDetector.detectLang(lang);

    }

    public DcUsersEntity getUserInfoByHash(String hash) {
        return getDbForQuery().getUserInfoByHash(hash);
    }

    public long extractUniqueIdForUser() {
        return getDbForQuery().extractUniqueIdForUser();
    }

    public boolean tokenAccept(String token, long idUser) {
        boolean accept = false;
        try {
            SessionInfo sessionInfo = (SessionInfo) RMemoryAPI.getInstance()
                    .pullElemFromMemory(Formula.USER_SESSION + token, SessionInfo.class);
            if (sessionInfo != null) {
                accept = sessionInfo.getIdUser() == idUser;
            }
        } catch (Exception e) {
            log.debug("ERROR IN MAIN API TOKEN NOT FOUND OR ERROR IN MEMORY", e);
        }

        return accept;
    }

    public EnumSQLErrors saveVideoRate(DcMediaEntity entity, int type, int color, long now) {

        Notification notif = new Notification();
        notif.setType(12);
        notif.setColorType(color);
        notif.setRatingType(type);
        String hostLiveID = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + entity.idMedia, "debate.author");
        if(hostLiveID==null) {
            notif.setIdMedia(entity.idMedia);
            notif.setIdHoster(entity.idMedia);
        } else {
            notif.setIdMedia(Long.valueOf(hostLiveID));
            notif.setIdHoster(entity.idMedia);
        }
        notif.setIdFrom(entity.idUser);
        ISystemDelivery systemDelivery = SystemDelivery
                .builder(notif)
                .socket();

        if (entity.method.equals("live")) {

            String idTime = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "start-time");
            if (idTime != null) {
                now = (RMemoryAPI.getInstance().currentTime() - Long.valueOf(idTime));
            }
        }

        RMemoryAPI.getInstance()
                .pushHashIncrToMemory(Constants.ACTIVITY + "likes:cnt:" + entity.idMedia + ":" + now, type + ":" + color, 1);
        RMemoryAPI.getInstance()
                .pushSetElemToMemory(Constants.ACTIVITY + "likes:time:" + entity.idMedia, type + ":" + color+":"+now);

        RMemoryAPI.getInstance().pushHashIncrToMemory(Constants.MEDIA_KEY + entity.idMedia, "liked", 1);
        RMemoryAPI.getInstance().pushHashIncrToMemory(Constants.USER_KEY + entity.idUser, "stars", 1);
        return EnumSQLErrors.OK;

    }

    public List<RatingInfo> getVideoRate(DcMediaEntity entity) {

        List<RatingInfo> lklist = new ArrayList<>();

        Map<String, List<LikeInfo>> lm = new HashMap<>();
        try {
            Set<String> res = RMemoryAPI.getInstance()
                    .pullSetElemFromMemory(Constants.ACTIVITY + "likes:time:" + entity.idMedia);
            res.forEach(tpl -> {
                {
                    JSONObject json = new JSONObject();
                    String[] elms = tpl.split(":");
                    List<LikeInfo> linfo = lm.get(elms[2]);
                    LikeInfo likeInfo = new LikeInfo();
                    String num = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.ACTIVITY + "likes:cnt:" + entity.idMedia + ":" + elms[2], elms[0] + ":" + elms[1]);
                    if(linfo==null){
                        linfo = new ArrayList<>();
                        likeInfo.type = elms[0];
                        likeInfo.color = elms[1];
                        likeInfo.num = num;
                        linfo.add(likeInfo);
                        lm.put(elms[2],linfo);
                    } else {
                        likeInfo.type = elms[0];
                        likeInfo.color = elms[1];
                        likeInfo.num = num;
                        linfo.add(likeInfo);
                        lm.put(elms[2],linfo);
                    }
                }
            });

            lm.forEach((time, likeInfos) -> {
                RatingInfo json = new RatingInfo();
                json.time = time;
                json.data = likeInfos;
                lklist.add(json);
            });

        } catch (Exception e) {
            log.error("ERROR IN MAIN API ", e);
        }

        return lklist;
    }

    public long insertFreshComments(DcCommentsEntity entity, Long dest, long timeFrac) {

        AbuseFilter f = new AbuseFilter();

        String text = entity.getText();
        try {
            entity.setText(f.filter(text));
        } catch (UnsupportedEncodingException e) {
            log.error("ERROR IN WORDS FILTERING");
        }
        if(entity.idComment==0){
            entity.idComment = System.currentTimeMillis();
        }
        Notification notification = new Notification();
        notification.setIdFrom(entity.getIdUser());
        notification.setType(0);
        notification.setDuration((short)entity.duration);
        notification.setIdMedia(entity.idMedia);
        notification.setText(entity.text);
        notification.setCommentType(entity.commentType);
        notification.setIdUser(dest);
        ISystemDelivery systemDelivery = SystemDelivery
                .builder(notification)
                .push()
                .socket();

        RMemoryAPI.getInstance().pushHashIncrToMemory(Constants.MEDIA_KEY + entity.getIdMedia(), "ccount", 1);
        getDbForUpd().addComments(entity, timeFrac);

        return entity.idComment;
    }

    public void updateUserDevice(String devid, long idUser, String devType) {
        getDbForUpd().updateUserDevice(devid, idUser, devType);
    }

    public EnumSQLErrors flagTheMedia(long idUser, long idMedia, short idFlag) {
        return getDbForUpd().reportMediaActivity(idUser, idMedia, idFlag);
    }

    public short isFlaggedTheMedia(long idUser, long idMedia) {
        return getDbForQuery().extractVideoReportByUserAct(idUser, idMedia);
    }

    public long insertFreshChannel(DcChannelsEntity entity) {
        long idCh = getDbForChannel().createChannel(entity);
        if (idCh != 0) {
            addUserActChangeLog(entity.getIdUser(), entity.getIdChannel(), UserActivitiesLogEnum.CREATED_CHANNEL.name(), 2); //2 - Channel related type
            AmazonS3Module s3Module = new AmazonS3Module();
            s3Module.copyImageFile("defaults/channel.jpg", entity.getIdUser() + "/image" + entity.getAvatar() + ".jpg");
            s3Module.copyImageFile("defaults/channel-cover.jpg", entity.getIdUser() + "/image" + entity.getWall() + ".jpg");
        }


        return idCh;
    }

    public List<DcChannelsEntity> extractChannelByOwner(long idOwner) {
        return getDbForChannel().extractChannelsInfoByOwner(idOwner);
    }

    public EnumErrors subToChannel(long idChannel, long idUser) {
        EnumSQLErrors sqlErrors = getDbForChannel().subscribeToChannel(idUser, idChannel);
        return sqlErrors == EnumSQLErrors.OK ? EnumErrors.NO_ERRORS : EnumErrors.CANNOT_SUBSCRIBE_CHANNEL;
    }

    public EnumErrors unSubFromChannel(long idChannel, long idUser, long idOwner) {
        EnumSQLErrors sqlErrors = getDbForChannel().unsubFromChannel(idUser, idChannel);

        return sqlErrors == EnumSQLErrors.OK ? EnumErrors.NO_ERRORS : EnumErrors.UNSUBSCRIBE_CHANNEL_ERROR;
    }

    public List<UserShortInfo> extractChannelSubscribers(long idChannel, long idUser, long off, long limit) {
        return getDbForChannel().extractSubsOfChannel(idChannel, idUser, off, limit, lang);
    }

    public List<UserShortInfo> extractChannelsUsers(long idChannel, long off, long limit) {
        return getDbForChannel().extractChannelsUsers(idChannel, off, limit, lang);
    }

    public Deque<UserShortInfo> extractChannelsUsers(long idChannel,long idUser, long off, long limit) {
        return getDbForChannel().extractChannelsUsers(idChannel, idUser, off, limit, lang);
    }

    public List<PublicChannelsEntity> extractChannelByUserSubs(long idUser, long off, long lm) {
        return getDbForChannel().extractChannelsInfoByIdUser(idUser, off, lm);
    }

    public List<PublicChannelsEntity> extractChannelByFollowUserSubs(long idUser, long off, long lm) {
        return getDbForChannel().extractChannelsInfoByFollowing(idUser, off, lm);
    }

    public List<PublicChannelsEntity> extractChannelByUserSearch(String name, long idUser, long off, long lm) {
        return getDbForChannel().extractChannelsInfoBySearch(name, idUser, off, lm);
    }

    public List<PublicChannelsEntity> extractChannelByFollowSearch(String name, long idUser, long off, long lm) {
        return getDbForChannel().extractChannelsInfoByFollowsSearch(name, idUser, off, lm);
    }

    public List<MediaPublicInfo> extractMediaByUserID(long idUser, int off, int limit, boolean isMine) {
        return getDbForQuery().extractUsersContentByIdUser(idUser, off, limit, isMine);
    }

    public List<EventListEntity> extractUserEventList(long idUser, int off, int limit) {
        List<EventListEntity> listEntities = new ArrayList<>();
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            listEntities = queryFactory.queryUserEventList(idUser, off, limit);
        } catch (Exception e){
            e.printStackTrace();
        }
        return listEntities;
    }

    public PublicChannelsEntity extractChannelByIdChannel(long idChannel, long idUser) {
        return getDbForChannel().extractChannelInfoByIdChannel(idChannel, idUser);
    }

    public PublicChannelsEntity extractPubChannelById(Long idChannel, long idUser){
        return getDbForChannel().extractChannelInfoByIdChannel(idChannel, idUser);
    }

    public EnumSQLErrors removeTheChannelByOwner(long idOwner, long idChannel, final String avatar) {

        try {
        } catch (Exception e) {
            log.warn("Couldn't remove channel cover picture check the S3 configs", e);
        }

        addUserActChangeLog(idOwner, idChannel, UserActivitiesLogEnum.REMOVED_CHANNEL.name(), 2); //2 - Channel related type
        return getDbForChannel().removeChannelById(idOwner, idChannel);
    }

    public JSONObject joinTheUserInChannel(long id_user, long idChannel, boolean isOwner, boolean accept, boolean refuse) {
        return getDbForChannel().joinToChannel(id_user, idChannel, isOwner, accept, refuse);
    }

    public EnumErrors blockUserForComment(long idMedia, long idUser, long blockUserId) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {

            RMemoryAPI.getInstance().pushSetElemToMemory("dc_users:comment:blocked:users:" + idUser, blockUserId + "");

            /*Notifying WS server about block*/
            JsonNode node =
                    new JsonNode("{\"time\":\""+RMemoryAPI.getInstance().currentTimeMillis()+"\",\"idHash\":\""+Formula.DEFAULT_HASH+"\"," +
                            "\"text\":\""+blockUserId+"\",\"idMedia\":\"" + idMedia + "\",\"wsType\":5}");

            try {
                Future<HttpResponse<JsonNode>> rStatus =
                        Unirest.post(Constants.WEBSOCKET_URL + "ws/media/" + idMedia)
                                .header("Content-Type", "application/json")
                                .body(node)
                                .asJsonAsync(new Callback<JsonNode>() {
                                    public void failed(UnirestException e) {
                                        log.debug("The request has failed");
                                    }

                                    public void completed(HttpResponse<JsonNode> response) {
                                        int code = response.getStatus();
                                        log.debug("Completed {}", code);
                                    }

                                    public void cancelled() {
                                        log.debug("The request has been cancelled");
                                    }
                                });
            }catch (Exception e){
                log.error("ERROR IN SENDING BROADCAST WS", e);
            }

            Set<String> clist =RMemoryAPI.getInstance()
                    .pullSetElemFromMemory(Constants.MEDIA_KEY + "comment:user:" + blockUserId+":"+idMedia);
            RiakTP transport = RiakAPI.getInstance();
            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            clist.forEach(idc -> saveFactory.removeMediaComments(Long.valueOf(idc)));

        } catch (Exception e) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("Error in blocking and removing comment", e);
        }

        return errors;
    }

    public EnumErrors removeUsersComment(long idComment, long idUser, long idMedia) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
            saveFactory.removeMediaComments(idComment);
            String hashOfCommentAuthor = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "hash");

            /*Notifying WS server about block*/
            JsonNode node =
                    new JsonNode("{\"time\":\"" + RMemoryAPI.getInstance().currentTimeMillis() + "\",\"idHash\":\"" + hashOfCommentAuthor + "\"," +
                            "\"text\":\"" + idComment + "\",\"idMedia\":\"" + idMedia + "\",\"wsType\":7}");

            try {
                Future<HttpResponse<JsonNode>> rStatus =
                        Unirest.post(Constants.WEBSOCKET_URL + "ws/media/" + idMedia)
                                .header("Content-Type", "application/json")
                                .body(node)
                                .asJsonAsync(new Callback<JsonNode>() {
                                    public void failed(UnirestException e) {
                                        log.debug("The request has failed");
                                    }

                                    public void completed(HttpResponse<JsonNode> response) {
                                        int code = response.getStatus();
                                        log.debug("Completed {}", code);
                                    }

                                    public void cancelled() {
                                        log.debug("The request has been cancelled");
                                    }
                                });
            } catch (Exception e) {
                log.error("ERROR IN SENDING BROADCAST WS", e);
            }

        } catch (Exception e) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("Error in blocking and removing comment", e);
        }

        return errors;
    }

    public EnumErrors allowUserForVoiceComment(long idUser, long voiceUserId, String idMedia) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            RMemoryAPI.getInstance().pushSetElemToMemory("dc_users:comment:voice:users:" + idUser, voiceUserId + "");

            if(idMedia!=null && !idMedia.isEmpty() && Long.valueOf(idMedia)!=0) {
            /*Notifying WS server about block*/
                JsonNode node =
                        new JsonNode("{\"time\":\"" + RMemoryAPI.getInstance().currentTimeMillis() + "\",\"idHash\":\""+Formula.DEFAULT_HASH+"\"," +
                                "\"text\":\"" + voiceUserId + "\",\"idMedia\":\"" + idMedia + "\",\"wsType\":6}");

                try {
                    Future<HttpResponse<JsonNode>> rStatus =
                            Unirest.post(Constants.WEBSOCKET_URL + "ws/media/" + idMedia)
                                    .header("Content-Type", "application/json")
                                    .body(node)
                                    .asJsonAsync(new Callback<JsonNode>() {
                                        public void failed(UnirestException e) {
                                            log.debug("The request has failed");
                                        }

                                        public void completed(HttpResponse<JsonNode> response) {
                                            int code = response.getStatus();
                                            log.debug("Completed {}", code);
                                        }

                                        public void cancelled() {
                                            log.debug("The request has been cancelled");
                                        }
                                    });
                } catch (Exception e) {
                    log.error("ERROR IN SENDING BROADCAST WS", e);
                }
            }
        } catch (Exception e) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("Error in allowing voice comment", e);
        }

        return errors;
    }

    public EnumErrors denyUserVoiceComment(long idUser, long voiceUserId, String idMedia){

        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            RMemoryAPI.getInstance().delFromSetElem("dc_users:comment:voice:users:" + idUser, voiceUserId + "");

            if(idMedia!=null && !idMedia.isEmpty() && Long.valueOf(idMedia)!=0) {
            /*Notifying WS server about block*/
                JsonNode node =
                        new JsonNode("{\"time\":\"" + RMemoryAPI.getInstance().currentTimeMillis() + "\",\"idHash\":\""+Formula.DEFAULT_HASH+"\"," +
                                "\"text\":\"" + voiceUserId + "\",\"idMedia\":\"" + idMedia + "\",\"wsType\":6}");

                try {
                    Future<HttpResponse<JsonNode>> rStatus =
                            Unirest.post(Constants.WEBSOCKET_URL + "ws/media/" + idMedia)
                                    .header("Content-Type", "application/json")
                                    .body(node)
                                    .asJsonAsync(new Callback<JsonNode>() {
                                        public void failed(UnirestException e) {
                                            log.debug("The request has failed");
                                        }

                                        public void completed(HttpResponse<JsonNode> response) {
                                            int code = response.getStatus();
                                            log.debug("Completed {}", code);
                                        }
                                        public void cancelled() {
                                            log.debug("The request has been cancelled");
                                        }
                                    });
                } catch (Exception e) {
                    log.error("ERROR IN SENDING BROADCAST WS", e);
                }
            }

        } catch (Exception e) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("Error in allowing voice comment", e);
        }

        return errors;

    }

    /**
     * @param type: 1- Comment, 2- Channel
     * */
    public List<UserShortInfo> blockedUsersList(long id_user, long type) {

        List<UserShortInfo> infoList = new ArrayList<>();

        List<String> res = RMemoryAPI.getInstance().pullSortedListElem("dc_users:comment:blocked:users:" + id_user,
                Constants.USER_KEY + "*->detail", 0, 100, Constants.USER_KEY + "*->id_user");
        Gson gson = new Gson();

        List<DcUsersEntity> list = gson.fromJson(res.toString(),
                new TypeToken<List<DcUsersEntity>>() {
                }.getType());

        list.forEach(entity -> {
            UserShortInfo object = new UserShortInfo();
            object.setAvatar(Constants.STATIC_URL + entity.idUser + "/image" + entity.avatar + ".jpg");
            object.setUsername(entity.username);
            object.setFullname(entity.fullname);
            object.setIdUser(entity.idUser);
            infoList.add(object);
        });

        return infoList;
    }


    public List<UserShortInfo> voiceUsersList(long id_user, int off, int limit) {

        List<UserShortInfo> infoList = new ArrayList<>();

        off = (off < 0 ? 0 : off);
        limit = ((limit <= 0 || limit > 60) ? LIMIT : limit);

        List<String> res = RMemoryAPI.getInstance().pullSortedListElem("dc_users:comment:voice:users:" + id_user,
                Constants.USER_KEY + "*->detail", off, limit, Constants.USER_KEY + "*->id_user");
        Gson gson = new Gson();

        List<DcUsersEntity> list = gson.fromJson(res.toString(),
                new TypeToken<List<DcUsersEntity>>() {
                }.getType());

        list.forEach(entity -> {
            UserShortInfo object = new UserShortInfo();
            object.setAvatar(Constants.STATIC_URL + entity.idUser + "/image" + entity.avatar + ".jpg");
            object.setUsername(entity.username);
            object.setFullname(entity.fullname);
            object.setIdUser(entity.idUser);
            infoList.add(object);
        });

        return infoList;
    }

    public void saveToMemory(String key, String value, int exp) {
        try {
            RMemoryAPI.getInstance().pushElemToMemory(key, exp, value);
        } catch (Exception e) {
            log.error("ERROR IN MAIN API: ", e);
        }
    }

    public long videoIsFollowed(long source, long dest) {
        return getDbForQuery().extractVideoIsFollowed(source, dest);
    }

    public void addExtraLanguageOfUser(String langCode, long idUser, int delLang){
        boolean trueLangCode = RMemoryAPI.getInstance().pullIfSetElem(Constants.TRANSLATION + ":list", langCode);
        if(trueLangCode || langCode.equals("en")){
            if(delLang==0){
                long langAmount = RMemoryAPI.getInstance().checkSetElemCount(Constants.USER_KEY + "extlang:" + idUser);
                if(langAmount>1) {
                    RMemoryAPI.getInstance().delFromSetElem(Constants.USER_KEY + "extlang:" + idUser, langCode);
                }
            }else {
                RMemoryAPI.getInstance().pushSetElemToMemory(Constants.USER_KEY + "extlang:" + idUser, langCode);
            }

        }

    }

    public Set<String> listOfuserLanguages(long idUser){
        return RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.USER_KEY + "extlang:" + idUser);
    }

    public ChangeLogs userLastChangeLogs(long idUser){
        ChangeLogs logs = null;
        Set<String> elems = RMemoryAPI.getInstance().pullLSetElemFromMemory(Constants.USER_KEY + "chngids:" + idUser, -1, -1);
        for(String lg: elems){
            logs = com.dgtz.mcache.api.utils.GsonInsta.getInstance().fromJson(lg, ChangeLogs.class);
        }

        return logs;
    }

    public List<ChangeLogs> userChangeLogDiffs(long idUser, Long localID){
        Set<String> elemID = RMemoryAPI.getInstance().pullLSortedListElem(Constants.USER_KEY + "chngids:" + idUser, "(" + localID);
        Gson gson = new Gson();
        List<ChangeLogs> list = gson.fromJson(elemID.toString(),
                new TypeToken<List<ChangeLogs>>() {
                }.getType());
        return list;
    }

    public NotificationInfo notificationByChannels(long idUser, long off, long limit) {
        return getDbForChannel().getChannelNotification(idUser, off, limit);
    }

    public Long userProfileStatisticInfo(long idUser, int type, boolean mine) {
        Long count = 0L;
        switch (type) {
            case 0: {
                count = getDbForQuery().getUserFollowsCount(idUser, 0); //Followers count
                break;
            }
            case 1: {
                count = getDbForQuery().getUserFollowsCount(idUser, 1); //Follows count
                break;
            }
            case 2: {
                count = getDbForQuery().getUserVideosCount(idUser, mine);
                break;
            }
            case 3: {
                //count = getDbForQuery().getUserChannelsCount(idUser, mine);
                break;
            }
        }
        return count;
    }

    public EnumSQLErrors followByUserVideo(long source, long dest) {
        EnumSQLErrors error = getDbForUpd().addFollower(source, dest);
        if(error == EnumSQLErrors.OK){
            allowUserForVoiceComment(source, dest, null);
        }

        return error;
    }

    public EnumErrors followByTag(String tags, long idUser) {
        Set<String> taglist = TagTokenizer.normilizeTag(tags);
        taglist.forEach(tg -> {
                    RMemoryAPI.getInstance().pushSetElemToMemory(Constants.FOLLOWS + "tags:" + idUser, tg);
                    RMemoryAPI.getInstance().pushSetElemToMemory(Constants.MEDIA_KEY + "tags:followers:" + tg, idUser + "");
                });

        return EnumErrors.NO_ERRORS;
    }

    public EnumErrors unFollowByTag(String tag, long idUser) {
        Set<String> taglist = TagTokenizer.normilizeTag(tag);
        taglist
                .forEach(tg -> {
                    RMemoryAPI.getInstance().delFromSetElem(Constants.FOLLOWS + "tags:" + idUser, tg);
                    RMemoryAPI.getInstance().delFromSetElem(Constants.MEDIA_KEY + "tags:followers:"+tg, idUser+"");
                });

        return EnumErrors.NO_ERRORS;
    }

    public EnumSQLErrors loggingOut(long idUser, String device) {
        return getDbForUpd().logoutUser(idUser, device);
    }

    public EnumSQLErrors unFollowByUserVideo(long source, long dest) {
        EnumSQLErrors error = getDbForUpd().unFollow(source, dest);
        if(error == EnumSQLErrors.OK){
            denyUserVoiceComment(source, dest, null);
        }

        return error;
    }

    public List<MediaPublicInfo> extractMediaCollection(Long idUser, int off, int limit) {
        List<MediaPublicInfo> list = new ArrayList<>();


        try {
            Set<String> collection = RMemoryAPI.getInstance()
                    .pullSetElemFromMemory(Constants.MEDIA_KEY + "collection:" + idUser);
            if (collection!= null && !collection.isEmpty()) {
                list = buildCollectionData(idUser, off, limit);
            } else {
                log.debug("Building collection for user {}", idUser);
                System.out.println("building fucking collectionnnnn");
                RiakTP transport = RiakAPI.getInstance();
                IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
                queryFactory.queryUserMediaCollection(idUser);

                list = buildCollectionData(idUser, off, limit);
            }

        } catch (Exception e) {
            log.error("Error while building the wall feed", e);
        }

        return list;
    }



    /*public void sendSystemNotifications(Notification notif, boolean inbox, boolean push, boolean socket, boolean email, boolean system){
        try{
            String hash = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY+notif.getIdFrom(), "hash");
            if(inbox) {
                RiakTP transport = RiakAPI.getInstance();
                IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
                String endpoint = "room/publish";
                if(notif.getType()==EnumNotification.START_FOLLOWED.value){
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
            }
            if(system) {
                hash = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY+notif.getIdUser(), "hash");

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
            }
            if(socket){
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
            }
            if(push){
                String partUrl = "/private/push/notice";
                long key = System.nanoTime();
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + notif.getIdFrom(), "avatar");
                avatar = Constants.STATIC_URL + notif.getIdFrom() + "/image" + avatar + ".jpg";
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "type", notif.getType()+"", 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "title", notif.getText(), 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "avatar", avatar, 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idChannel", notif.getIdChannel() + "", 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idMedia", notif.getIdMedia()+"", 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "username", notif.getUsername(), 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idfrom", notif.getIdFrom()+"", 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idto", notif.getIdUser()+"", 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "email", notif.getEmail(), 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "id_room", notif.getIdRoom(), 300);

                String commentDelay = RMemoryAPI.getInstance()
                        .pullElemFromMemory(Constants.ACTIVITY+"push_comment:"+notif.getIdMedia());
                if(notif.getType()==0 && commentDelay==null){
                    RMemoryAPI.getInstance()
                            .pushElemToMemory(Constants.ACTIVITY+"push_comment:"+notif.getIdMedia(), 5, "1");
                    Unirest.get(PUSH_NOTIFCATION_URL+partUrl+"?key="+key).asJson();
                } else {
                    Unirest.get(PUSH_NOTIFCATION_URL+partUrl+"?key="+key).asJson();
                }


            }
            if(email){
                String partUrl = "/private/email/notice";
                long key = System.nanoTime();
                String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + notif.getIdFrom(), "avatar");
                avatar = Constants.STATIC_URL + notif.getIdFrom() + "/image" + avatar + ".jpg";
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "type", notif.getType()+"", 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "title", notif.getText() + "", 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "avatar", avatar, 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idChannel", notif.getIdChannel() + "", 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idMedia", notif.getIdMedia()+"", 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "username", notif.getUsername() + "", 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idfrom", notif.getIdFrom()+"", 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "activation", notif.getActivation(), 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "idto", notif.getIdUser()+"", 300);
                RMemoryAPI.getInstance().pushHashToMemory(Constants.NOTIFICATION_KEY + key, "email", notif.getEmail(), 300);

                Unirest.get(PUSH_NOTIFCATION_URL + partUrl + "?key=" + key).asJson();

            }
        }catch (Exception e){
            log.error("Error while sending system inbox", e);
        }

    }*/

    private List<MediaPublicInfo> buildCollectionData(Long idUser, int off, int limit) {
        List<MediaPublicInfo> list = new ArrayList<>();

        List<String> res = RMemoryAPI.getInstance()
                .pullSortedListElem(Constants.MEDIA_KEY + "collection:" + idUser
                        , "dc_media:*->detail", off, limit, "dc_media:*->last_update");

        Gson gson = new Gson();
        List<DcMediaEntity> entList = gson.fromJson(res.toString(),
                new TypeToken<List<DcMediaEntity>>() {
                }.getType());

        String currentTime = System.currentTimeMillis()+"";
        for (DcMediaEntity md : entList) {
            if (md != null && md.getProgress() == 0) {
                MediaPublicInfo media = new MediaPublicInfo();
                Long idU = md.idUser;
                Long idM = md.idMedia;

                media.setIdMedia(idM);
                media.setIdUser(idU);
                String username = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + idU, "username");
                media.setUsername(username);
                String verified = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + idU, "verified");
                media.setVerified(verified == null ? false : Boolean.valueOf(verified));

                String thumb = Constants.encryptAmazonURL(idU, idM, "jpg", "thumb", Constants.STATIC_URL);
                String thumb_webp = Constants.encryptAmazonURL(idU, idM, "webp", "thumb", Constants.STATIC_URL);
                media.setThumb(thumb);
                media.setThumb_webp(thumb_webp);

                String lcount = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + idM, "liked");
                String vcount = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + idM, "vcount");

                media.setAmount(Long.valueOf(vcount));
                media.setLiked(Long.valueOf(lcount));


                Boolean reflw = RMemoryAPI.getInstance().pullIfSetElem(Constants.FOLLOWS + idUser, idU + "");
                String evnt_time = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + md.idMedia, "evnt_time");
                media.setFollowed(reflw);

                media.setStart_time(evnt_time);
                media.setMethod(md.method);

                media.setTags(md.tags);
                media.setTitle(md.title);
                if (md.idChannel != 0) {
                    String chTitle = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.CHANNEL_KEY + md.idChannel, "title");
                    media.setChannelTitle(chTitle);
                    media.setIdChannel(md.idChannel);
                }

                String shared = RMemoryAPI.getInstance()
                        .pullElemFromMemory(Constants.MEDIA_KEY + "shared:" + idM + ":" + idU); //original author
                if (shared != null) {
                    media.setSharedby(shared);
                }

                String avatar = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + idU, "avatar");
                media.setAvatar(Constants.STATIC_URL + idU + "/image" + avatar + "M.jpg");

                String ratio = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + idM, "ratio");
                media.setRatio(ratio);

                //String cod = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "language");
                //cod = (cod == null || cod.isEmpty()) ? "en" : cod;

                String location = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + idM, "location", "en");
                if(location!=null){
                    location = location.replace(";", ",");
                }
                media.setLocation(location);
                media.setCurrenTime(currentTime);
                media.setDuration(md.duration.shortValue());
                media.setDateadded(md.dateadded);

                list.add(media);
            }
        }


        return list;

    }

    public DcUsersEntity getUserInfoById(long idUser) {

        return getDbForQuery().getUserProfileInfoById(idUser);
    }

    public EnumSQLErrors updateChannelInfo(DcChannelsEntity entity) {

        EnumSQLErrors errors = getDbForChannel().updateChannelInfo(entity);

        return errors;
    }

    public EnumSQLErrors removeTheMediaByOwner(final long idOwner, final long idMedia) {

        try {
            Thread thread = new Thread(() -> {
                AmazonS3Module s3Module = new AmazonS3Module();
                {
                    s3Module.removeImageFile(Constants.encryptAmazonURL(idOwner, idMedia, null, "p", ""));
                    s3Module.removeVideoFile(Constants.encryptAmazonURL(idOwner, idMedia, "_lo.mp4", "v", ""));
                    s3Module.removeVideoFile(Constants.encryptAmazonURL(idOwner, idMedia, "_lo.webm", "v", ""));
                    s3Module.removeVideoFile(Constants.encryptAmazonURL(idOwner, idMedia, "_hi.mp4", "v", ""));
                    s3Module.removeVideoFile(Constants.encryptAmazonURL(idOwner, idMedia, "_hi.webm", "v", ""));
                    s3Module.removeVideoFolder(idOwner + "/hls_vod/" + idMedia);
                }
            });

            thread.setName("media_removing_thrd_" + System.currentTimeMillis());
            thread.start();

        } catch (Exception e) {
            log.error("Couldn't remove channel cover picture check the S3 configs", e);
        }
        //FeedGenerator.deleteMultipleFeeds(idMedia, 0l);
        //addUserActChangeLog(idOwner, idMedia, UserActivitiesLogEnum.REMOVED_VIDEO.name(), 1); //1 - Video related type

        return getDbForUpd().removeMediaByOwner(idOwner, idMedia);
    }

    public void followByChannel(DcChannelsEntity entity, Long idUser){
        RMemoryAPI.getInstance()
                .pushSetElemToMemory(Constants.FOLLOWS + "channels:" + idUser, entity.idChannel + "");

        RMemoryAPI.getInstance()
                .pushSetElemToMemory(Constants.CHANNEL_KEY + "users:" + entity.idChannel, idUser + "");
        long uCount = RMemoryAPI.getInstance()
                .checkSetElemCount(Constants.CHANNEL_KEY + "users:" + entity.idChannel);
        RMemoryAPI.getInstance()
                .pushHashToMemory(Constants.CHANNEL_KEY + entity.idChannel, "ucount", uCount+"");
    }

    public void unFollowByChannel(DcChannelsEntity entity, Long idUser){
        RMemoryAPI.getInstance()
                .delFromSetElem(Constants.FOLLOWS+"channels:"+idUser, entity.idChannel+"");

        RMemoryAPI.getInstance()
                .delFromSetElem(Constants.CHANNEL_KEY + "users:" + entity.idChannel, idUser + "");
        long uCount = RMemoryAPI.getInstance()
                .checkSetElemCount(Constants.CHANNEL_KEY + "users:" + entity.idChannel);
        RMemoryAPI.getInstance()
                .pushHashToMemory(Constants.CHANNEL_KEY + entity.idChannel, "ucount", uCount+"");
    }

    public void addUserActChangeLog(Long idUser, Long idContent, String act_type, int type){
        Long ID = RMemoryAPI.getInstance().currentTimeMillis()/1000;

        ChangeLogs logs = new ChangeLogs();
        logs.setId(ID);
        logs.setIdContent(idContent);
        logs.setIdUser(idUser);
        logs.setActivity_type(act_type);
        logs.setType(type);

        RMemoryAPI.getInstance().pushLSetElemToMemory(Constants.USER_KEY + "chngids:" + idUser, ID, logs.toString());
    }


    public EnumSQLErrors removeTheMediaFromChannel(long idMedia, final long idChannel, int props) {
        EnumSQLErrors error = getDbForChannel().removeMediaFromChannel(idMedia, idChannel);
        if (error == EnumSQLErrors.OK) {
            Thread thread = new Thread() {
                public void run() {
                    updateChannelRating(idChannel);
                }
            };
            thread.setName("thread-rating-channel-" + System.currentTimeMillis());
            thread.start();

        }
        return error;
    }

    public void updMediaDeviceParam(final long idMedia, final long idUser, final int type, final String agent, final String data) {

        Thread thread = new Thread() {
            public void run() {
                int method = 2;
                if (agent.contains("Apache-HttpClient/UNAVAILABLE")) {
                    method = 1;
                }
                EnumSQLErrors error = getDbForUpd().updMediaDeviceParam(idMedia, idUser, type, method, data);
            }
        };
        thread.setName("thread-visitor-stat-" + System.currentTimeMillis());
        thread.start();
    }

    public boolean leaveTheChannel(long id_user, long idChannel) {
        return getDbForChannel().leaveTheChannel(id_user, idChannel);
    }

    public boolean amIjoinedToChannel(long id_user, long idChannel) {
        if (id_user == 0) {
            return false;
        } else {
            String owner = RMemoryAPI.getInstance().pullHashFromMemory(Constants.CHANNEL_KEY + idChannel, "owner");
            return owner!=null && owner.equals(String.valueOf(id_user));
        }
    }

    public boolean amIFollowChannel(long id_user, long idChannel) {
        if (id_user == 0) {
            return false;
        } else {
            return RMemoryAPI.getInstance().pullIfSetElem(Constants.FOLLOWS+"channels:"+id_user, idChannel+"");
        }
    }

    public Set<String> getFirstThreeFollowsAva(long idUser){
        Set<String> avas = new HashSet<>();
        Set<String> follows = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.FOLLOWS+idUser);
        int i =0;
        Iterator<String> flwiter = follows.iterator();
        while (flwiter.hasNext() && i<3) {
            String idu = flwiter.next();
            i++;
            DcUsersEntity user = getDbForQuery().getUserInfoByID(Long.valueOf(idu));
            avas.add(Constants.STATIC_URL + user.idUser + "/image" + user.avatar + "S.jpg");
        }

        return avas;
    }

    public Set<String> getFirstThreeFollowersAva(long idUser){
        Set<String> avas = new HashSet<>();
        Set<String> follows = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.FOLLOWERS+idUser);
        int i =0;
        Iterator<String> flwiter = follows.iterator();
        while (flwiter.hasNext() && i<3) {
            String idu = flwiter.next();
            i++;
            DcUsersEntity user = getDbForQuery().getUserInfoByID(Long.valueOf(idu));
            avas.add(Constants.STATIC_URL + user.idUser + "/image" + user.avatar + "S.jpg");
        }

        return avas;
    }

    public void updateChannelRating(long idChannel) {
        if (idChannel != 0) {
            List<String> mList = RMemoryAPI.getInstance().pullSortedListElem(Constants.CHANNEL_KEY + "videos:" +
                    idChannel, Constants.MEDIA_KEY + "*->detail", 0, 100, Constants.MEDIA_KEY + "*->id_media");

            if (mList != null) {
                Gson gson = new Gson();
                List<DcMediaEntity> entity = gson.fromJson(mList.toString(),
                        new TypeToken<List<DcMediaEntity>>() {
                        }.getType());
                double summ = 0.0;
                /*for (DcMediaEntity m : entity) {
                    if ((m.getLiked() != 0 || m.getDisliked() != 0) && m.getProgress() == 0 && m.getIdChannel() == idChannel) {
                        summ = summ + (((m.getLiked() + 1.9208) / (m.getLiked() + m.getDisliked()) -
                                1.96 * Math.sqrt((m.getLiked() * m.getDisliked()) / (m.getLiked() + m.getDisliked()) + 0.9604) /
                                        (m.getLiked() + m.getDisliked())) / (1 + 3.8416 / (m.getLiked() + m.getDisliked())));
                    }
                }*/ //todo
                RMemoryAPI.getInstance()
                        .pushHashToMemory(Constants.CHANNEL_KEY + idChannel, "rating", summ + "");
            }
        }


    }

    public List<UserShortInfo> showMyFollowerIntersection(long idUser, long off, long limit) {
        return getDbForQuery().extractPublicIntersectUserFollowers(idUser, off, limit, lang);
    }

    public List<UserShortInfo> showMyFollowers(long idUser, long idSource, long off, long limit) {
        return getDbForQuery().extractPublicUserFollowers(idUser, idSource, off, limit, lang);
    }

    public List<UserShortInfo> showMyFollowersByName(String name, Long idUser, int off, int limit) {
        return getDbForQuery().searchMyUserFollowersByName(name, idUser, off, limit);
    }

    public List<UserShortInfo> showMyFollowsByName(String name, Long idUser, int off, int limit) {
        return getDbForQuery().searchMyUserFollowsByName(name, idUser, off, limit);
    }

    public List<UserShortInfo> showUserFollowsByName(String name, Long src, Long dest, int off, int limit) {
        return getDbForQuery().searchUserFollowsByName(name, src, dest, "follows", off, limit);
    }

    public List<UserShortInfo> showUserFollowersByName(String name, Long src, Long dest, int off, int limit) {
        return getDbForQuery().searchUserFollowsByName(name, src, dest, "followers", off, limit);
    }

    public List<UserShortInfo> showAllUsersByName(String name, Long idUser, int off, int limit) {
        return getDbForQuery().searchAllUsersByName(name, idUser, off, limit);
    }

    public List<UserShortInfo> showMyInterFollowingByName(String name, long idUser, int off, int limit) {
        log.info("Room chat search user vals {}", name);
        return getDbForQuery().searchMyUserInterFollowsByName(name, idUser, off, limit);
    }

    public List<UserShortInfo> showMyFollowing(long idUser, long idSource, long off, long limit) {
        return getDbForQuery().extractPublicUserFollowing(idUser, idSource, off, limit, lang);
    }

    public EnumSQLErrors publishFeedback(long idUser, String text) {
        return getDbForUpd().saveFeedback(idUser, text);
    }

    public EnumErrors publishContacUs(String name, String email, String phone, String country, String text) {
        Notificator mailInfo = new Notificator();
        mailInfo.setEmail(email);
        mailInfo.setType(9);
        mailInfo.setUsername(name);
        mailInfo.setPhone(phone);
        mailInfo.setCountry(country);

        mailInfo.setDescr(text);
//        MailNotifier notifier = new MailNotifier();
//        notifier.sendMailBoxQueue(mailInfo);

        return EnumErrors.NO_ERRORS;

    }

    public EnumSQLErrors publishBugTracker(String title, String text) {
        return getDbForUpd().saveBugTrack(title, text);
    }

    private Set<Tuple> pullSortedSetFromRedis(String key, int start, int count){
        Jedis jedis = RedisAPI.getInstance();
        Set<Tuple> list = null;

        try {
            list = jedis.zrangeWithScores(key, start, count);
        } catch (JedisConnectionException var10) {
            if(null != jedis) {
                RedisAPI.putBrokenBack(jedis);
            }
        } finally {
            if(null != jedis) {
                RedisAPI.putBack(jedis);
            }

        }

        return list;
    }

}
