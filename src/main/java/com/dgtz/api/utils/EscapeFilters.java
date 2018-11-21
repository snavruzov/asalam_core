package com.dgtz.api.utils;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 6/15/14
 */
public final class EscapeFilters {

    public EscapeFilters() {
    }

    public static String xssEscape(String text) {

        text = org.apache.commons.lang3.StringUtils.replaceEach(text,
                new String[]{"&", "\"", "/", "<?", "?>", "\r", "\n", "\t"},
                new String[]{"&amp;", "&#x27;", "&#x2F;", "#", "#", "", "", ""});

        return text.trim();

    }
}
