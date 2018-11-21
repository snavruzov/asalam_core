package com.dgtz.api.compresser;

import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.feature.SemaphoreConnector;
import com.dgtz.api.utils.ShellCommands;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 12/15/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompressFactory {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger("transCoder");


    private String source;
    private String idUser;
    private String idMedia;
    private Short duration;
    private Short setting;
    private boolean isLive;
    private int rotation;

    private EnumErrors errors = EnumErrors.NO_ERRORS;

    public CompressFactory() {
    }

    /*SETTING: 1-rotate video manually*/
    public CompressFactory(String source, String idUser, String idMedia, Short duration, boolean isLive, Short setting) {
        this.source = source;
        this.idUser = idUser;
        this.idMedia = idMedia;
        this.duration = duration;
        this.isLive = isLive;
        this.setting = setting;

    }

    public CompressFactory(String source, String idUser, String idMedia, Short duration, boolean isLive, int rotation, Short setting) {
        this.source = source;
        this.idUser = idUser;
        this.idMedia = idMedia;
        this.duration = duration;
        this.isLive = isLive;
        this.rotation = rotation;
        this.setting = setting;

    }

    public EnumErrors build() throws Exception {

        log.debug(" ACCEPT SEMAPHORE ");
        Semaphore mSemaphore = SemaphoreConnector.getInstance();
        //EnumErrors enumErrors = EnumErrors.NO_ERRORS;
        try {
            if (mSemaphore.tryAcquire(180, TimeUnit.SECONDS)) {
                log.debug("PROHIBITED SECTION {}", Thread.currentThread().getName());

                log.debug("idUser {}, source {}, idMedia {}, rotation, sett {}", new Object[]{idUser, source, idMedia, rotation, setting});
                if (source != null && idUser != null && idMedia != null) {
                    log.debug("CREATE NEW INSTANCE BUILDER... ");
                    CompressBuilder builder = new CompressBuilder();
                    if (setting != 0) {
                        errors = builder.rotate(source, idUser, idMedia, errors, rotation);
                    } else if (!isLive) {
                        errors = builder.build(source, idUser, idMedia, duration, errors);
                    } else {
                        errors = builder.buildLive(source, idUser, idMedia, rotation, duration, errors);
                    }
                }

            } else {
                errors = EnumErrors.UNKNOWN_ERROR;
                log.debug("SORRY TIMEOUT");
            }
        } catch (Exception ex) {
            log.error("ERROR IN MAIN API", ex);
            errors = EnumErrors.ERROR_IN_COMPRESSING;
        } finally {
            log.debug("PASS SEMAPHORE ");  /* release semaphore */
            mSemaphore.release();
        }
        log.debug("THREAD LENGTH: {}", mSemaphore.getQueueLength());

        return errors;
    }

}
