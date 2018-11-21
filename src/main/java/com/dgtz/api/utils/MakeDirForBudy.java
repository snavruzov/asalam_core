package com.dgtz.api.utils;

import com.dgtz.api.enums.EnumErrors;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by sardor on 1/7/14.
 */
public class MakeDirForBudy {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MakeDirForBudy.class);

    public MakeDirForBudy() {
    }

    public static EnumErrors mkdirForFresh(String idUser) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {

            Files.createDirectories(Paths.get(String.format("/opt/media/%s/image", idUser)));
            Files.createDirectories(Paths.get(String.format("/opt/media/%s/original", idUser)));
            Files.createDirectories(Paths.get(String.format("/opt/media/%s/thumbnail/instance", idUser)));
            Files.createDirectories(Paths.get(String.format("/opt/media/%s/video", idUser)));

            Runtime rt = Runtime.getRuntime();
            Process proc;
            int exitVal = -1;
            try {
                proc = rt.exec("chmod -R 777 " + String.format("/opt/media/%s", idUser));
                exitVal = proc.waitFor();
            } catch (Exception e) {
                log.error("ERROR IN MAIN API IN CHMOD", e);
                errors = EnumErrors.MKDIR_ERR;
            }

        } catch (IOException e) {
            errors = EnumErrors.MKDIR_ERR;
            log.error("ERROR IN MAIN API ", e);
        }
        return errors;
    }
}
