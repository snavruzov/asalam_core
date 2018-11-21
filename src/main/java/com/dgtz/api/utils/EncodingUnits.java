package com.dgtz.api.utils;

import com.brocast.riak.api.beans.DcMediaEntity;
import com.dgtz.api.beans.MediaInfo;
import com.dgtz.api.beans.Response;
import com.dgtz.api.contents.MediaShelf;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 * Created by sardor on 5/8/16.
 */
public final class EncodingUnits {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EncodingUnits.class);

    public EncodingUnits() {
    }

    public static void tuneMediaInfoParams(String idMedia,  com.google.gson.JsonObject json) {
        try {

            Long duration = json.getAsJsonObject("metadata")
                    .getAsJsonObject("mp4::copy")
                    .getAsJsonObject("format")
                    .get("duration").getAsLong();

            if (duration != 0) {
                log.debug("Right duration {} idMEdia {}", duration, idMedia);
                DcMediaEntity entity = new DcMediaEntity();
                entity.setDuration(duration.intValue());
                entity.setIdMedia(Long.valueOf(idMedia));

                MediaShelf mediaShelf = new MediaShelf();
                mediaShelf.updateMediaTechProps(entity);

                RMemoryAPI.getInstance()
                        .pushHashToMemory(Constants.MEDIA_KEY + "properties:" + idMedia, "duration", "" + duration);
            }
        } catch (Exception e) {
            log.error("Cannot retrieve video duration ", e);
        }
    }

}
