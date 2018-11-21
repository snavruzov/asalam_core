package com.dgtz.api.utils;

import com.dgtz.api.beans.Response;
import com.dgtz.api.feature.EncodingImpl;
import com.dgtz.api.settings.IVideoMuxer;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 4/21/14
 */
public class VideoEncoder implements IVideoMuxer {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(VideoEncoder.class);

    private String input;
    private int time;
    private String[] idDir;

    public VideoEncoder() {
    }

    public VideoEncoder(String input, String[] idDir, int time) {
        this.input = input;
        this.idDir = idDir;
        this.time = time;
    }

    @Override
    public Response buildFast() {
        return null;
    }

    @Override
    public List<String> buildWebM() {
        String rotate = "";
        List<String> buildCmd = new CopyOnWriteArrayList<String>();
        return buildCmd;
    }

    @Override
    public List<String> buildLiveWebM() {
        return null;
    }

    @Override
    public List<String> build() {
        String rotate = "";

        List<String> buildCmd = new CopyOnWriteArrayList<String>();

        return buildCmd;

    }

    @Override
    public List<String> buildLive() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> buildThumbnails() {
        List<String> buildCmd = new CopyOnWriteArrayList<String>();

        return buildCmd;

    }

    @Override
    public List<String> transpose() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
