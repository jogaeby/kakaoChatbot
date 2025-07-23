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

        alarmTalkService.sendSuggestionReceipt("010-7713-1548","테스트","2025-07-21","www.naver.com");
    }

    @Test
    void name() {
        alarmTalkService.sendInquiriesReceipt("01077131548");
    }
}