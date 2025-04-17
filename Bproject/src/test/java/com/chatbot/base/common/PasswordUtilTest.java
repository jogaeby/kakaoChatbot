package com.chatbot.base.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PasswordUtilTest {
    @Autowired
    private PasswordUtil passwordUtil;
    @Test
    void encodePassword() {
        String s = passwordUtil.encodePassword("dao2025!");
        System.out.println("s = " + s);

    }

    @Test
    void matches() {
    }
}