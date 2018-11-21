package com.dgtz.api.utils;

import com.eaio.stringsearch.BoyerMooreHorspoolRaita;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/24/13
 * Time: 6:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {

    public StringUtils() {
    }

    public static boolean hasSubString(String search) {
        BoyerMooreHorspoolRaita fastSrc = new BoyerMooreHorspoolRaita();


        int err = fastSrc.searchChars(search.toCharArray(), 0, search.length(),
                "Error".toCharArray(), fastSrc.processString("Error"));
        int frmError = fastSrc.searchChars(search.toCharArray(), 0, search.length(),
                "frames successfully".toCharArray(), fastSrc.processString("frames successfully"));
        if (err == -1 || (err > -1 && frmError > -1))
            err = fastSrc.searchChars(search.toCharArray(), 0, search.length(),
                    "Unknown encoder".toCharArray(), fastSrc.processString("Unknown encoder"));


        return err != -1;
    }

    public static boolean mailMatcher(String email) {
        Pattern rfc2822 =
                Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
        return rfc2822.matcher(email).matches();

    }

    public static String addOneDayToDay() {

        String time = "" + (System.currentTimeMillis() / 1000 + 1500);
        //System.out.println(time);
        return time;

    }

}
