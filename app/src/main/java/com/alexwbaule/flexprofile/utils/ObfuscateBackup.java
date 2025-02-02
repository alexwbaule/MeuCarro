package com.alexwbaule.flexprofile.utils;

/**
 * Created by alex on 05/09/15.
 */
public class ObfuscateBackup {

    public ObfuscateBackup() {

    }

    static String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz,();";
    static String target = "Q9A8ZWS7XEDC6RFVT5GBY4HNU3J2MI1KO0LPmlpoknjihbvg#!*+tfcxdreswzaq|{";

    public static String obfuscate(String s) {
        char[] result = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int index = source.indexOf(c);
            if (index == -1) {
                result[i] = c;
            } else {
                result[i] = target.charAt(index);
            }
        }
        return new String(result);
    }

    public static String unobfuscate(String s) {
        char[] result = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int index = target.indexOf(c);
            if (index == -1) {
                result[i] = c;
            } else {
                result[i] = source.charAt(index);
            }
        }
        return new String(result);
    }
}
