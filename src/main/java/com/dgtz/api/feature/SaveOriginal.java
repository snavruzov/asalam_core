package com.dgtz.api.feature;

import com.dgtz.api.beans.MediaInfo;
import com.dgtz.api.beans.MediaJSObject;
import com.dgtz.api.beans.MediaStreamFormat;
import com.dgtz.api.compresser.CompressFactory;
import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.enums.EnumFileType;
import com.dgtz.api.settings.IFileManipulate;
import com.dgtz.api.utils.FileUtil;
import com.dgtz.api.utils.ShellCommands;
import com.google.gson.Gson;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 12/15/13
 * Time: 7:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveOriginal implements IFileManipulate {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SaveOriginal.class);

    public SaveOriginal() {
    }


    @Override
    public EnumErrors save(byte[] source, File dest) {
        FileChannel outputChannel = null;
        EnumErrors errors = EnumErrors.NO_ERRORS;

        try {

            final ByteBuffer ret = ByteBuffer.wrap(new byte[source.length]);
            ret.put(source);
            ret.flip();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.write(ret);

        } catch (IOException e) {
            log.error("ERROR IN MAIN API ", e);
        } finally {

            if (outputChannel != null) {
                try {
                    outputChannel.close();
                } catch (IOException e) {
                    log.error("ERROR IN MAIN API ", e);
                }
            }

        }

        return errors;

    }

    @Override
    public EnumErrors remove() {
        return null;
    }

    @Override
    public void preProcess(String filePath) {
        ShellCommands.preVideoLiveProcess(filePath);
    }

    @Override
    public void preConcatProcess(String apath, String vpath, String fileOut) {
        ShellCommands.preVideoJoinAudioProcess(apath, vpath,fileOut);
    }



    @Override
    public MediaInfo info(String filename, EnumFileType fType) {
        MediaInfo mediaInfo = new MediaInfo();
        switch (fType) {
            case IMAGE: {
                mediaInfo = imageInfo(filename, mediaInfo);
                break;
            }
            case VIDEO: {
                mediaInfo = videoInfo(filename, mediaInfo);
                /*Broken video trying to fix*/
                if (mediaInfo != null && mediaInfo.getWidth() == 0) {
                    mediaInfo.setWidth(320);
                    mediaInfo.setHeight(240);
                }
                break;
            }
            case AUDIO: {
                mediaInfo = audioInfo(filename, mediaInfo);
                break;
            }
        }
        return mediaInfo;
    }

    private MediaInfo imageInfo(String filename, MediaInfo mediaInfo) {
        File fImg = new File(filename);
        Image image = Toolkit.getDefaultToolkit()
                .getImage(fImg.getAbsolutePath());
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        mediaInfo.setHeight(height);
        mediaInfo.setWidth(width);
        mediaInfo.setType("image");
        mediaInfo.setSize(fImg.length());

        return mediaInfo;
    }

    private MediaInfo videoInfo(String filename, MediaInfo mediaInfo) {
        Gson gson = new Gson();
        String js = ShellCommands.getJsonInfoOfVideo(filename);
        log.debug("JS SHELL {}", js);
        if (js.isEmpty()) {
            return null;
        }
        try {
            MediaJSObject jsObject = gson.fromJson(js, MediaJSObject.class);
            mediaInfo.setDuration((long) jsObject.getFormat().getDuration());
            mediaInfo.setSize((jsObject.getFormat().getSize() / 1024) / 1024);
            mediaInfo.setFilename(jsObject.getFormat().getFilename());
            for (MediaStreamFormat ms : jsObject.getStreams()) {
                if (ms.getCodec_type().equals("video")) {
                /*===============*/
                    long vb = 0;
                    if (ms.getBit_rate() == null) {
                        vb = jsObject.getFormat().getBit_rate();
                    } else {
                        vb = ms.getBit_rate();
                    }
                /*=======END========*/
                    mediaInfo.setVbitrate(vb / 1024);
                    mediaInfo.setHeight(ms.getHeight());
                    mediaInfo.setWidth(ms.getWidth());
                    mediaInfo.setType("video");

                    Double framerate = 23.98;
                    String fps = ms.getR_frame_rate();
                    if(fps!=null) {
                        String numer = fps.substring(0,fps.indexOf("/"));
                        String denom = fps.substring(fps.indexOf("/")+1,fps.length());
                        framerate = Double.valueOf(numer)/Double.valueOf(denom);
                    }
                    mediaInfo.setFps(framerate);

                    if (ms.getTags() != null && ms.getTags().getRotate() != null && ms.getTags().getRotate() == 90)
                        mediaInfo.setRotate(true);
                }

                if (ms.getCodec_type().equals("audio")) {
                /*===============*/
                    long ab = 0;
                    if (ms.getBit_rate() == null) {
                        ab = 131072;
                    } else {
                        ab = ms.getBit_rate();
                    }
                /*=======END========*/
                    mediaInfo.setAbitrate(ab / 1024);
                    mediaInfo.setChannels(ms.getChannels());
                }

            }


            log.debug(mediaInfo.toString());
        } catch (Exception e) {
            log.error("ERROR IN THE MAIN API, GETTING FFPROBE INFO: ", e);
            mediaInfo = null;
        }
        return mediaInfo;
    }

    private MediaInfo audioInfo(String filename, MediaInfo mediaInfo) {
        Gson gson = new Gson();
        String js = ShellCommands.getJsonInfoOfVideo(filename);
        log.debug("JS SHELL {}", js);
        if (js == null || js.isEmpty()) {
            return null;
        }
        try {
            MediaJSObject jsObject = gson.fromJson(js, MediaJSObject.class);
            mediaInfo.setDuration((long) jsObject.getFormat().getDuration());
            mediaInfo.setSize((jsObject.getFormat().getSize() / 1024) / 1024);
            mediaInfo.setFilename(jsObject.getFormat().getFilename());
            for (MediaStreamFormat ms : jsObject.getStreams()) {
                if (ms.getCodec_type().equals("audio")) {
                /*===============*/
                    long ab = 0;
                    if (ms.getBit_rate() == null) {
                        ab = 131072;
                    } else {
                        ab = ms.getBit_rate();
                    }
                /*=======END========*/
                    mediaInfo.setAbitrate(ab / 1024);
                }

            }

            log.debug(mediaInfo.toString());
        } catch (Exception e) {
            log.error("ERROR IN THE MAIN API, GETTING FFPROBE INFO: ", e);
            mediaInfo = null;
        }
        return mediaInfo;
    }


    @Override
    public EnumFileType check(String fullPath) {

        EnumFileType fileType = null;
        try {

            String str = FileUtil.checkMimeType(fullPath);
            log.debug(" File tp: {}, PATH: {}", str, fullPath);
            if (str.contains("riff")) {
                ShellCommands shellCommands = new ShellCommands();
                shellCommands.decodeFromWebP(fullPath);
                str = "image";
            }
            if (str.contains("image")) {
                fileType = EnumFileType.IMAGE;
            } else if (str.contains("video")) {
                fileType = EnumFileType.VIDEO;
            } else {
                fileType = EnumFileType.BIN;
            }


        } catch (IOException e) {
            log.error("ERROR IN MAIN API ", e);
        }

        return fileType;

    }

    @Override
    public EnumErrors hash() {


        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public EnumErrors mkdir() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
