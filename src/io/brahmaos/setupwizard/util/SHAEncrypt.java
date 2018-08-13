package io.brahmaos.setupwizard.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAEncrypt {
    public static String shaEncrypt(String strSrc, String algorithm) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance(algorithm);
            md.update(bt);
            strDes = bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    public static String bytes2Hex(byte[] bts) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bts.length; i++) {
            String hex = Integer.toHexString(bts[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
}
