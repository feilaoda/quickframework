package com.quickpaas.framework.utils;

import com.google.common.io.BaseEncoding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class KeyKit {
    public static final BaseEncoding HEX = BaseEncoding.base16().lowerCase();

    public static String hash256(String text) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return "HASH_ERROR";
        }

        final byte bytes[] = digest.digest(text.getBytes());
        return HEX.encode(bytes);
    }

    public static String sha1(byte[] bytes) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            return "HASH_ERROR";
        }

        final byte res[] = digest.digest(bytes);
        return HEX.encode(res);
    }
}
