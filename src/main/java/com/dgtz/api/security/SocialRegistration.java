package com.dgtz.api.security;

import com.dgtz.api.beans.*;
import com.dgtz.api.constants.Formula;

/**
 * Created by root on 1/17/14.
 */
public final class SocialRegistration extends HttpClientWrapper {

    public SocialRegistration() {

    }

    public FBInfo authSocialRequest(String token) {
        FBInfo fbInfo = (FBInfo) doFBRequestGet(Formula.FB_URL.concat(token));

        return fbInfo;
    }

    public VKInfo authVKRequest(String token) {
        VKInfo vkInfo = (VKInfo) doVKRequestGet(String.format(Formula.VK_URL, token));

        return vkInfo;
    }

    public GOOGInfo authGoogleRequest(String token) {
        GOOGInfo googInfo = (GOOGInfo) doGoogleRequestGet(Formula.GOOAUTH_URL,token);

        return googInfo;
    }

    public GoogleInfo authGoogleOAuthRequest(String token) {
        GoogleInfo googInfo = (GoogleInfo) doGoogleAuthRequestGet(String.format(Formula.GOOGLE_URL, token));

        return googInfo;
    }

    public TwitterInfo authTwitterRequest(String header) {
        TwitterInfo twInfo = (TwitterInfo) doTwitterRequestGet(Formula.TWITTER_URL, header);

        return twInfo;
    }
}
