package com.dgtz.api.utils;

import com.dgtz.api.security.HttpClientWrapper;
import org.slf4j.LoggerFactory;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 5/26/14
 */
public class HttpClientFactory extends HttpClientWrapper {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);


    public HttpClientFactory() {
    }

    @Override
    public Object doRequestGet(Class clazz, String subUrl) {
        return super.doRequestGet(clazz, subUrl);
    }

    @Override
    public void doRequestGet(String subUrl) {
        super.doRequestGet(subUrl);
    }


}
