package com.dgtz.api.utils;

import com.dgtz.api.beans.MediaInfo;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 2/19/15
 */
public class MediaParameters {

    public MediaParameters() {
    }

    public void saveVideoRation(MediaInfo mediaInfo, Long idMedia) {
        String ratio = getAspectRatio(mediaInfo.getWidth(), mediaInfo.getHeight(), false);

        if (mediaInfo.getHeight() > mediaInfo.getWidth()) {
            ratio = getAspectRatio(mediaInfo.getWidth(), mediaInfo.getHeight(), true);
        }

        RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + "properties:" + idMedia, "ratio", ratio);
    }

    private String getAspectRatio(int w, int h, boolean rotate) {
        int rem;
        int newW = w;
        int newH = h;

        while (h != 0) {
            rem = w % h;
            w = h;
            h = rem;
        }

        newH = newH / w;
        newW = newW / w;

        String ratio = newW + ":" + newH;
        if (rotate) {
            ratio = newH + ":" + newW;
        }
        return ratio;
    }
}
