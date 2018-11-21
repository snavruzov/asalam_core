package com.dgtz.api.compresser;

import com.dgtz.api.beans.MediaInfo;
import com.dgtz.api.constants.Media;
import com.dgtz.api.enums.EnumDimension;
import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.enums.EnumFileType;
import com.dgtz.api.feature.SaveOriginal;
import com.dgtz.api.settings.IFileManipulate;
import com.dgtz.api.utils.FileUtil;
import com.dgtz.api.utils.ImageConverter;
import com.dgtz.api.utils.ShellCommands;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 3/24/14
 */
public final class CompressBuilder {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CompressBuilder.class);


    public CompressBuilder() {
    }

    protected synchronized EnumErrors build(String source, String idUser, String idMedia, Short duration, EnumErrors errors) {

        log.debug("ERRORS: {}", errors);
        if (errors == EnumErrors.NO_ERRORS) {
            log.debug("SOURCE: {}", source);

            ShellCommands shellCommands = new ShellCommands();
            errors = shellCommands.launchEncoding(source, new String[]{idUser, idMedia}, duration);


        } else {
            errors = EnumErrors.ERROR_IN_COMPRESSING;
            log.debug(errors.toString());
        }


        return errors;

    }

    /*ROTATION ANGLE POINT 1-90_ClockWise, 0-default, -1-90_CounterClockwise, 2-180_upsideDown*/
    protected EnumErrors rotate(String source, final String idUser, final String idMedia, EnumErrors errors, final int rotation) {

        log.debug("ERRORS: {}", errors);
        if (errors == EnumErrors.NO_ERRORS) {

            IFileManipulate fileManipulate = new SaveOriginal();

            log.debug("SOURCE: {}", source);
            log.debug("START ENCODING...");

            ShellCommands shellCommands = new ShellCommands();
            MediaInfo mediaInfo = fileManipulate.info(source, EnumFileType.VIDEO);
            mediaInfo.setClockPointer(rotation);
            File file = new File(source);
            if (!file.exists()) {
                source = Media.PATH_SETTINGS.getMediaPath() + idUser + "/original/" + idMedia;
            }

            errors = shellCommands.transposeVideo(mediaInfo, source, new String[]{idUser, idMedia});

            if (errors == EnumErrors.NO_ERRORS) {

                int rt = 0;
                switch (rotation) {
                    case 1: {
                        rt = 90;
                        break;
                    }
                    case -1: {
                        rt = -90;
                        break;
                    }
                    case 2: {
                        rt = 180;
                        break;
                    }
                }

                FileUtil.rename(
                        Media.PATH_SETTINGS.getMediaPath() + idUser + "/original/" + idMedia + "_encoded_temp.mp4",
                        Media.PATH_SETTINGS.getMediaPath() + idUser + "/original/" + idMedia + "_encoded.mp4");

                ImageConverter imageConverter = new ImageConverter();
                //imageConverter.convertToThumb(Media.PATH_SETTINGS.getMediaPath() + idUser + "/thumbnail/" + idMedia + ".jpg", rt);

                FileUtil.rename(
                        Media.PATH_SETTINGS.getMediaPath() + idUser + "/thumbnail/" + idMedia + "_temp.jpg",
                        Media.PATH_SETTINGS.getMediaPath() + idUser + "/thumbnail/" + idMedia + ".jpg");

                EnumDimension dm = EnumDimension.HD;
                {
                    FileUtil.rename(
                            Media.PATH_SETTINGS.getMediaPath() + idUser + "/video/" +
                                    idMedia + "_" + dm.size + "_temp.mp4",
                            Media.PATH_SETTINGS.getMediaPath() + idUser + "/video/" +
                                    idMedia + "_" + dm.size + ".mp4");
                }

                for (int ii = 1; ii <= 3; ii++) {
                    imageConverter = new ImageConverter();
                    //imageConverter.convertToThumb(Media.PATH_SETTINGS.getMediaPath() + idUser + "/thumbnail/instance/" + idMedia + "_" + ii + ".jpg", rt);
                }

            } else {

                Thread thread = new Thread() {
                    public void run() {
                        try {
                            Files.deleteIfExists(Paths.get(
                                    Media.PATH_SETTINGS.getMediaPath() + idUser + "/original/" + idMedia + "_encoded_temp.mp4"));
                            Files.deleteIfExists(Paths.get(
                                    Media.PATH_SETTINGS.getMediaPath() + idUser + "/thumbnail/" + idMedia + "_temp.jpg"));

                            EnumDimension dm = EnumDimension.HD;
                            {
                                Files.deleteIfExists(Paths.get(
                                        Media.PATH_SETTINGS.getMediaPath() + idUser + "/video/" + idMedia + "_" + dm.size + "_temp.mp4"));
                            }
                        } catch (IOException e) {
                            log.error("ERROR IN MAIN API DELETE TEMPS {}", e);
                        }
                    }
                };

                thread.setName("remove_tmp_thread_" + System.currentTimeMillis());
                thread.start();
            }
        }

        return errors;
    }

    protected synchronized EnumErrors buildLive(String source, String idUser, String idMedia, int rotation, Short duration, EnumErrors errors) {

        log.debug("LIVE COMPRESSING: {}", errors.toString());

        if (errors == EnumErrors.NO_ERRORS) {

            log.debug(source);

            ShellCommands shellCommands = new ShellCommands();
            log.debug("LIVE DURATION: {}", duration);
            errors = shellCommands.launchLiveEncoding(source,
                    new String[]{idUser, idMedia}, duration, rotation);

        } else {
            errors = EnumErrors.ERROR_IN_COMPRESSING;
            log.debug(errors.toString());
        }

        return errors;
    }
}
