package com.github.lernejo.korekto.grader.load_file;

import java.util.regex.Matcher;

public class StringUtils {

    public static String safeEscapeElide(String s) {
        if (s == null) {
            return "";
        } else {
            String s1 = s.trim().replaceAll("\n", Matcher.quoteReplacement("\\n"));
            if (s1.length() > 80) {
                return s1.substring(0, 77) + "...";
            }
            return s1;
        }
    }

    public static String safeLowerTrim(String s) {
        if (s == null) {
            return "";
        } else {
            return s.trim().toLowerCase();
        }
    }
}