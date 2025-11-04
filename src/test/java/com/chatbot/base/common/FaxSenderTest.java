package com.chatbot.base.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class FaxSenderTest {
    @Autowired
    private FaxSender faxSender;
    @Test
    void uploadImageFromUrl() throws Exception {
        String s = faxSender.uploadImageFromUrl("https://cdn.enewstoday.co.kr/news/photo/202211/1611750_668106_3353.png");
        boolean b = faxSender.sendFax("123213123", "010-8776-9454", "0647249454");
        System.out.println("b = " + b);
    }
}