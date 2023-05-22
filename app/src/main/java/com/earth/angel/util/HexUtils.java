package com.earth.angel.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HexUtils {


    /**
     * 16进制数字字符集
     */
    private static String hexString="0123456789ABCDEF";

    /**
     * 转化字符串为十六进制编码
     */
    public static String toHexString(String s){
        String str="";
        for (int i=0;i<s.length();i++){
            int ch = (int)s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 转化十六进制编码为字符串
     */
    public static String toStringHex(String s){
        byte[] baKeyword = new byte[s.length()/2];
        for(int i = 0; i < baKeyword.length; i++){
            try{
                baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16));
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        try{
            s = new String(baKeyword, "utf-8");//UTF-16le:Not
        }catch (Exception e1){
            e1.printStackTrace();
        }
        return s;
    }

    public static byte[] hmacSHA256(byte[] key, String content) throws Exception {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(content.getBytes());
        } catch (Exception e) {
            throw new Exception(
                    "Unable to calculate a request signature: "
                            + e.getMessage(), e);
        }
    }




    public static String getSHA256(String str){
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = encodeHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    public static String encode(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return toHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }

    /**
     * 字节数组转16进制编码字符串
     *<p>Title: toHexString</p>
     *<p>Description: </p>
     *@param byteArray
     *@return
     */
    public static String toHexString(byte[] byteArray) {
        String str = null;
        if (byteArray != null && byteArray.length > 0) {
            StringBuffer stringBuffer = new StringBuffer(byteArray.length);
            for (byte byteChar : byteArray) {
                stringBuffer.append(String.format("%02X", byteChar));
            }
            str = stringBuffer.toString();
        }
        return str;
    }
    private static final char[] DIGITS_LOWER;
    private static final char[] DIGITS_UPPER;
    public static String encodeHexString(byte[] data) {
        return new String(encodeHex(data));
    }
    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }
    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }
    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;

        for(int var5 = 0; i < l; ++i) {
            out[var5++] = toDigits[(240 & data[i]) >>> 4];
            out[var5++] = toDigits[15 & data[i]];
        }

        return out;
    }
    static {
        DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    }
}
