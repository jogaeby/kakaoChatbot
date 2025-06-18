package com.chatbot.base.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionUtilTest {

    @Test
    void getKey() {
    }

    @Test
    void encrypt() throws Exception {
        String phone = "01077131548";
        String id = "6_1750055844290";
        String phoneEncrypt = EncryptionUtil.encrypt(EncryptionUtil.getKey(), phone);
        String idEncrypt = EncryptionUtil.encrypt(EncryptionUtil.getKey(), id);
        String phoneDecrypt = EncryptionUtil.decrypt(EncryptionUtil.getKey(), phoneEncrypt);
        String idDecrypt = EncryptionUtil.decrypt(EncryptionUtil.getKey(), idEncrypt);
        System.out.println("idEncrypt = " + phoneEncrypt);
        System.out.println("phoneEncrypt = " + idEncrypt);
        System.out.println("idEncrypt = " + phoneDecrypt);
        System.out.println("phoneEncrypt = " + idDecrypt);
    }

    @Test
    void decrypt() throws Exception {
        String phone = "01077131548";
        String receiptId = "6_1750055844290";

        String encryptPhone = EncryptionUtil.encrypt(EncryptionUtil.getKey(), phone);
        String encryptReceiptId = EncryptionUtil.encrypt(EncryptionUtil.getKey(), receiptId);
        String url = "localhost:8080" + "/receipt/"+encryptPhone+"/"+encryptReceiptId;
        System.out.println("url = " + url);
    }
}