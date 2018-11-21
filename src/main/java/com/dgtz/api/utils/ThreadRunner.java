package com.dgtz.api.utils;

import com.brocast.riak.api.beans.DcMediaEntity;
import com.dgtz.api.contents.MediaShelf;
import com.dgtz.api.contents.UsersShelf;
import com.dgtz.api.enums.EnumErrors;

import com.dgtz.api.feature.SystemDelivery;
import com.dgtz.api.settings.ISystemDelivery;
import com.dgtz.db.api.domain.Notification;
import com.dgtz.db.api.enums.EnumNotification;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.slf4j.LoggerFactory;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 9/23/14
 */
public class ThreadRunner {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ThreadRunner.class);
    private DcMediaEntity mediaInfoMem;
    private long idMedia;

    public ThreadRunner(long idMedia) {
        this.idMedia = idMedia;
        mediaInfoMem = new MediaShelf().retrieveMediaByIdValue(idMedia);
    }


    public EnumErrors pushMultiCastFollowers() {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        if (mediaInfoMem != null) {
            try {
                Thread thread = new Thread() {
                    public void run() {
                        String val = null;
                        try {
                            val = (String) RMemoryAPI.getInstance()
                                    .pullElemFromMemory(Constants.LIVE_KEY + "live:" + idMedia, String.class);
                        } catch (Exception e) {
                            log.error("ERROR IN MAIN API, THREAD RUNNER::: ", e);
                        }
                        if (mediaInfoMem.getIdUser() != 0 && val == null) {
                            String username = RMemoryAPI.getInstance()
                                    .pullHashFromMemory(Constants.USER_KEY + mediaInfoMem.getIdUser(), "username");

                            Notification notification = new Notification();
                            notification.setType(2);
                            notification.setIdFrom(mediaInfoMem.idUser);
                            notification.setIdMedia(idMedia);
                            notification.setText(mediaInfoMem.title);
                            notification.setUsername(username);

                            ISystemDelivery systemDelivery = SystemDelivery
                                    .builder(notification)
                                    .push();

                        }
                    }
                };
                thread.setName("push_indexing-" + System.currentTimeMillis());
                thread.start();

            } catch (Exception ex) {
                log.error("ERROR IN WEB API ", ex);
                errors = EnumErrors.UNKNOWN_ERROR;
            }

        } else {
            errors = EnumErrors.NO_MEDIA_FOUND;
        }

        return errors;
    }

    /*status: 0-OK, 2-ERROR*/
    public EnumErrors   pushOwnerNotification() {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        if (mediaInfoMem != null && (mediaInfoMem.getProgress() == 0 || mediaInfoMem.getProgress() == 2)) {
            log.debug("Push owner mail notification about video status {}",mediaInfoMem.toString());
        } else {
            errors = EnumErrors.NO_MEDIA_FOUND;
        }

        return errors;
    }

    public EnumErrors pushMultiCastFollowers(final boolean isPicture, boolean status) {

        EnumErrors errors = EnumErrors.NO_ERRORS;

        if (mediaInfoMem != null) {
            if (status)
                status = mediaInfoMem.getProgress() == 0;
            else
                status = true;

            final boolean process = status;
            try {
                Thread thread = new Thread() {
                    public void run() {
                        if (mediaInfoMem.getIdUser() != 0 && process) {
                            String username = RMemoryAPI.getInstance()
                                    .pullHashFromMemory(Constants.USER_KEY + mediaInfoMem.getIdUser(), "username");

                            Notification notification = new Notification();
                            notification.setType(2);
                            notification.setIdFrom(mediaInfoMem.idUser);
                            notification.setIdMedia(idMedia);
                            notification.setText(mediaInfoMem.title);
                            notification.setUsername(username);
                            ISystemDelivery systemDelivery = SystemDelivery
                                    .builder(notification)
                                    .push();

                        }
                    }
                };
                thread.setName("push_indexing-" + System.currentTimeMillis());
                thread.start();

            } catch (Exception ex) {
                log.error("ERROR IN WEB API ", ex);
                errors = EnumErrors.UNKNOWN_ERROR;
            }

        } else {
            errors = EnumErrors.NO_MEDIA_FOUND;
        }

        return errors;
    }

}
