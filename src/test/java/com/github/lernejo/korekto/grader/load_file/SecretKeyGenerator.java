package com.github.lernejo.korekto.grader.load_file;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey key = keyGenerator.generateKey();
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println(encodedKey);
    }
}
