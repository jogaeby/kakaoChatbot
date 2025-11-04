package com.chatbot.base.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MailServiceTest {
    @Autowired
    private MailService mailService;
    @Test
    void sendMailWithInlineImages() throws Exception {
        List<String> strings = List.of("https://cdn.m-i.kr/news/photo/202502/1205351_977931_3441.png",
                "https://flexible.img.hani.co.kr/flexible/normal/680/383/imgdb/original/2023/0221/20230221501169.jpg",
                "https://nimage.g-enews.com/phpwas/restmb_allidxmake.php?idx=999&simg=2025062310330109920288320b10e11823574248.jpg");
        mailService.sendMailWithInlineImages("subin.han@visuworks.co.kr","테스트123123",strings);
    }

    @Test
    void sendMailWithImageAttachments() throws Exception {

        List<String> strings = List.of("https://cdn.m-i.kr/news/photo/202502/1205351_977931_3441.png",
                "https://flexible.img.hani.co.kr/flexible/normal/680/383/imgdb/original/2023/0221/20230221501169.jpg",
                "https://nimage.g-enews.com/phpwas/restmb_allidxmake.php?idx=999&simg=2025062310330109920288320b10e11823574248.jpg");
        mailService.sendMailWithImageAttachments("subin.han@visuworks.co.kr","테스트123123","010",strings);
    }
}