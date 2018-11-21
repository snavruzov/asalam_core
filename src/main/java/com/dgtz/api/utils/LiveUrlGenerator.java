package com.dgtz.api.utils;

import com.dgtz.mcache.api.factory.Constants;

/**
 * Created by sardor on 1/3/14.
 */
public class LiveUrlGenerator {

    public LiveUrlGenerator() {
    }

    public static String[] buildRTMPUrl(long idUser, long idStream) {

        String path = "dgtz";
        String stream = "live_id" + idUser + "_" + idStream;

        return new String[]{Constants.RTMP_PUBLISH_URL, path, stream};
    }

    public static String[] buildRTMPUrl(long idUser, String dName, long idStream) {

        String path = "dgtz";
        String stream = "live_id" + idUser + "_" + idStream;

        return new String[]{Constants.RTMP_PUBLISH_URL, path, stream};

    }

    public static String[] buildTeamRTMPUrl(long idUser, long idStream) {

        String path = "team";
        String stream = "live_id" + idUser + "_" + idStream;
        String chList = null;
        String channel = "ch=none";

        if (chList != null) {
            channel = "adbe-live-event=" + chList;
        } else {
            chList = "";
        }

        return new String[]{Constants.RTMP_PUBLISH_URL, path, stream, channel, chList};

    }

    public static String buildHTMLUrl(long idUser, long idStream, String channel, boolean fromCDN) {

        String hls = Constants.HLS_LIVE_URL + "live_id" + idUser + "_" + idStream + "/index.m3u8";
        if (fromCDN) {
            hls = "http://hls.digitizen.com/hls-live/2010A02/default/"
                    + channel + "/live_id" + idUser + "_" + idStream + ".m3u8";
        }
        return hls;
    }


}
