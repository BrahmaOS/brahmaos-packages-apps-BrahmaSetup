package io.brahmaos.setupwizard.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES128 {
    /**
     * Encrypt
     *
     * @param content the clear text which want to be encrypted.
     * @param password the secret key
     *
     * @return the encrypted hex string
     */
    public static String encrypt(String content, String password) {

        try {
            Cipher cipher = Cipher.getInstance("AES");
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, genKey(password));
            byte[] result = cipher.doFinal(byteContent);
            return parseByte2HexStr(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * Decrypt
     *
     * @param
     * @return
     */
    public static String decrypt(String content, String password) {

        try {
            byte[] decryptFrom = parseHexStr2Byte(content);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, genKey(password));
            byte[] result = cipher.doFinal(decryptFrom);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get SecretKeySpec according to the password
     * @return
     */
    private  static SecretKeySpec genKey(String password){
        byte[] enCodeFormat = {0};
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            enCodeFormat = secretKey.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(enCodeFormat, "AES");
    }


    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}