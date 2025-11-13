package com.chatbot.base.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MailServiceTest {
    @Autowired
    private MailService mailService;

    @Autowired
    private FaxSender faxSender;

    @Test
    void sendMailWithImageAttachments() throws Exception {


        // 1️⃣ 이미지 → PDF 변환
        File pdfFile = mailService.convertImageUrlToFaxTiff("https://blog.kakaocdn.net/dna/xYFz9/dJMcai9tqgQ/AAAAAAAAAAAAAAAAAAAAAH94YqJ4Y1MfCKMT-pGNqOpaICk8yVFeTdSgRbDQWvT_/img.jpg?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1764514799&allow_ip=&allow_referer=&signature=czaopC1TOVg1LocGzKDD2OA7kLo%3D", "123123123");

        // 2️⃣ 메일 전송 (PDF 첨부)
        mailService.sendMailWithPdfAttachment(
                "vinsulill@gmail.com",
                "[" + 123123 + "] " + 123123,
                "연락처: " + 123123,
                pdfFile
        );
//
//         faxSender.uploadPdfFileAndSendFax(pdfFile);

//        faxSender.sendFax("0082647249454",pdfFile, Map.of());
//        System.out.println("s = " + s);


    }
}