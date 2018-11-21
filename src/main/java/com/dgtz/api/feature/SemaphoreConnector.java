package com.dgtz.api.feature;

import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 6/15/14
 */
public final class SemaphoreConnector {
    private static Semaphore mSemaphore = null;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SemaphoreConnector.class);


    public static Semaphore getInstance() {
        if (mSemaphore == null) {
            log.debug("SEMAPHORE IS NULL INIT NEW THREAD");
            mSemaphore = new Semaphore(5);
        }

        return mSemaphore;
    }

    private SemaphoreConnector() {

    }

}
