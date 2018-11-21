package com.dgtz.api.feature;

import com.dgtz.api.beans.EncodingResponse;
import com.dgtz.api.beans.Response;

/**
 * Created by sardor on 5/8/16.
 */
public interface EncodingImpl {

    Response videoMux();
    EncodingResponse liveMux();
    Response thumbMux();
}
