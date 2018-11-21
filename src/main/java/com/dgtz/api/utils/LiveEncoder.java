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

public class LiveEncoder implements IVideoMuxer {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LiveEncoder.class);

    private String input;
    private String[] idDir;
    private int time;
    private int rotation;

    public LiveEncoder() {
    }

    public LiveEncoder(String input, String[] idDir, int time, int rotation) {
        this.input = input;
        this.idDir = idDir;
        this.time = time;
        this.rotation = rotation;
    }

    @Override
    public Response buildFast() {
        return null;
    }

    @Override
    public List<String> build() {
        return null;
    }

    @Override
    public List<String> buildThumbnails() {

        List<String> buildCmd = new CopyOnWriteArrayList<>();

        return buildCmd;
    }

    @Override
    public List<String> buildWebM() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> buildLiveWebM() {
        String idUser = idDir[0];
        String idMedia = idDir[1];

        List<String> buildCmd = new CopyOnWriteArrayList<>();

        return buildCmd;
    }

    @Override
    public List<String> buildLive() {


        String idUser = idDir[0];
        String idMedia = idDir[1];

        List<String> buildCmd = new CopyOnWriteArrayList<>();

        return buildCmd;
    }

    @Override
    public List<String> transpose() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
