package com.dgtz.api.contents;

import com.brocast.riak.api.beans.DcMediaEntity;
import com.brocast.riak.api.beans.LiveProps;
import com.dgtz.api.beans.LiveMediaInfo;
import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.feature.DBConnector;
import com.dgtz.api.utils.AbuseFilter;
import com.dgtz.api.utils.LanguageDetector;
import com.dgtz.api.utils.TagTokenizer;
import com.dgtz.db.api.domain.DcLiveMediaEntity;
import com.dgtz.db.api.enums.EnumSQLErrors;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Set;

/**
 * Created by sardor on 1/3/14.
 */
public class LiveShelf extends DBConnector {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LiveShelf.class);
    private String lang;

    public LiveShelf() {
        super();
    }

    public LiveShelf(String lang) {
        super();
        this.lang = LanguageDetector.detectLang(lang);
    }


    public EnumErrors saveLiveProperties(LiveMediaInfo info) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            com.brocast.riak.api.beans.DcMediaEntity liveMediaEntity = new com.brocast.riak.api.beans.DcMediaEntity();

            LiveProps props = new LiveProps();
            props.rtmp_url = info.getRtmpUrl();
            props.hls_url = info.getHttpUrl();
            props.debate = info.getDebate();
            props.position = info.getPosition();

            liveMediaEntity.idMedia = (info.getIdLive());
            liveMediaEntity.idUser = (info.getIdUser());
            liveMediaEntity.title = (info.getTitle());
            liveMediaEntity.description = (info.getDescription());
            liveMediaEntity.idCategory = (1); //1 - video, 2 - text
            liveMediaEntity.idChannel = (info.getIdChannel());
            liveMediaEntity.location = info.getLocation();
            liveMediaEntity.method = "live";
            liveMediaEntity.dateadded = RMemoryAPI.getInstance().currentTimeMillis() + "";
            liveMediaEntity.duration = 0;
            liveMediaEntity.progress = (info.getProcess());


            if(info.getLatlong()!=null){
                String[] coords = info.getLatlong().split(" ");
                liveMediaEntity.coordinate = coords[1]+","+coords[0];
            }
            liveMediaEntity.lang = info.getLang();
            liveMediaEntity.rating = 0L;
            liveMediaEntity.setLiveProps(props);

            RMemoryAPI.getInstance()
                    .pushHashToMemory(Constants.MEDIA_KEY + info.getIdLive(), "download", info.getDownload()+"");

            log.debug("HLS Live Url: {} ", info.getHttpUrl());

            log.debug("LOCATION: {}", info.getLocation());


            if (info.getTitle().equals("undefined")) {
                liveMediaEntity.title = ("#live " + info.getUser().username);
            }

            Set<String> tagList = TagTokenizer.normilizeTag(info.getTags());

            log.debug("tags {}", tagList);

            liveMediaEntity.tags = tagList;

            EnumSQLErrors sqlErrors = getDbForUpd().saveContentInfo(liveMediaEntity, lang);
            if(sqlErrors!=EnumSQLErrors.OK){errors = EnumErrors.UNKNOWN_ERROR;}
        } catch (Exception e){
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("Error while live saving ", e);
        }

        return errors;
    }

    public DcLiveMediaEntity extractLiveById(long idLive) {
        DcLiveMediaEntity entities = getDbForQuery().castLiveById(idLive);
        return entities;
    }

    public EnumSQLErrors doneLivePublish(long idLive, int progress, int duration) {

        DcMediaEntity entity = new DcMediaEntity();
        entity.setDuration(duration);
        entity.setIdMedia(idLive);

        MediaShelf mediaShelf = new MediaShelf();
        mediaShelf.updateMediaTechProps(entity);
        mediaShelf.updateMediaStatus(idLive, (short)0);

        return getDbForUpd().updateLiveInfo(idLive, progress);
    }

    public EnumSQLErrors publishLiveStream(long idLive, int progress) {
        return getDbForUpd().publishLive(idLive, progress);
    }



    public EnumSQLErrors updateInRealTime(final DcMediaEntity entity) {
        AbuseFilter f = new AbuseFilter();
        try {
            String title = f.filter(entity.getTitle());
            String descr = f.filter(entity.getDescription());
            entity.setTitle(title);
            entity.setDescription(descr);
        } catch (UnsupportedEncodingException e) {
            log.error("ERROR IN MAIN API ", e);
        }

        EnumSQLErrors errors = getDbForUpd().updateRealTimeLiveInfo(entity);

        return errors;
    }

    public long extractUniqueIdForLive() {
        return getDbForQuery().extractUniqueIdForMedia();
    }

    public long validateBeforeUpdate(String key) {
        return getDbForQuery().validateKeyPreUpdate(key);
    }

    public long getLiveViewCount(long idLive) {
        return getDbForQuery().getLiveViewCount(idLive);
    }

}
