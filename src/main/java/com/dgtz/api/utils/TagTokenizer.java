package com.dgtz.api.utils;

import java.text.BreakIterator;
import java.util.*;

/**
 * Created by sardor on 1/11/14.
 */
public class TagTokenizer {

    public TagTokenizer() {
    }

    public static Set<String> normilizeTag(String tag){
        if(tag==null || tag.isEmpty() || tag.length()<2){
            return Collections.emptySet();
        }
        tag = tag.replaceAll("[^\\p{L}\\p{Nd} ,]+", "");
        tag = tag.toLowerCase();
        tag = tag.trim();
        return new HashSet<>(Arrays.asList(tag.split(",")));
    }

    public static String extractTagToSave(String title) {
        title = title.replaceAll("[^\\p{L}\\p{Nd} ,]+", "");

        final Set<String> tags = new HashSet<>();
        for (String str : tokenize(title)) {
            if (str.length() >= 2) {
                tags.add(str);
            }
        }


        String result = tags.toString().replaceAll("[^\\p{L},0-9]+", "");
        result = result.toLowerCase();
        return result;
    }

    public static String tagChecker(String title) {
        title = title.replaceAll("[^\\p{L}\\s]+", "");
        return title;
    }

    private static Set<String> tokenize(final String aString) {
        String inputStr = aString.replace(",", " ");
        Set<String> tokens = new HashSet<>();
        BreakIterator bi = BreakIterator.getWordInstance();
        bi.setText(inputStr);
        int begin = bi.first();
        int end;
        for (end = bi.next(); end != BreakIterator.DONE; end = bi.next()) {
            String t = inputStr.substring(begin, end);
            if (t.trim().length() > 0) {
                tokens.add(t);
            }
            begin = end;
        }
        if (end != -1) {
            tokens.add(inputStr.substring(end));
        }
        return tokens;
    }
}
