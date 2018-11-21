package com.dgtz.api.utils;

import com.dgtz.api.beans.MediaInfo;
import com.dgtz.api.beans.Response;
import com.dgtz.api.constants.Media;
import com.dgtz.api.enums.EnumDimension;
import com.dgtz.api.settings.IVideoMuxer;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 4/21/14
 */
public class TransposeVideo implements IVideoMuxer {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(TransposeVideo.class);
    private static final String LIBPATH = Media.PATH_SETTINGS.getCoderPath();
    private static final String OUTPATH = Media.PATH_SETTINGS.getMediaPath();

    private MediaInfo info;
    private String input;
    private String[] idDir;


    public TransposeVideo() {
    }

    public TransposeVideo(MediaInfo info, String input, String[] idDir) {
        this.info = info;
        this.input = input;
        this.idDir = idDir;
    }

    @Override
    public Response buildFast() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> build() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> buildLive() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> buildWebM() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> buildLiveWebM() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> buildThumbnails() {
        return null;
    }

    @Override
    public List<String> transpose() {
        String rotate = "";

        if (info.getClockPointer() == 1) {
            rotate = "transpose=1,";
        } else if (info.getClockPointer() == -1) {
            rotate = "transpose=2,";
        } else if (info.getClockPointer() == 2) {
            rotate = "rotate=180*(PI/180),";
        }

        String idUser = idDir[0];
        String idMedia = idDir[1];

        List<String> buildCmd = new CopyOnWriteArrayList<String>();
        String shell = "";
        List<EnumDimension> dimmList = new ArrayList<EnumDimension>();

        buildCmd.add(LIBPATH);

        buildCmd.add("-probesize");
        buildCmd.add("10M");

        buildCmd.add("-analyzeduration");
        buildCmd.add("10M");

        buildCmd.add("-i");
        buildCmd.add(input);

        buildCmd.add("-i");
        buildCmd.add("/opt/encoder/logo/water.png");

        buildCmd.add("-filter_complex");
        buildCmd.add("[0]" + rotate + "overlay=10:10,split=2 [720x480] [640x360];[720x480]scale=min(720\\,iw):trunc(ow/a/2)*2:-1[720x480];[640x360]scale=min(640\\,iw):trunc(ow/a/2)*2:-1[640x360]");


        EnumDimension dm = EnumDimension.HD;
        {


            buildCmd.add("-map");
            buildCmd.add("[" + dm.size + "]");

            buildCmd.add("-map");
            buildCmd.add("a:0");

            buildCmd.add("-metadata:s:v:0");
            buildCmd.add("rotate=0");

            buildCmd.add("-c:v");
            buildCmd.add("libx264");

            buildCmd.add("-profile:v");
            buildCmd.add("baseline");

            buildCmd.add("-preset");
            buildCmd.add("fast");

            if (info.getVbitrate() > dm.bitrate) {
                buildCmd.add("-b:v");
                buildCmd.add(dm.bitrate + "k");
            } else {
                buildCmd.add("-b:v");
                buildCmd.add(String.valueOf(info.getVbitrate()) + "k");
            }

            if (info.getAbitrate() > Long.valueOf(Media.ab.replace("k", ""))) {
                buildCmd.add("-b:a");
                buildCmd.add(Media.ab);
            } else {
                buildCmd.add("-b:a");
                buildCmd.add(String.valueOf(info.getAbitrate()) + "k");
            }

            buildCmd.add("-r");
            buildCmd.add("20");

            buildCmd.add("-maxrate");
            buildCmd.add("1000k");

            buildCmd.add("-bufsize");
            buildCmd.add("1000k");

            buildCmd.add("-threads");
            buildCmd.add("0");

            buildCmd.add("-c:a");
            buildCmd.add("libvo_aacenc");

            buildCmd.add("-ac");
            buildCmd.add(Media.ch);

            //buildCmd.add("-deinterlace");

            buildCmd.add("-ar");
            buildCmd.add("44100");

            buildCmd.add("-y");

            buildCmd.add("-f");
            buildCmd.add("mp4");

            buildCmd.add(OUTPATH + idUser + "/video/" + idMedia + "_" + dm.size + "_temp.mp4");

        }

        if (!rotate.isEmpty()) {
            buildCmd.add("-vf");
            String rt = rotate.replace(",", "");
            buildCmd.add(rt);
        }

        buildCmd.add("-metadata:s:v:0");
        buildCmd.add("rotate=0");

        buildCmd.add("-c:v");
        buildCmd.add("libx264");

        buildCmd.add("-profile:v");
        buildCmd.add("baseline");

        buildCmd.add("-preset");
        buildCmd.add("fast");

        buildCmd.add("-pix_fmt");
        buildCmd.add("yuv420p");

        buildCmd.add("-r");
        buildCmd.add("24");

        buildCmd.add("-crf");
        buildCmd.add("20");

        buildCmd.add("-maxrate");
        buildCmd.add("1000k");

        buildCmd.add("-bufsize");
        buildCmd.add("1835k");

        buildCmd.add("-threads");
        buildCmd.add("0");

        buildCmd.add("-c:a");
        buildCmd.add("libvo_aacenc");

        buildCmd.add("-ac");
        buildCmd.add(Media.ch);

        buildCmd.add("-ar");
        buildCmd.add("44100");

        buildCmd.add("-y");

        buildCmd.add("-f");
        buildCmd.add("mp4");


        buildCmd.add(OUTPATH + idUser + "/original/" + idMedia + "_encoded_temp.mp4");

        return buildCmd;
    }
}
