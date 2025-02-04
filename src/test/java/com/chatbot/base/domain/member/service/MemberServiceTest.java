package com.chatbot.base.domain.member.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @Test
    void getMemberByAccountId() {
    }

    @Test
    void joinMember() {
//        memberService.joinMember("admin","admin2024!");
    }
}