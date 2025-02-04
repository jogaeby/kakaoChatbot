package com.chatbot.base.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AlarmTalkServiceTest {
    @Autowired
    private AlarmTalkService alarmTalkService;
    @Test
    void send() {
//        alarmTalkService.send("알림톡테스트","01099229545", LocalDate.now().toString(),"09시","서울팀","1234번","서울역 1번출구","08시");
    }
}