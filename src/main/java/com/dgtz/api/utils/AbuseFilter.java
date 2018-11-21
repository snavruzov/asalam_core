package com.dgtz.api.utils;

import com.dgtz.api.security.HttpClientWrapper;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 6/10/14
 */
public class AbuseFilter extends HttpClientWrapper {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AbuseFilter.class);

    //private static final String FILTER_URL = "http://www.wdyl.com/profanity?q=%s";

    public String filter(String text) throws UnsupportedEncodingException {
        text = org.apache.commons.lang3.StringUtils.normalizeSpace(text);


        log.debug("FILTERED TEXT", text);
        return text;
    }


}
