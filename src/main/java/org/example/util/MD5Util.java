package org.example.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    public static String md5(String s) {
        return DigestUtils.md5Hex(s);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassToFormPass(String inputPass) {
        String s = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(s);
    }

    public static String formPassToDBPass(String formPass, String salt) {
        String s = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(s);
    }

    public static String inputPassToDBPass(String input, String saltDB) {
        String formPass = inputPassToFormPass(input);
        return formPassToDBPass(formPass, saltDB);

    }

    public static void main(String[] args) {
        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
    }
}
