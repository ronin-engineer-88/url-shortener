package com.roninhub;

public class EncodeUtility {

    public static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encodeBase62(long value) {
        StringBuilder encoded = new StringBuilder();
        while (value > 0) {
            int remainder = (int) (value % 62);
            encoded.append(BASE62_CHARS.charAt(remainder));
            value /= 62;
        }
        return encoded.reverse().toString();
    }
}
