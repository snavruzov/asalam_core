package com.dgtz.api.utils.gravatar;

/**
 * Created by sardor on 4/27/16.
 */
public class GravatarDownloadException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GravatarDownloadException(Throwable cause) {
        super("Gravatar could not be downloaded: " + cause.getMessage(), cause);
    }

}
