package com.sm.lt.infrastructure;

public class RandomUtils {

    private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String NUMERIC_STRING = "0123456789";
    private static final String HEX_NUMERIC_STRING = "0123456789abcdef";

    public static String randomAlphaNumeric(int length) {
        return randomString(length, ALPHA_NUMERIC_STRING);
    }

    public static String randomNumeric(int length) {
        return randomString(length, NUMERIC_STRING);
    }

    public static String randomHexNumeric(int length) {
        return randomString(length, HEX_NUMERIC_STRING);
    }

    private static String randomString(int length, String allowedCharacters) {
        StringBuilder builder = new StringBuilder();
        while (length-- != 0) {
            int character = (int) (Math.random() * allowedCharacters.length());
            builder.append(allowedCharacters.charAt(character));
        }
        return builder.toString();
    }
}