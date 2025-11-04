package com.chatbot.base.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;

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
        File pdfFile = mailService.convertImageUrlToPdf("https://cdn.enewstoday.co.kr/news/photo/202211/1611750_668106_3353.png", "123123123");

//        // 2️⃣ 메일 전송 (PDF 첨부)
//        boolean mailSuccess = mailService.sendMailWithPdfAttachment(
//                "vinsulill@gmail.com",
//                "[" + 123123 + "] " + 123123,
//                "연락처: " + 123123,
//                pdfFile
//        );

        String s = faxSender.uploadPdfFileToSolapi(pdfFile);

        faxSender.sendFax(s,"010-8776-9454","0647249454");
        System.out.println("s = " + s);


    }
}