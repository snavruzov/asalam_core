package com.dgtz.api.contents;

import com.brocast.riak.api.beans.*;
import com.brocast.riak.api.dao.RiakAPI;
import com.brocast.riak.api.dao.RiakTP;
import com.brocast.riak.api.factory.IRiakQueryFactory;
import com.brocast.riak.api.factory.IRiakSaveFactory;
import com.brocast.riak.api.factory.RiakQueryFactory;
import com.brocast.riak.api.factory.RiakSaveFactory;
import com.dgtz.api.beans.*;
import com.dgtz.api.beans.MediaInfo;
import com.dgtz.api.beans.MediaListInfo;
import com.dgtz.api.beans.UserPublicInfo;
import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.enums.UserActivitiesLogEnum;
import com.dgtz.api.feature.*;
import com.dgtz.api.security.LocationDefinition;
import com.dgtz.api.utils.*;
import com.dgtz.db.api.beans.DcDebateEntity;
import com.dgtz.db.api.beans.MediaMappingStatInfo;
import com.dgtz.db.api.beans.ScreenRotation;
import com.dgtz.db.api.domain.*;
import com.dgtz.db.api.domain.MediaNewsStatInfo;
import com.dgtz.db.api.enums.EnumAggregations;
import com.dgtz.db.api.enums.EnumSQLErrors;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/23/13
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class MediaShelf extends DBConnector {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MediaShelf.class);
    private static final int LIMIT = 12;
    private static final String COMPRESS_THUMB_URL = Constants.ENCODING_URL + "compresser/algo/tools/thumbnailer?idm=%s";
    private static final String[] DEMO_VIDEO =
            {"default/hls_vod/demo_event.m3u8"
            ,"default/hls_vod/demo_event1.m3u8"
            ,"default/hls_vod/demo_event2.m3u8"};
    private String lang;

    public MediaShelf() {
        super();
    }

    public MediaShelf(String lang) {
        super();
        this.lang = LanguageDetector.detectLang(lang);
    }

    public List<MediaNewsStatInfo> extractMediaListByCategory(int idCategory, long offset, int limit, int sortType, String sorting) {
        return getDbForQuery().castMediaByIdCategory(idCategory, sortType, offset, limit, lang);

    }

    public List<MediaNewsStatInfo> extractMediaListByLive(long offset, int limit, String sortColumn, String sorting) {
        return getDbForQuery().castMediaByLive(offset, limit);

    }

    public DcMediaEntity retrieveMediaByIdValue(long idMedia) {
        DcMediaEntity entity = getDbForQuery().getMediaById(idMedia);
        return entity;
    }

    protected MediaListInfo extractMediaById(long idMedia) {
        DcMediaEntity entity = getDbForQuery().getMediaById(idMedia);

        MediaListInfo listInfo = null;
        if (entity != null) {
            listInfo = new MediaListInfo();
            listInfo.setIdMedia(idMedia);
            listInfo.setTitle(entity.getTitle());
          //  listInfo.setUrl(entity.getUrl());
            //listInfo.setDuration(entity.getDuration());
            listInfo.setDateadded(entity.getDateadded());
            //listInfo.setLiked(entity.getLiked());
            //listInfo.setDisliked(entity.getDisliked());
            //listInfo.setTags(entity.getTags());
            listInfo.setIdChannel(entity.getIdChannel());
            listInfo.setIdUser(entity.getIdUser());

            DcUsersEntity user = getDbForQuery().getUserProfileInfoById(entity.getIdUser());
            listInfo.setUsername(user.username);

            listInfo.setLocation(entity.getLocation());
            listInfo.setDescription(entity.getDescription());
            listInfo.setProgress(entity.getProgress());
            listInfo.setAlt_user("");
            listInfo.setIdCategory(entity.getIdCategory());
            //listInfo.setProps(entity.getProps());
            //listInfo.setAmount(entity.getAmount());
        }


        return listInfo;
    }

    public List<PublicChannelsEntity> extractChannelsInfo(int idAggr, Long idUser, long off, long limit) {
        List<PublicChannelsEntity> entities = getDbForChannel()
                .extractChannelsInfo(EnumAggregations.getEnumValByID(idAggr), idUser, off, limit);
        return entities;
    }

    public Set<TagInfo> extractMostPopularTags(Long idUser, Integer rows) {
        Set<TagInfo> tags = new LinkedHashSet<>(rows+5);

        try{
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            Set<String> tgs = queryFactory.queryTagDataByTOP(rows);
            tgs.forEach(tg -> {
                boolean reflw = RMemoryAPI.getInstance().pullIfSetElem(Constants.FOLLOWS + "tags:" + idUser, tg);
                Long tgFlwNum = RMemoryAPI.getInstance().checkSetElemCount(Constants.MEDIA_KEY + "tags:followers:"+tg);
                TagInfo info = new TagInfo();
                info.flwnum = tgFlwNum;
                info.videonum = RMemoryAPI.getInstance().checkLSetAllElemCount(Constants.MEDIA_KEY + "tag:" + tg);
                info.reFollow = reflw;
                info.tag = tg;
                tags.add(info);
            });

        } catch (Exception e){
            e.printStackTrace();
        }

        return tags;
    }

    public List<UserPublicInfo> extractMostPopularUsers(DcUsersEntity usr, Integer rows) {
        List<UserPublicInfo> users = new ArrayList<>();

        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            Set<String> uids = queryFactory.queryUsersByTOP(rows);
            uids.forEach(uid -> {
                DcUsersEntity user = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + uid, "detail", DcUsersEntity.class);
                if (user != null) {
                    UserPublicInfo info = new UserPublicInfo();
                    info.setIdUser(Long.valueOf(uid));
                    info.setUsername(user.username);
                    info.setFullName(user.fullname);
                    info.setAvatar(user.avatar);

                    String ava = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + info.getIdUser(), "avatar");
                    info.setAvatar(Constants.STATIC_URL + info.getIdUser() + "/image" + ava + ".jpg");

                    String verified = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.USER_KEY + info.getIdUser(), "verified");
                    info.setVerified(verified == null ? false : Boolean.valueOf(verified));

                    info.setLocation(user.city + " " + user.country);

                    info.setAbout(user.about);
                    Boolean follow = RMemoryAPI.getInstance()
                            .pullIfSetElem(Constants.FOLLOWS + usr.idUser, uid);
                    info.setReFollow(follow);
                    info.setFlwnum(new UsersShelf().userProfileStatisticInfo(info.getIdUser(), 0, false)); //Followers
                    info.setFcount(new UsersShelf().userProfileStatisticInfo(info.getIdUser(), 1, false)); //Follows
                    info.setMcount(new UsersShelf().userProfileStatisticInfo(info.getIdUser(), 3, false));
                    info.setChcount(0l);//usersShelf.userProfileStatisticInfo((Long) obj[0], 2, false);

                    users.add(info);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<MediaNewsStatInfo> extractMediaListByTag(String tagName, int limit, int off) {

        List<MediaNewsStatInfo> list = new ArrayList<>();
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            List<DcMediaEntity> mlist = queryFactory.queryMediaDataByTags(tagName, off, limit);
            mlist.forEach(entity -> list.add(new MediaNewsStatInfo(entity)));

        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public List<MediaNewsStatInfo> extractMediaListByPlace(String place, int limit, int off) {

        List<MediaNewsStatInfo> list = new ArrayList<>();
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            List<DcMediaEntity> mlist = queryFactory.queryMediaDataByPlace(place, off, limit);
            mlist.forEach(entity -> list.add(new MediaNewsStatInfo(entity)));

        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public Set<TagInfo> extractTagListByName(String name, Long idUser, int limit, int off) {

        Set<TagInfo> tags = new HashSet<>(limit+5);

        try{
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            Set<String> tgs = queryFactory.queryTagDataByName(name, off, limit);
            tgs.forEach(tg -> {
                boolean reflw = RMemoryAPI.getInstance().pullIfSetElem(Constants.FOLLOWS + "tags:" + idUser, tg);
                Long tgFlwNum = RMemoryAPI.getInstance().checkSetElemCount(Constants.MEDIA_KEY + "tags:followers:"+tg);
                TagInfo info = new TagInfo();
                info.flwnum = tgFlwNum;
                info.videonum = RMemoryAPI.getInstance().checkLSetAllElemCount(Constants.MEDIA_KEY + "tag:" + tg);
                info.reFollow = reflw;
                info.tag = tg;
                tags.add(info);
            });

        } catch (Exception e){
            log.error("Error in tag list", e);
        }

        return tags;
    }

    public List<DcChannelsEntity> castChannelsByChannelName(String chName, int limit, int off) {
        return getDbForChannel().castMediaByChannelName(chName, off, limit);
    }

    public EnumErrors updateMediaStatus(final Long idMedia, Short progress) {

        EnumErrors errors = EnumErrors.UNKNOWN_ERROR;
        EnumSQLErrors sqlErrors = getDbForUpd().updateMediaStatus(idMedia, progress);

        if (sqlErrors == EnumSQLErrors.OK) {
            errors = EnumErrors.NO_ERRORS;
        }
        return errors;
    }

    public MediaListInfo extractMediaByIdMedia(long idMedia) {

        DcMediaEntity objects = getDbForQuery().castMediaById(idMedia);
        MediaListInfo listInfo = null;
        if (objects != null && objects.getProgress() < 2) {

            listInfo = new MediaListInfo();

            String username = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.USER_KEY + objects.getIdUser(), "username");
            String avatar = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.USER_KEY + objects.getIdUser(), "avatar");
            String verified = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.USER_KEY + objects.getIdUser(), "verified");
            String liked = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.MEDIA_KEY + objects.getIdMedia(), "liked");
            String vcount = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.MEDIA_KEY + objects.getIdMedia(), "vcount");

            listInfo.setVerified(verified == null ? false : Boolean.valueOf(verified));
            listInfo.setUsername(username);
            listInfo.setAvatar(Constants.STATIC_URL + objects.getIdUser() + "/image" + avatar + ".jpg");

            listInfo.setIdMedia(objects.getIdMedia());
            listInfo.setTitle(objects.getTitle());
            listInfo.setUrl(objects.liveProps.mp4_url);
            listInfo.setDuration(objects.getDuration());
            listInfo.setDateadded(objects.getDateadded());
            listInfo.setLiked(Long.valueOf(liked));
            listInfo.setAmount(Long.valueOf(vcount));
            listInfo.setTags(objects.getTags());
            listInfo.setLive(objects.method.equals("live"));
            listInfo.setIdChannel(objects.getIdChannel());
            listInfo.setIdUser(objects.getIdUser());
            listInfo.setMethod(objects.getMethod());

            listInfo.setLocation(objects.location);

            String ratio = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + idMedia, "ratio");
            listInfo.setRatio(ratio == null ? "4:3" : ratio);
            listInfo.setProgress(objects.getProgress());
        }
        return listInfo;
    }

    public EnumErrors saveMediaDeviceVendor(long idMedia, String dname) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        EnumSQLErrors dbErr = getDbForUpd().updMediaDeviceParam(idMedia, dname);
        if (dbErr != EnumSQLErrors.OK)
        {
            errors = EnumErrors.UNKNOWN_ERROR;
        }

        return errors;

    }

    public DcChannelsEntity getChannelDataByID(long idChannel){

        DcChannelsEntity ch = new DcChannelsEntity();
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            ch = queryFactory.queryChannelDataByID(idChannel);
        } catch (Exception e){
            log.error("Something wrong in getting channel by ud", e);
        }

        return ch;
    }

    public MediaVIewInfo extractMediaViewActivityByIdMedia(long idMedia, long idViewer) {

        boolean isFollowed = false;
        long numberOfFollowers = 0;
        MediaVIewInfo listInfo = null;

        try {
            DcMediaEntity objects = getDbForQuery().castMediaById(idMedia);

            if (objects != null && objects.getProgress() < 2) {

                LiveProps props = new LiveProps();
                listInfo = new MediaVIewInfo();

                if(objects.idChannel!=0) {
                    DcChannelsEntity channel = getChannelDataByID(objects.idChannel);
                    boolean followed = RMemoryAPI.getInstance()
                            .pullIfSetElem(Constants.CHANNEL_KEY + "users:" + objects.idChannel, idViewer+"");
                    if(channel.privacy == 1){
                        listInfo.access = followed;
                    }
                    String chTitle = RMemoryAPI.getInstance()
                            .pullHashFromMemory(Constants.CHANNEL_KEY + objects.idChannel, "title");
                    listInfo.setChannelTitle(chTitle);
                }

                if(objects.method.equals("event") || objects.method.equals("live")) {
                    props.hls_url = objects.getLiveProps().hls_url;
                    props.mp4_url = objects.getLiveProps().mp4_url;
                    props.rtmp_url = RMemoryAPI.getInstance().pullHashFromMemory(Constants.LIVE_KEY + idMedia, "rtmp_liveurl");
                } else {
                    props.hls_url = Constants.encryptAmazonURL(objects.idUser, idMedia, "", "hls", Constants.VIDEO_URL);
                    props.mp4_url = Constants.encryptAmazonURL(objects.idUser, idMedia, "_hi.mp4", "v", Constants.VIDEO_URL);
                    props.rtmp_url = RMemoryAPI.getInstance().pullHashFromMemory(Constants.LIVE_KEY + idMedia, "rtmp_liveurl");
                }

                List<DcDebateEntity> debateEntity = extractVideosInDebate(idMedia);

                List<String> entities =
                        RMemoryAPI.getInstance()
                                .pullListElemFromMemory(Constants.MEDIA_KEY + "properties:rotatime:" + idMedia, 0, -1);
                List<ScreenRotation> lrotation = new Gson().fromJson(entities.toString(),
                        new TypeToken<List<ScreenRotation>>() {
                        }.getType());

                String username = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + objects.getIdUser(), "username");
                String avatar = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + objects.getIdUser(), "avatar");
                String verified = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.USER_KEY + objects.getIdUser(), "verified");
                String liked = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + objects.getIdMedia(), "liked");
                String ccount = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + objects.getIdMedia(), "ccount");
                String vcount = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + objects.getIdMedia(), "vcount");
                String lvcount = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + objects.getIdMedia(), "lvcount");
                long event_joiners = RMemoryAPI.getInstance()
                        .checkSetElemCount(Constants.MEDIA_KEY + "event_viewers:"+objects.getIdMedia());

                if(RMemoryAPI.getInstance().pullIfSetElem("dc_users:comment:blocked:users:"+objects.idUser
                        , idViewer + "")){
                    listInfo.setBlocked(true);
                } else {
                    listInfo.setBlocked(false);
                }

                /*check for voice message allowed user*/
                if(RMemoryAPI.getInstance().pullIfSetElem("dc_users:comment:voice:users:"+objects.getIdUser()
                        , idViewer + "") || objects.getIdUser()==idViewer){
                    listInfo.setVoice(true);
                } else {
                    listInfo.setVoice(false);
                }

                MediaStatistics statistics = new MediaStatistics();
                statistics.likes = Long.valueOf(liked);
                statistics.comments = Long.valueOf(ccount);
                statistics.record_viewers = Long.valueOf(vcount);
                statistics.live_viewers = Long.valueOf(lvcount ==null?"0":lvcount);
                statistics.event_joiners = event_joiners;

                listInfo.verified = (verified == null ? false : Boolean.valueOf(verified));
                listInfo.username = (username);
                listInfo.avatar = (Constants.STATIC_URL + objects.getIdUser() + "/image" + avatar + ".jpg");

                listInfo.currentTime = System.currentTimeMillis()+"";
                listInfo.idMedia = (objects.getIdMedia());
                listInfo.title = (objects.getTitle());
                listInfo.setLiveProps(props);
                listInfo.duration = (objects.getDuration());
                listInfo.dateadded = (objects.getDateadded());
                listInfo.setDebaters(debateEntity);
                listInfo.setRotations(lrotation);

                listInfo.setStats(statistics);

                listInfo.tags = objects.getTags();
                listInfo.idChannel = objects.getIdChannel();
                listInfo.idUser = (objects.getIdUser());
                listInfo.method = (objects.getMethod());
                listInfo.latlong = (objects.coordinate);
                listInfo.thumb = (Constants.encryptAmazonURL(objects.idUser, idMedia, "jpg", "thumb", Constants.STATIC_URL));
                listInfo.storyboard =
                        Constants.encryptAmazonURL(objects.idUser, idMedia, "jpg", "tail", Constants.STATIC_URL);
                listInfo.description = (objects.getDescription());
                listInfo.progress = (objects.getProgress());
                listInfo.idCategory = (objects.getIdCategory());

                String evnt_time = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "evnt_time");
                listInfo.time_start = (evnt_time==null?"":evnt_time);
                listInfo.rating = (objects.rating);
                listInfo.location = (RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "location", "en"));

                listInfo.ratio = (objects.ratio);

                if (idViewer != 0) {
                    isFollowed = RMemoryAPI.getInstance().pullIfSetElem(Constants.FOLLOWS + idViewer, objects.idUser + "");
                }
                if(listInfo.getIdUser()!=0) {
                    numberOfFollowers = new UsersShelf().userProfileStatisticInfo(listInfo.getIdUser(), 0, false);
                }

                listInfo.fcount = (numberOfFollowers);
                listInfo.followed = (isFollowed);
            }
        } catch (Exception e) {
            log.error("ERROR IN MAIN API: ", e);
            listInfo = null;
        }

        return listInfo;
    }

    public List<DcMediaEntity> extractMediaListByLocation(int idTag, int from, int to, int limit, String sortColumn, String sorting) {
        return null;
    }

    public List<MediaNewsStatInfo> extractMediaListByStat(int idEnum, long offset, int limit, DcUsersEntity user, String addr) {

        Set<String> langs = new HashSet<>();
        if(false){ //temporary
            String code = getLanguageCodeByLocation(addr, null);
            langs.add(Constants.MEDIA_KEY+"ids:"+code);
        } else {
            Set<String> usrLangs = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.USER_KEY + "extlang:" + 0);

            usrLangs.forEach(usrLng -> langs.add(Constants.MEDIA_KEY+"ids:"+usrLng));

            log.debug("user {} langs {}", user.idUser, langs.toString());
        }

        return getDbForQuery().castMediaByStats(EnumAggregations.getEnumValByID(idEnum), lang, langs, offset, limit);
    }

    public List<MediaPublicInfo> extractMediaListByRating(int offset, int limit, DcUsersEntity user, String addr) {
        return getDbForQuery().extractUsersContentByTOP(user.idUser, offset, limit);
    }

    public List<UserShortInfo> extractLiveViewrsData(long idMedia, long off, long limit) {
        return getDbForQuery().extractPublicViewersList(idMedia, off, limit);
    }

    public List<MediaMappingStatInfo> extractMapMediaList() {
        return getDbForQuery().castMediaByLatLng();
    }

    public List<MediaFeaturedBean> extractFeaturedMediaList() {
        List<MediaFeaturedBean> entities = new ArrayList<>();

        try {
            List<DcMediaEntity> list = getDbForQuery().castMediaByFeatured();

            list.forEach(o ->
                    {
                        LiveProps props = new LiveProps();
                        MediaFeaturedBean listInfo = new MediaFeaturedBean();
                        Long idMedia = o.idMedia;
                        Long idUser = o.idUser;
                        props.hls_url = Constants.encryptAmazonURL(o.idUser, o.idMedia, "", "hls", Constants.VIDEO_URL);
                        props.mp4_url = Constants.encryptAmazonURL(o.idUser, o.idMedia, "_hi.mp4", "v", Constants.VIDEO_URL);
                        props.rtmp_url = RMemoryAPI.getInstance().pullHashFromMemory(Constants.LIVE_KEY + o.idMedia, "rtmp_liveurl");


                        List<DcDebateEntity> debateEntity = extractVideosInDebate(idMedia);

                        List<String> rlist =
                                RMemoryAPI.getInstance()
                                        .pullListElemFromMemory(Constants.MEDIA_KEY + "properties:rotatime:" + idMedia, 0, -1);
                        List<ScreenRotation> lrotation = new Gson().fromJson(rlist.toString(),
                                new TypeToken<List<ScreenRotation>>() {
                                }.getType());

                        String username = RMemoryAPI.getInstance()
                                .pullHashFromMemory(Constants.USER_KEY + idUser, "username");
                        String avatar = RMemoryAPI.getInstance()
                                .pullHashFromMemory(Constants.USER_KEY + idUser, "avatar");
                        String liked = RMemoryAPI.getInstance()
                                .pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "liked");
                        String ccount = RMemoryAPI.getInstance()
                                .pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "ccount");
                        String vcount = RMemoryAPI.getInstance()
                                .pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "vcount");
                        String lvcount = RMemoryAPI.getInstance()
                                .pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "lvcount");


                        MediaStatistics statistics = new MediaStatistics();
                        statistics.likes = Long.valueOf(liked);
                        statistics.comments = Long.valueOf(ccount);
                        statistics.record_viewers = Long.valueOf(vcount);
                        statistics.live_viewers = Long.valueOf(lvcount == null ? "0" : lvcount);

                        listInfo.username = (username);
                        listInfo.avatar = (Constants.STATIC_URL + idUser + "/image" + avatar + ".jpg");

                        listInfo.idMedia = idMedia;
                        listInfo.title = o.getTitle();
                        listInfo.setLiveProps(props);
                        listInfo.duration = (o.getDuration());
                        listInfo.dateadded = (o.getDateadded());
                        listInfo.setDebaters(debateEntity);
                        listInfo.setRotations(lrotation);

                        listInfo.setStats(statistics);

                        listInfo.idUser = (idUser);
                        listInfo.method = (o.getMethod());
                        listInfo.thumb = (Constants.encryptAmazonURL(idUser, idMedia, "jpg", "thumb", Constants.STATIC_URL));

                        listInfo.location = (RMemoryAPI.getInstance()
                                .pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "location", "en"));

                        listInfo.ratio = (o.ratio);

                        entities.add(listInfo);
                    }
            );

        } catch (Exception e) {
            log.error("ERROR IN MAIN API: ", e);
        }

        return entities;

    }

    public String checkMediaStatus(long idMedia) {

        return getDbForQuery().checkMediaCompressStatus(idMedia);
    }

    public String checkMediaCurrentStatus(long idMedia) throws Exception {

        String pr = RMemoryAPI.getInstance().pullElemFromMemory(Constants.MEDIA_KEY + "encstate:" + idMedia);

        return pr == null ? "0" : pr;
    }

    public int checkMediaEncStatus(long idMedia){
        String pr = RMemoryAPI.getInstance().pullElemFromMemory(Constants.MEDIA_KEY + "encstate:" + idMedia);
        return pr == null ? 0 : Integer.valueOf(pr);
    }



    public void insertFreshMediaContent(DcMediaEntity mediaEntity) {
        AbuseFilter f = new AbuseFilter();
        try {
            String title = f.filter(mediaEntity.getTitle());
            String descr = f.filter(mediaEntity.getDescription());
            mediaEntity.setTitle(title);
            mediaEntity.setDescription(descr);
        } catch (UnsupportedEncodingException e) {
            log.error("ERROR IN MAIN API ", e);
        }
        getDbForUpd().saveMediaInfo(mediaEntity, lang);
    }

    public EnumSQLErrors insertMediaCounterParam(long idMedia, EnumAggregations type) {
        getDbForUpd().addCounterMediaStat(idMedia, type);
        return EnumSQLErrors.OK;
    }

    public String modifyLocation(String point, Long idMedia) throws Exception {

        double lat = 0.0;
        double lng = 0.0;

        String location = point.trim();
        String[] locs = location.split(" ");

        if (locs.length == 2) {
            location = locs[1].trim() + "," + locs[0].trim();
            lat = Double.valueOf(locs[1].trim());
            lng = Double.valueOf(locs[0].trim());
        } else {
            location = "";
        }

        if (lat != 0.0) {
            log.debug("START GPS");
            HttpResponse<JsonNode> body = Unirest.get(Constants.GEO_URL+"address/?latlng="+lat+","+lng).asJson();
            JSONObject json = body.getBody().getObject();
            log.info("Client live location {}, {}", json.getString("city"), json.getString("country"));

            RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idMedia, "latlng", location);
            //LocationDefinition mapFactory = new LocationDefinition(lat, lng, "en");

            String state = json.getString("state");
            String country = json.getString("country");
            String city = json.getString("city");
            String full_address = json.getString("formatted_address");

            if(state.isEmpty() || city.equals(state)){
                state = ",";
            } else {
                state = ","+state+",";
            }

            if(city.isEmpty()){
                location = "";
            } else {
                location = city + state + country;
                RMemoryAPI.getInstance()
                        .pushHashToMemory(Constants.MEDIA_KEY + idMedia, "full_location", full_address);
            }

            log.debug("LOCATION: {}", location);
        }
        return location;

    }

    public String getLanguageCodeByLocation(String addr, String latlng) {
        String code = "en";
        String country_code = null;

        /*if (addr!=null && !addr.isEmpty()) {
            country_code = IP2LocationDefiner.detectCountryCodeByIPLocation(addr);
            log.debug("IP based detection {}", country_code);
        }
        else if (latlng != null && !latlng.isEmpty() && !latlng.equals("0.0 0.0")){
            double lat = 0.0;
            double lng = 0.0;

            String location = latlng.trim();
            String[] locs = location.split(" ");

            if (locs.length == 2) {
                lat = Double.valueOf(locs[1].trim());
                lng = Double.valueOf(locs[0].trim());
            }

            if (lat != 0.0) {
                country_code = LocationDefinition.shortCountryDefinition(lat, lng);
            }
            log.debug("Google GPS based {} ", country_code);
        }

        if(country_code!=null && !country_code.isEmpty()) {
            code = getDbForQuery().getCountryLanguages(country_code);
        } else {
            log.error("Country code is NULL {}", code);
        }*/

        log.debug("language code {}", code);

        return code;
    }



    protected void updateGeoLocation(String altLocation, Long idMedia) {
        log.debug("ALT LOCATION: {}", altLocation);
        getDbForUpd().updateLocation(altLocation, idMedia);

    }


    public Long eventDefaultVideo(Long idUser){
        Long idLive = new LiveShelf().extractUniqueIdForLive();
        Constants.encryptAmazonURL(idUser, idLive, "_hi.mp4", "defv", "");
        Constants.encryptAmazonURL(idUser, idLive, "", "defhls", "");
        RMemoryAPI.getInstance()
                .pushHashToMemory(Constants.MEDIA_KEY + idLive, "progress", new MediaStatus(0).toString());

        return idLive;

    }

    public void updateContentInfo(DcMediaEntity entity){
        DcMediaEntity e = RMemoryAPI.getInstance()
                .pullHashFromMemory(Constants.MEDIA_KEY + entity.idMedia, "detail", DcMediaEntity.class);

        e.title = entity.title;
        e.description = entity.description;
        e.idChannel = entity.idChannel;
        e.tags = entity.tags;
        e.method = entity.method;

        getDbForUpd().updateContentInfo(e);

    }

    public void updateLiveGeoLocation(Long idMedia,  String location){
        try {
            String geo_local = "";
            if(location!=null) {
                geo_local = modifyLocation(location, idMedia);

                if(geo_local!=null && !geo_local.isEmpty()) {
                    RiakTP transport = RiakAPI.getInstance();

                    IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);

                    DcMediaEntity e = queryFactory.queryMediaDataByID(idMedia);
                    e.location = geo_local;

                    IRiakSaveFactory saveFactory = new RiakSaveFactory(transport);
                    saveFactory.updMediaContent(e);

                    RMemoryAPI.getInstance()
                            .pushHashToMemory(Constants.MEDIA_KEY + idMedia, "location", geo_local);
                }
            }

        }catch (Exception e){
            log.error("Error ion Main Core API", e);
        }

    }


    public void updateContentUrls(DcMediaEntity entity) {
        DcMediaEntity e = RMemoryAPI.getInstance()
                .pullHashFromMemory(Constants.MEDIA_KEY + entity.idMedia, "detail", DcMediaEntity.class);

        e.liveProps.rtmp_url = entity.liveProps.rtmp_url;
        e.liveProps.hls_url = entity.liveProps.hls_url;
        e.liveProps.mp4_url = entity.liveProps.mp4_url;

        getDbForUpd().updateContentInfo(e);
    }

    public void updateDebateInfo(DcMediaEntity entity){
        DcMediaEntity e = RMemoryAPI.getInstance()
                .pullHashFromMemory(Constants.MEDIA_KEY + entity.idMedia, "detail", DcMediaEntity.class);

        e.liveProps.debate = entity.liveProps.debate;
        e.liveProps.position = entity.liveProps.position;

        getDbForUpd().updateContentInfo(e);
    }

    public void  updateEventInfo(DcMediaEntity entity){
        DcMediaEntity e = RMemoryAPI.getInstance()
                .pullHashFromMemory(Constants.MEDIA_KEY + entity.idMedia, "detail", DcMediaEntity.class);
        RMemoryAPI.getInstance().delFromSetElem(Constants.MEDIA_KEY + "events", entity.idMedia + "");

        e.method = entity.method;
        e.liveProps.rtmp_url = entity.liveProps.rtmp_url;
        e.liveProps.hls_url = entity.liveProps.hls_url;

        getDbForUpd().updateContentInfo(e);
    }



    public EnumErrors saveVideoProperties(LiveMediaInfo info, String method) {

        log.debug("LOCATIONS: {}", info.getLocation());
        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {

            DcMediaEntity liveMediaEntity = new DcMediaEntity();
            LiveProps props = new LiveProps();
            if(info.getDefclip()!=-1){
                RMemoryAPI.getInstance()
                        .pushHashToMemory(Constants.MEDIA_KEY + info.getIdMedia(), "progress", new MediaStatus(0).toString());
                RMemoryAPI.getInstance().pushElemToMemory(Constants.MEDIA_KEY + "encstate:" + info.getIdMedia(), 3, "100");

                props.mp4_url = Constants.VIDEO_URL+DEMO_VIDEO[info.getDefclip()];
                props.hls_url = Constants.VIDEO_URL+DEMO_VIDEO[info.getDefclip()];

                String jpgURL = Constants.encryptAmazonURL(info.getIdUser(), info.getIdMedia(), "jpg", "thumb", "");
                String webpURL = Constants.encryptAmazonURL(info.getIdUser(), info.getIdMedia(), "webp", "thumb", "");

                AmazonS3Module s3Module = new AmazonS3Module();
                s3Module.copyImageFile("defaults/media.jpg", jpgURL);
                s3Module.copyImageFile("defaults/media.webp", webpURL);

            } else {
                props.mp4_url
                        = Constants.encryptAmazonURL(info.getIdUser(), info.getIdMedia(), "_hi.mp4", "v", Constants.VIDEO_URL);

                props.hls_url
                        = Constants.encryptAmazonURL(info.getIdUser(), info.getIdMedia(), "", "hls", Constants.VIDEO_URL);
            }

            String vstatus = RMemoryAPI.getInstance().pullElemFromMemory(Constants.MEDIA_KEY + "encstate:" + info.getIdMedia());
            log.debug("encstatus <><><><> {}",vstatus);
            liveMediaEntity.idMedia = (info.getIdMedia());
            liveMediaEntity.idUser = (info.getIdUser());
            liveMediaEntity.rating = 0L;
            liveMediaEntity.title = (info.getTitle());
            liveMediaEntity.description = (info.getDescription());
            liveMediaEntity.idCategory = (1); //1 - video, 2 - text
            liveMediaEntity.idChannel = (info.getIdChannel());
            liveMediaEntity.location = info.getLocation();
            liveMediaEntity.method = method;
            liveMediaEntity.dateadded = System.currentTimeMillis() + "";
            liveMediaEntity.progress = (vstatus==null || !vstatus.equals("100"))?1:0;
            if(info.getLatlong()!=null){
                String[] coords = info.getLatlong().split(" ");
                liveMediaEntity.coordinate = coords[1]+","+coords[0];
            }
            liveMediaEntity.lang = info.getLang();
            liveMediaEntity.setLiveProps(props);


            String duration = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + info.getIdMedia(), "duration");

            if (duration != null) {
                liveMediaEntity.duration = Integer.valueOf(duration);
            } else {
                liveMediaEntity.duration = 0;
            }

            Set<String> tagList = TagTokenizer.normilizeTag(info.getTags());
            log.debug("tags {}", tagList);
            liveMediaEntity.tags = tagList;

            EnumSQLErrors sqlErrors = getDbForUpd().saveContentInfo(liveMediaEntity, lang);
            if(sqlErrors!=EnumSQLErrors.OK){errors = EnumErrors.UNKNOWN_ERROR;}

            if(method.equals("event")){
                RMemoryAPI.getInstance()
                        .pushHashToMemory(Constants.MEDIA_KEY + info.getIdMedia(), "evnt_time", info.getEventTime());
                RMemoryAPI.getInstance()
                        .pushSetElemToMemory(Constants.MEDIA_KEY + "events", info.getIdMedia()+"");
            }
        }catch (Exception e){
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("Error while video saving ", e);
        }
        return errors;
    }

    public EnumErrors saveTextPostProperties(LiveMediaInfo info, String method) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {

            DcMediaEntity liveMediaEntity = new DcMediaEntity();

            liveMediaEntity.idMedia = (info.getIdMedia());
            liveMediaEntity.idUser = (info.getIdUser());
            liveMediaEntity.title = (info.getTitle());
            liveMediaEntity.description = (info.getDescription());
            liveMediaEntity.idCategory = (2); //1 - video, 2 - text
            liveMediaEntity.idChannel = (info.getIdChannel());
            liveMediaEntity.location = "";
            liveMediaEntity.method = method;
            liveMediaEntity.dateadded = RMemoryAPI.getInstance().currentTimeMillis() + "";
            liveMediaEntity.progress = (0);
            liveMediaEntity.coordinate = "";
            liveMediaEntity.lang = "en";
            liveMediaEntity.duration = 0;
            liveMediaEntity.tags = Collections.emptySet();

            EnumSQLErrors sqlErrors = getDbForUpd().saveTextContentInfo(liveMediaEntity, lang);
            if(sqlErrors!=EnumSQLErrors.OK){errors = EnumErrors.UNKNOWN_ERROR;}

        } catch (Exception e){
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("Error while text saving ", e);
        }
        return errors;
    }

    public boolean saveFreshMediaIntoMem(MediaProperties properties) {
        DcMediaEntity entity = new DcMediaEntity();
        entity.setIdUser(properties.getIdUser());
        entity.setIdMedia(properties.getIdMedia());
        entity.setTitle(properties.getTitle());
        //entity.setUrl(properties.get());
        return getDbForUpd().saveMediaInfoIntoMem(entity);
    }

    @Deprecated
    public EnumSQLErrors updateMediaContent(final DcMediaEntity entity) {
        EnumSQLErrors error = getDbForUpd().updateContentInfo(entity);
        if (error == EnumSQLErrors.OK) {
            Thread thread = new Thread() {
                public void run() {
                    new UsersShelf().updateChannelRating(entity.getIdChannel());
                }
            };
            thread.setName("thread-rating-channel-" + System.currentTimeMillis());
            thread.start();
        }
        return error;
    }

    public Map<Long, List<String>> getRatingRTAction(String idLive) {
        return getDbForQuery().castRatingsByLive(idLive);
    }


    public EnumErrors updateEncodingStatus(com.google.gson.JsonObject json) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {

            if (json.get("event").getAsString().equals("job.completed")
                    && !json.getAsJsonObject("errors").has("output")
                    && !json.getAsJsonObject("errors").has("source")) {

                String idJob = json.get("id").getAsString();
                String idMedia =
                        RMemoryAPI.getInstance().pullElemFromMemory(Constants.MEDIA_KEY + "coding:queue:" + idJob);
                if (idMedia == null) {
                    throw new Exception("Queue is empty no ID Job with: " + idJob);
                } else {
                    RMemoryAPI.getInstance().delFromMemory(Constants.MEDIA_KEY + "coding:queue:" + idJob);
                    RMemoryAPI.getInstance().delFromSetElem(Constants.MEDIA_KEY + "vqueue", idJob);
                }

                String idUser = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "id_user");
                if (idUser == null) {
                    throw new Exception("User key is not found by ID Media: " + idMedia);
                }
                MediaStatus progress =
                        RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "progress", MediaStatus.class);

                RMemoryAPI.getInstance().pushElemToMemory(Constants.MEDIA_KEY + "encstate:" + idMedia, 3, "100");
                log.debug("Progress value for upload video {}", progress.toString());
                if (progress != null && progress.getProgress() < 2) {

                    String idM = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "id_media");
                    if (idM != null && !idM.isEmpty()) {
                        ThreadRunner runner = new ThreadRunner(Long.valueOf(idMedia));
                        errors = updateMediaStatus(Long.valueOf(idMedia), (short) 0);
                        if (errors == EnumErrors.NO_ERRORS) {
                            new UsersShelf()
                                    .addUserActChangeLog(Long.valueOf(idUser), Long.valueOf(idMedia), UserActivitiesLogEnum.ADDED_VIDEO.name(), 1); //1 - Video related type
                            errors = runner.pushMultiCastFollowers();
                        }
                    }

                    //EncodingUnits.tuneMediaInfoParams(idMedia, json);
                    ThumbnailBuilder.thumbnailCopy(Long.valueOf(idMedia), Long.valueOf(idUser));
                }

            } else if (json.get("event").getAsString().equals("source.transferred")
                    || json.get("event").getAsString().equals("output.processed")) {

                String idJob = json.get("id").getAsString();
                String idMedia =
                        RMemoryAPI.getInstance().pullElemFromMemory(Constants.MEDIA_KEY + "coding:queue:" + idJob);
                if (idMedia != null) {
                    String progress = json.get("progress").getAsString().replace("%", "");
                    RMemoryAPI.getInstance()
                            .pushElemToMemory(Constants.MEDIA_KEY + "encstate:" + idMedia, 3, progress);
                }

            } else if (json.getAsJsonObject("errors").has("output")) {
                String idJob = json.get("id").getAsString();
                String idMedia =
                        RMemoryAPI.getInstance().pullElemFromMemory(Constants.MEDIA_KEY + "coding:queue:" + idJob);
                log.error("GOT ERROR FROM TRANSCODER ID MEDIA {}, idJob {}", idMedia, idJob);
            }
        } catch (Exception ex) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("ERROR IN MAIN API:::: ", ex);
        }

        return errors;
    }

    public EnumErrors updateEncodingDebateStatus(com.google.gson.JsonObject json) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {

            if(json.get("event").getAsString().equals("job.completed")
                    && !json.getAsJsonObject("errors").has("output")
                    && !json.getAsJsonObject("errors").has("source")){

                String idJob = json.get("id").getAsString();
                String idMedia =
                        RMemoryAPI.getInstance().pullElemFromMemory(Constants.MEDIA_KEY + "coding:queue:" + idJob);
                if(idMedia==null){
                    throw new Exception("Queue is empty no ID Job with: "+idJob);
                }
                else {
                    RMemoryAPI.getInstance().delFromMemory(Constants.MEDIA_KEY + "coding:queue:" + idJob);
                    RMemoryAPI.getInstance().delFromSetElem(Constants.MEDIA_KEY + "vqueue", idJob);
                }

            }
        } catch (Exception ex) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("ERROR IN MAIN DEBATE VIDEO TRANSCODER API:::: ", ex);
        }

        return errors;
    }

    public void updateVideoThumbnail(com.google.gson.JsonObject json) {
        try {
            if (json.get("event").getAsString().equals("job.completed")
                    && !json.getAsJsonObject("errors").has("output")
                    && !json.getAsJsonObject("errors").has("source")) {

                String idJob = json.get("id").getAsString();
                String idMedia =
                        RMemoryAPI.getInstance().pullElemFromMemory(Constants.MEDIA_KEY + "coding:queue:" + idJob);
                if (idMedia == null) {
                    throw new Exception("Queue is empty no ID Job with: " + idJob);
                }
                String idUser = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY+idMedia,"id_user");
                if(idUser==null){
                    throw new Exception("User key is not found by ID Media: "+idMedia);
                }
                ThumbnailBuilder.thumbnailCopy(Long.valueOf(idMedia), Long.valueOf(idUser));
            }
        } catch (Exception e) {
            log.error("ERROR THUMBNAIL EXTRACTING ", e);
        }
    }



    public EnumErrors updateThumbEncodingStatus(String json) {
        EnumErrors errors = EnumErrors.NO_ERRORS;

        return errors;
    }



    public EnumErrors updateEncodingErrorStatus(String json) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {

            EncodingResult result = new Gson().fromJson(json, EncodingResult.class);
            if (result != null) {

                String st = (String) RMemoryAPI.getInstance().
                        pullElemFromMemory(Constants.MEDIA_KEY + "mediaid:" + result.getResult().getMediaid(), String.class);

                final Long idMedia = Long.valueOf(st.split("-")[0]);
                MediaStatus progress = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "progress", MediaStatus.class);


                if(progress!=null && progress.getProgress()<3) {
                    RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + idMedia, "progress", new MediaStatus(2).toString());
                    RMemoryAPI.getInstance().pushElemToMemory(Constants.MEDIA_KEY + "encstate:" + idMedia, 2, "100");

                    ThreadRunner runner = new ThreadRunner(idMedia);
                    errors = updateMediaStatus(idMedia, (short) 2);
                    runner.pushOwnerNotification();
                }
            }
        } catch (Exception ex) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("ERROR IN MAIN API:::: ", ex);
        }

        return errors;
    }

    public EnumSQLErrors updateMediaTechProps(DcMediaEntity properties) {
        return getDbForUpd().updateMediaTechInfo(properties);
    }

    public void alertMediaThumbnail(Long idMedia, Long idUser, int thumb) throws IOException {
        new MediaThumbnail().changeThumb(idMedia, idUser, thumb);
    }


    public Map<Long, List<DcCommentsEntity>> castCommentsByIdLive(long idMedia, int duration, long off, long lm, boolean reverse, boolean sort) {
        return getDbForQuery().castCommentsByIdLive(idMedia, duration, off, lm, reverse, sort);
    }

    public List<DcCommentsEntity> castCommentsByIdMedia(long idMedia, long off, long limit, boolean sort) {
        off = (off < 0 ? 0 : off);
        limit = ((limit <= 0 || limit > 60) ? LIMIT : limit);
        List<DcCommentsEntity> entity = new LinkedList<>();
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            entity = queryFactory.queryMediaComments(0, idMedia, (int)off, (int)limit, false, sort);

        } catch (Exception e) {
            log.error("ERROR IN CORE API ", e);
        }

        return entity;
    }

    /*START PLAYLIST*/
    public List<MediaFeaturedBean> getMediaFromPlaylist(String ID, int off, int limit) {
        off = off < 0 ? 0 : off;
        limit = off + ((limit <= 0 || limit > 60) ? LIMIT : limit);

        List<MediaFeaturedBean> mlist = new ArrayList<>();
        List<String> res = RMemoryAPI.getInstance()
                .pullSortedListElem(Constants.MEDIA_PLAYLIST+"media:"+ID
                        , "dc_media:*->detail", off, limit, "dc_media:*->id_media");

        Gson gson = new Gson();
        List<DcMediaEntity> entList = gson.fromJson(res.toString(),
                new TypeToken<List<DcMediaEntity>>() {
                }.getType());

        entList.forEach(o -> {
            LiveProps props = new LiveProps();
            MediaFeaturedBean listInfo = new MediaFeaturedBean();
            Long idMedia = o.idMedia;
            Long idUser = o.idUser;
            props.hls_url = Constants.encryptAmazonURL(o.idUser, o.idMedia, "", "hls", Constants.VIDEO_URL);
            props.mp4_url = Constants.encryptAmazonURL(o.idUser, o.idMedia, "_hi.mp4", "v", Constants.VIDEO_URL);
            props.rtmp_url = RMemoryAPI.getInstance().pullHashFromMemory(Constants.LIVE_KEY + o.idMedia, "rtmp_liveurl");


            List<DcDebateEntity> debateEntity = extractVideosInDebate(idMedia);

            List<String> rlist =
                    RMemoryAPI.getInstance()
                            .pullListElemFromMemory(Constants.MEDIA_KEY + "properties:rotatime:" + idMedia, 0, -1);
            List<ScreenRotation> lrotation = new Gson().fromJson(rlist.toString(),
                    new TypeToken<List<ScreenRotation>>() {
                    }.getType());

            String username = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.USER_KEY + idUser, "username");
            String avatar = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.USER_KEY + idUser, "avatar");
            String liked = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "liked");
            String ccount = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "ccount");
            String vcount = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "vcount");
            String lvcount = RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "lvcount");


            MediaStatistics statistics = new MediaStatistics();
            statistics.likes = Long.valueOf(liked);
            statistics.comments = Long.valueOf(ccount);
            statistics.record_viewers = Long.valueOf(vcount);
            statistics.live_viewers = Long.valueOf(lvcount == null ? "0" : lvcount);

            listInfo.username = (username);
            listInfo.avatar = (Constants.STATIC_URL + idUser + "/image" + avatar + ".jpg");
            listInfo.idMedia = idMedia;
            listInfo.title = o.getTitle();
            listInfo.setLiveProps(props);
            listInfo.duration = (o.getDuration());
            listInfo.dateadded = (o.getDateadded());
            listInfo.setDebaters(debateEntity);
            listInfo.setRotations(lrotation);
            listInfo.setStats(statistics);
            listInfo.idUser = (idUser);
            listInfo.method = (o.getMethod());
            listInfo.thumb = (Constants.encryptAmazonURL(idUser, idMedia, "jpg", "thumb", Constants.STATIC_URL));

            listInfo.location = (RMemoryAPI.getInstance()
                    .pullHashFromMemory(Constants.MEDIA_KEY + idMedia, "location", "en"));

            listInfo.ratio = (o.ratio);

            mlist.add(listInfo);
        });

        return mlist;
    }

    public List<DcMediaPlaylist> getPlaylistByIdUser(Long idUser, int off, int limit) {
        off = off < 0 ? 0 : off;
        limit = off + ((limit <= 0 || limit > 60) ? LIMIT : limit);

        List<DcMediaPlaylist> plist = new ArrayList<>();
        List<String> res = RMemoryAPI.getInstance()
                .pullListElemFromMemory(Constants.MEDIA_PLAYLIST+"list:"+idUser, off, limit);

        res.forEach(ID -> {
            String title = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_PLAYLIST+ID, "title");
            String descr =RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_PLAYLIST+ID, "description");
            String username =RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY+idUser, "username");
            long vcount =RMemoryAPI.getInstance().checkSetElemCount(Constants.MEDIA_PLAYLIST+"media:"+ID);
            DcMediaPlaylist playlist = new DcMediaPlaylist();
            playlist.descr = descr;
            playlist.id_playlist = ID;
            playlist.idUser = idUser;
            playlist.username = username;
            playlist.vcount = vcount;
            playlist.title = title;

            plist.add(playlist);

        });

        return plist;
    }

    public List<DcMediaPlaylist> getTrendingPlaylists(int off, int limit) {
        off = off < 0 ? 0 : off;
        limit = off + ((limit <= 0 || limit > 60) ? LIMIT : limit);

        List<DcMediaPlaylist> plist = new ArrayList<>();
        List<String> res = RMemoryAPI.getInstance()
                .pullListElemFromMemory(Constants.MEDIA_PLAYLIST+"trending", off, limit);

        res.forEach(ID -> {
            String title = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_PLAYLIST+ID, "title");
            String descr =RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_PLAYLIST+ID, "description");
            String idUser =RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_PLAYLIST+ID, "id_user");
            String username =RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY+idUser, "username");
            long vcount =RMemoryAPI.getInstance().checkSetElemCount(Constants.MEDIA_PLAYLIST+"media:"+ID);
            DcMediaPlaylist playlist = new DcMediaPlaylist();
            playlist.descr = descr;
            playlist.id_playlist = ID;
            playlist.idUser = Long.valueOf(idUser);
            playlist.username = username;
            playlist.vcount = vcount;
            playlist.title = title;

            plist.add(playlist);

        });

        return plist;
    }

    public DcMediaPlaylist getPlaylistInfo(String ID) {
        DcMediaPlaylist playlist = new DcMediaPlaylist();
        String title = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_PLAYLIST+ID, "title");
        if(title!=null) {
            String descr = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_PLAYLIST + ID, "description");
            String idUser = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_PLAYLIST + ID, "id_user");
            String username = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "username");
            long vcount = RMemoryAPI.getInstance().checkSetElemCount(Constants.MEDIA_PLAYLIST + "media:" + ID);
            playlist.descr = descr;
            playlist.id_playlist = ID;
            playlist.idUser = Long.valueOf(idUser);
            playlist.username = username;
            playlist.vcount = vcount;
            playlist.title = title;
        }
        return playlist;
    }

    public void addMediaInPlaylist(Long idMedia, String ID) {
        String idm = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY+idMedia, "id_media");
        if(idm!=null) {
            RMemoryAPI.getInstance().pushSetElemToMemory(Constants.MEDIA_PLAYLIST + "media:" + ID, idMedia + "");
            RMemoryAPI.getInstance().pushSetElemToMemory(Constants.MEDIA_PLAYLIST + "id:" + idMedia, ID);
        }
    }

    public void delMediaInPlaylist(Long idMedia, String ID) {
        RMemoryAPI.getInstance().delFromSetElem(Constants.MEDIA_PLAYLIST+"media:"+ID, idMedia+"");
    }

    public void createPlaylist(String title, String description, Long idMedia, Long idUser) {
        String ID = System.currentTimeMillis()+"_plst";
        title = org.apache.commons.lang3.StringUtils.normalizeSpace(title);
        description = (description==null||description.isEmpty())?"":description;
        RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_PLAYLIST+ID, "title", title);
        RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_PLAYLIST+ID, "description", description);
        RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_PLAYLIST+ID, "id_user", idUser+"");

        RMemoryAPI.getInstance().pushUnlimitedListToMemory(Constants.MEDIA_PLAYLIST+"list:"+idUser, ID);
        if(idMedia!=null && idMedia!=0){
            String idm = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY+idMedia, "id_media");
            if(idm!=null) {
                RMemoryAPI.getInstance().pushSetElemToMemory(Constants.MEDIA_PLAYLIST + "media:" + ID, idMedia + "");
                RMemoryAPI.getInstance().pushSetElemToMemory(Constants.MEDIA_PLAYLIST+"id:"+idMedia, ID);
            }
        }
    }

    public void deletePlaylist(String ID, Long idUser) {
        RMemoryAPI.getInstance().delFromMemory(Constants.MEDIA_PLAYLIST+ID);
        RMemoryAPI.getInstance().delFromListElem(Constants.MEDIA_PLAYLIST+"list:"+idUser, ID);
        RMemoryAPI.getInstance().delFromMemory(Constants.MEDIA_PLAYLIST+"media:"+ID);
        RMemoryAPI.getInstance().delFromListElem(Constants.MEDIA_PLAYLIST+"trending", ID);

    }

    public void updatePlaylist(String ID, String title, String description) {
        description = (description==null||description.isEmpty())?"":description;
        title = org.apache.commons.lang3.StringUtils.normalizeSpace(title);
        RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_PLAYLIST+ID, "title", title);
        RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_PLAYLIST+ID, "description", description);
    }

    /*END PLAYLIST*/

    public List<MediaNewsStatInfo> extractVideosByFragment(String frg, int limit, int off) {

        List<MediaNewsStatInfo> list = new ArrayList<>();
        try {
            RiakTP transport = RiakAPI.getInstance();
            IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
            List<DcMediaEntity> mlist = queryFactory.queryMediaDataByTitle(frg, off, limit);
            mlist.forEach(entity -> list.add(new MediaNewsStatInfo(entity)));

        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public List<MediaNewsStatInfo> extractVideosInTrash(long idUser, long limit, long offset) {
        return getDbForQuery().castMediaInArchive(idUser, limit, offset, lang);
    }

    public List<DcDebateEntity> extractVideosInDebate(long idMedia) {
        return getDbForQuery().castMediaInDebate(idMedia);
    }

    public com.dgtz.api.beans.MediaInfo getMediaSizes(Long idMedia) throws InterruptedException, ExecutionException, TimeoutException, IOException {
        String width = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + idMedia, "width");
        String height = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + "properties:" + idMedia, "height");

        if (width != null && height != null) {
            return new MediaInfo(Integer.valueOf(width), Integer.valueOf(height));
        } else {
            return new MediaInfo(640, 480);
        }
    }

    public static boolean isLiveHLSReady(Long idMedia) {
        boolean isReady = true;
        String status = RMemoryAPI.getInstance().pullHashFromMemory(Constants.MEDIA_KEY + "hls:" + idMedia, "status");
        if (status == null) {
            isReady = false;
        }

        return isReady;
    }

    public List<MediaNewsStatInfo> getRelatedContents(Long idMedia) {

        List<MediaNewsStatInfo> mediaList = new ArrayList<>();

        try {
            DcMediaEntity media = getDbForQuery().castMediaById(idMedia);

            if (media != null) {
                RiakTP transport = RiakAPI.getInstance();
                IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
                List<DcMediaEntity> mlist = queryFactory.queryRelatedVideos(media.tags, media.idUser, idMedia);

                mlist.forEach(entity -> mediaList.add(new MediaNewsStatInfo(entity)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaList;
    }


    public long extractUniqueIdForMedia() {
        return getDbForQuery().extractUniqueIdForMedia();
    }

    public long extractVideoRate(long idUser, long idMedia) {
        return getDbForQuery().extractVideoRateByUserAVideo(idUser, idMedia);
    }

    public List<MediaNewsStatInfo> extractVideoByIdChannel(long idChannel, long off, long limit) {
        return getDbForChannel().extractChannelsContent(idChannel, off, limit, lang);
    }

    /*private List<ScreenRotation> addDefaultRotationTime(){
        ScreenRotation rotation = new ScreenRotation();
        rotation.setRotation(0);
    }*/


}