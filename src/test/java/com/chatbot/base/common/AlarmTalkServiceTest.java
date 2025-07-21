package com.chatbot.base.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AlarmTalkServiceTest {
    @Autowired
    private AlarmTalkService alarmTalkService;
    @Test
    void sendRoomTourAssignment() {

        alarmTalkService.sendSuggestionReceipt("01077131548","테스트","2025-07-21","www.naver.com");
    }
}