package com.dgtz.api.settings;

import com.dgtz.api.beans.Response;

import java.util.List;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 4/21/14
 */
public interface IVideoMuxer {

    List<String> build();

    Response buildFast();

    List<String> buildLive();

    List<String> buildWebM();

    List<String> buildLiveWebM();

    List<String> buildThumbnails();

    List<String> transpose();
}
