package com.chatbot.base.domain.member.service;

import com.chatbot.base.domain.member.dto.MemberDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Test
    void join() {
        MemberDTO admin = MemberDTO.builder()
                .id("admin")
                .password("admin2025!")
                .phone("01000000000")
                .isAlarmTalk(true)

                .build();
        memberService.join(admin);
    }
}