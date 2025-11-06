package com.chatbot.base.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class FaxSenderTest {
    @Autowired
    private FaxSender faxSender;

    @Autowired
    private MailService mailService;
    @Test
    void uploadImageFromUrl() throws Exception {
        String s = faxSender.uploadImageFromUrl("https://cdn.enewstoday.co.kr/news/photo/202211/1611750_668106_3353.png");
        boolean b = faxSender.sendFax("123213123", "010-8776-9454", "+82647249454");
        System.out.println("b = " + b);
    }

    @Test
    void convertResourceImageToTiff() throws Exception {

        File file = mailService.convertResourceImageToTiff("static/testimage.jpeg", "테스트");
        mailService.sendMailWithPdfAttachment("vinsulill@gmail.com","테스트","테스트",file);

    }

    @Test
    void convertImageUrlToFaxTiff() throws Exception {
        File file = mailService.convertImageUrlToTiff("https://blog.kakaocdn.net/dna/xYFz9/dJMcai9tqgQ/AAAAAAAAAAAAAAAAAAAAAH94YqJ4Y1MfCKMT-pGNqOpaICk8yVFeTdSgRbDQWvT_/img.jpg?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1764514799&allow_ip=&allow_referer=&signature=czaopC1TOVg1LocGzKDD2OA7kLo%3D", "테스트");
        faxSender.uploadPdfFileAndSendFax(file);
//        mailService.sendMailWithPdfAttachment("vinsulill@gmail.com","테스트","테스트",file);
//        faxSender.sendFax("+82647249454",file,Map.of());
    }
}