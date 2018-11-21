package com.dgtz.api.utils;

import com.dgtz.api.beans.MediaInfo;
import com.dgtz.api.beans.Response;
import com.dgtz.api.constants.Media;
import com.dgtz.api.enums.EnumDimension;
import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.settings.IVideoMuxer;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 12/15/13
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
public final class ShellCommands {

    private static final String LIB_PATH = "/path/to";
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ShellCommands.class);
    private static final String OUTPATH = Media.PATH_SETTINGS.getMediaPath();

    public ShellCommands() {
    }

    public ShellCommands(String libPath, String outPath) {

    }

    private static EnumErrors catchMediaCapture(MediaInfo info, String input, String idDir) {

        return null;

    }

    public EnumErrors transposeVideo(MediaInfo info, String input, String[] idDir) {


        String idUser = idDir[0];
        String idMedia = idDir[1];

        IVideoMuxer videoMuxer = new TransposeVideo(info, input, idDir);

        EnumErrors errors = launch(videoMuxer.transpose());

        if (errors == EnumErrors.NO_ERRORS) {
            EnumDimension dm = EnumDimension.HD;
            {
                if (!dm.size.equals(EnumDimension.SD_L.size)) {
                    errors = moveAtom("" + idUser + "/video/" + idMedia + "_" + dm.size + ".mp4");
                }
            }
        }

        return errors;
    }

    public EnumErrors launchEncoding(String input, String[] idDir, Short duration) {
        EnumErrors errors = EnumErrors.NO_ERRORS;

        try {
            String idUser = idDir[0];
            Long idMedia = Long.valueOf(idDir[1]);

            IVideoMuxer videoMuxer = new VideoEncoder(input, idDir, duration);
            Response response = videoMuxer.buildFast();
            System.out.println(response.getMediaID());
            if (response.getErrors() != null) {
                log.error("ENCODING ERROR::::::: ", response.getErrors());
                errors = EnumErrors.ERROR_IN_COMPRESSING;
            } else {
                log.debug("MediaID {}", response.getMediaID());
                if (response.getMediaID() != 0) {
                    RMemoryAPI.getInstance().pushElemToMemory(Constants.MEDIA_KEY + "mediaid:"
                            + response.getMediaID(), 3, idMedia + "-" + idUser);
                    RMemoryAPI.getInstance().pushElemToMemory(Constants.MEDIA_KEY + "idprocess:"
                            + idMedia, 3, response.getMediaID() + "");
                }
            }
        } catch (Exception e) {
            log.error("ERROR IN COMPRESSING ", e);
        }

        return errors;

    }


    public EnumErrors launchLiveEncoding(String input, String[] idDir, short duration, int rotation) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            String idUser = idDir[0];
            Long idMedia = Long.valueOf(idDir[1]);

            IVideoMuxer liveMuxer = new LiveEncoder(input, idDir, duration, rotation);
            Response response = liveMuxer.buildFast();

            if (response.getErrors() != null) {
                log.error("ENCODING ERROR::::::: ", response.getErrors());
                errors = EnumErrors.ERROR_IN_COMPRESSING;
            } else {
                if (response.getMediaID() != 0) {
                    RMemoryAPI.getInstance().pushElemToMemory(Constants.MEDIA_KEY + "mediaid:"
                            + response.getMediaID(), 3, idMedia + "-" + idUser);
                    RMemoryAPI.getInstance().pushElemToMemory(Constants.MEDIA_KEY + "idprocess:"
                            + idMedia, 3, response.getMediaID() + "");
                }
            }

        } catch (Exception e) {
            log.error("ERROR IN COMPRESSING ", e);
        }

        return errors;

    }

    private EnumErrors launch(List<String> cmd) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
            log.info(cmd.toString());
            int extVal = 0;
            try {
                extVal = new ProcessExecutor().command(cmd)
                        .destroyOnExit()
                        .execute()
                        .getExitValue();
                log.info("Exit value of executor {}", extVal);

            } catch (Exception ex) {
                errors = EnumErrors.ERROR_IN_COMPRESSING;
                log.error("ERROR IN TRYING to check MEDIA INFO", ex);
            }

            if (extVal!=0) {
                errors = EnumErrors.ERROR_IN_COMPRESSING;
            }

        log.info("PROCESS LAUNCHING: {}", errors);

        return errors;

    }

    public static String getJsonInfoOfVideo(String filePath) {
        String result = "";
        List<String> cmd = new ArrayList<>();

        cmd.add("ffprobe");

        cmd.add("-v");
        cmd.add("quiet");

        cmd.add("-print_format");
        cmd.add("json");

        cmd.add("-show_format");
        cmd.add("-show_streams");

        cmd.add(filePath);

        log.info(cmd.toString());

        try {
            log.info("Sending to FFPROBE MEDIA INFO process {}", cmd);
            result = new ProcessExecutor().command(cmd)
                    .destroyOnExit()
                    .readOutput(true).execute()
                    .outputUTF8();

        } catch (Exception ex) {
            log.error("ERROR IN TRYING to check MEDIA INFO", ex);
        }
        log.info("STDERR: {}", result);

        return result;

    }

    public static String preVideoLiveProcess(String filePath) {
        String result = "";
        List<String> cmd = new ArrayList<>();

        cmd.add("ffmpeg");

        cmd.add("-i");
        cmd.add(filePath);

        cmd.add("-itsoffset");
        cmd.add("0.3");

        cmd.add("-i");
        cmd.add(filePath);

        cmd.add("-c:v");
        cmd.add("copy");

        cmd.add("-c:a");
        cmd.add("libfdk_aac");

        cmd.add("-f");
        cmd.add("flv");

        cmd.add(filePath + ".pre");

        log.info(cmd.toString());
        try {
            log.info("Sending to preVideoLiveProcess MEDIA INFO process {}", cmd);
            result = new ProcessExecutor().command(cmd)
                    .destroyOnExit()
                    .readOutput(true).execute()
                    .outputUTF8();

        } catch (Exception ex) {
            log.error("ERROR IN TRYING to check MEDIA INFO", ex);
        }

        log.info("STDERR: {}", result);

        return result;

    }

    public static String preVideoJoinAudioProcess(String fileAudio, String fileVideo, String fileOut) {

        List<String> cmd = new ArrayList<>();

        cmd.add("ffmpeg");

        cmd.add("-i");
        cmd.add(fileVideo);

        cmd.add("-itsoffset");
        cmd.add("0.5");

        cmd.add("-i");
        cmd.add(fileAudio);

        cmd.add("-map");
        cmd.add("0:0");

        cmd.add("-map");
        cmd.add("1:0");

        cmd.add("-c:v");
        cmd.add("copy");

        cmd.add("-c:a");
        cmd.add("libfdk_aac");

        cmd.add("-filter_complex");
        cmd.add("aresample=async=1");

        cmd.add("-ar");
        cmd.add("44100");

        cmd.add("-shortest");

        cmd.add(fileOut);

        log.info(cmd.toString());
        String result = "";
        try {
            log.info("Sending to preVideoJoinAudioProcess process {}", cmd);
            result = new ProcessExecutor().command(cmd)
                    .destroyOnExit()
                    .readOutput(true).execute()
                    .outputUTF8();

        } catch (Exception ex) {
            log.error("ERROR IN TRYING to check MEDIA INFO", ex);
        }
        log.info("STDERR: {}", result);

        return result;

    }

    public static synchronized void videoAnalyze(String filePath) {

        List<String> cmd = new ArrayList<>();

        cmd.add("/opt/encoder/ffmpeg");

        cmd.add("-probesize");
        cmd.add("10M");

        cmd.add("-analyzeduration");
        cmd.add("10M");

        cmd.add("-i");
        cmd.add(filePath);

        cmd.add("-c:v");
        cmd.add("libx264");

        cmd.add("-crf");
        cmd.add("15");

        cmd.add("-bufsize");
        cmd.add("1500k");

        cmd.add("-maxrate");
        cmd.add("1500k");

        cmd.add("-deinterlace");

        cmd.add("-pix_fmt");
        cmd.add("yuv420p");

        cmd.add("-profile:v");
        cmd.add("baseline");

        cmd.add("-level");
        cmd.add("3.0");

        cmd.add("-preset");
        cmd.add("ultrafast");

        cmd.add("-c:a");
        cmd.add("libvo_aacenc");

        cmd.add("-ar");
        cmd.add("44100");

        cmd.add("-threads");
        cmd.add("0");

        cmd.add("-f");
        cmd.add("mp4");

        cmd.add(filePath + ".mp4");

        log.info(cmd.toString());

        String result = "";
        try {
            log.info("Sending to videoAnalyze process {}", cmd);
            result = new ProcessExecutor().command(cmd)
                    .destroyOnExit()
                    .readOutput(true).execute()
                    .outputUTF8();

        } catch (Exception ex) {
            log.error("ERROR IN TRYING to check MEDIA INFO", ex);
        }

        log.info("STDERR: {}", result);
        FileUtil.rename(filePath + ".mp4", filePath);

    }

    public EnumErrors extractFrameFromVideo(IVideoMuxer muxer, String output) {
        log.debug("THUMB FRAMING");
        EnumErrors errors = launch(muxer.buildThumbnails());


        return errors;
    }

    public static void generateVideoThumbnails(String input, String output, int rotation) {
        log.info("THUMB GENERIC {}", input);


        List<String> buildCmd = new CopyOnWriteArrayList<String>();
        String libBin = "mplayer";

        buildCmd.add(libBin);

        buildCmd.add("-input");
        buildCmd.add("conf=/opt/input.conf");

        buildCmd.add("-ss");
        buildCmd.add("3");

        switch (rotation) {
            case 90: {
                buildCmd.add("-vf");
                buildCmd.add("screenshot,rotate=1");
                break;
            }
            case 270: {
                buildCmd.add("-vf");
                buildCmd.add("screenshot,rotate=2");
                break;
            }
            case 180: {
                buildCmd.add("-vf");
                buildCmd.add("screenshot,rotate=0,rotate=1");
                break;
            }
            default: {
                buildCmd.add("-vf");
                buildCmd.add("screenshot");
                break;
            }
        }

        buildCmd.add("-frames");
        buildCmd.add("1");

        buildCmd.add("-vo");
        buildCmd.add("jpeg:progressive:nobaseline:outdir=" + output);

        buildCmd.add("-nosound");
        buildCmd.add(input);

        log.info(buildCmd.toString());
        try {
            String extVal = new ProcessExecutor().command(buildCmd)
                    .destroyOnExit()
                    .readOutput(true).execute()
                    .outputUTF8();
            log.info("Exit value of executor {}", extVal);
        } catch (Exception ex) {
            log.error("ERROR IN TRYING to check MEDIA INFO", ex);
        }

    }

    public EnumErrors encodeToWebP(String pathFile) {

        List<String> buildCmd = new ArrayList<String>();
        String libBin = "/opt/encoder/webp/bin/cwebp";
        buildCmd.add(libBin);

        buildCmd.add("-mt");
        buildCmd.add(pathFile);

        buildCmd.add("-o");
        buildCmd.add(pathFile);

        return launch(buildCmd);
    }

    public EnumErrors decodeFromWebP(String pathFile) {

        log.info("IT`S WebP");
        List<String> buildCmd = new ArrayList<String>();
        String libBin = "/opt/encoder/webp/bin/dwebp";
        buildCmd.add(libBin);

        buildCmd.add("-mt");
        buildCmd.add(pathFile);
        buildCmd.add("-o");
        buildCmd.add(pathFile);

        return launch(buildCmd);
    }

    public EnumErrors moveAtom(String input) {

        List<String> cmd = new CopyOnWriteArrayList<>();
        log.info("MOVE ATOM");

        cmd.add("/opt/encoder/movatome.sh");
        cmd.add(input);

        return launch(cmd);

    }

    public synchronized static int thumbnailer(String input, String output) {

        List<String> cmd = new CopyOnWriteArrayList<>();
        log.info("ImageMagic initialization {}", input);

        cmd.add("/opt/encoder/thumbnail.sh");
        cmd.add(input);
        cmd.add(output);

        log.info(cmd.toString());
        return executer(cmd);

    }

    public synchronized static int imageBuilder(String input, String output) {

        List<String> cmd = new CopyOnWriteArrayList<>();
        log.info("ImageMagic initialization {} {}", input, output);

        cmd.add("/opt/encoder/cimage.sh");
        cmd.add(String.valueOf(input));
        cmd.add(String.valueOf(output));

        log.info(cmd.toString());

        return executer(cmd);

    }

    private static int executer(List<String> cmd){
        int done;
        try {
            String extVal = new ProcessExecutor().command(cmd)
                    .destroyOnExit()
                    .readOutput(true).execute()
                    .outputUTF8();
            log.info("Exit value of executor {}", extVal);
            done = extVal.contains("Done")?1:0;
        } catch (Exception ex) {
            done = 0;
            log.error("ERROR IN TRYING to check MEDIA INFO", ex);
        }

        return done;
    }

}
