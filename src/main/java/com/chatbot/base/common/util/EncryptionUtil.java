package com.chatbot.base.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {
    private final static String KEY = "W9h7Xz8qL2r5T6v1Y4s3P0g8N5d2Q7k1";
    // 32바이트 키를 환경변수에서 가져옴
    public static String getKey() {
        String key = KEY;
        if (key == null || key.isEmpty()) {
            throw new IllegalStateException("환경변수 AES_SECRET_KEY가 설정되지 않았습니다.");
        }
        if (key.length() != 32) {
            throw new IllegalArgumentException("AES 키는 32바이트(256비트)여야 합니다.");
        }
        return key;
    }

    // AES 암호화 (URL-safe Base64 적용)
    public static String encrypt(String key, String plaintext) throws Exception {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16]; // 16바이트 IV
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        // ✅ URL-safe Base64 인코딩 적용
        return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
    }

    // AES 복호화 (URL-safe Base64 적용)
    public static String decrypt(String key, String encryptedText) throws Exception {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // ✅ URL-safe Base64 디코딩 적용
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encryptedText);

        byte[] iv = new byte[16];
        byte[] encrypted = new byte[decodedBytes.length - 16];

        System.arraycopy(decodedBytes, 0, iv, 0, 16);
        System.arraycopy(decodedBytes, 16, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

}
