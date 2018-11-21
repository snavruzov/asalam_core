package com.dgtz.api.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 2/3/14.
 */
public class MediaJSObject implements Serializable {

    private static final long serialVersionUID = 1L;

    private MediaTechFormats format;
    private List<MediaStreamFormat> streams;

    public MediaTechFormats getFormat() {
        return format;
    }

    public void setFormat(MediaTechFormats format) {
        this.format = format;
    }

    public List<MediaStreamFormat> getStreams() {
        return streams;
    }

    public void setStreams(List<MediaStreamFormat> streams) {
        this.streams = streams;
    }
}
