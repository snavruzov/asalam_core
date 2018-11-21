package com.dgtz.api.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Base64;

/**
 * Created by sardor on 5/11/16.
 */
public class HMAC {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private static String toBase64String(byte[] bytes) throws UnsupportedEncodingException {
        String base64encodedString = Base64.getEncoder().encodeToString(bytes);

        System.out.println(base64encodedString);
        return URLEncoder.encode(base64encodedString, "UTF-8");
    }

    public static String calculateRFC2104HMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toBase64String(mac.doFinal(data.getBytes()));
    }

}
