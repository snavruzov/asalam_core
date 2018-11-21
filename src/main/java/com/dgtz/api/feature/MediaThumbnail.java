package com.dgtz.api.feature;

import com.dgtz.api.constants.Media;
import com.dgtz.api.utils.FileUtil;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 4/23/14
 */
public class MediaThumbnail {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MediaThumbnail.class);

    public MediaThumbnail() {
    }

    public void changeThumb(Long idMedia, Long idUser, int thumb) throws IOException {
        log.debug("START CHANGING THUMB: iduser {}, idmedia {}, thumb {} ", new Object[]{idUser, idMedia, thumb});

        FileUtil.copyFile(
                Media.PATH_SETTINGS.getMediaPath() + idUser + "/thumbnail/instance/" + idMedia + "_" + thumb + ".jpg",
                Media.PATH_SETTINGS.getMediaPath() + idUser + "/thumbnail/" + idMedia + ".jpg");
    }
}
