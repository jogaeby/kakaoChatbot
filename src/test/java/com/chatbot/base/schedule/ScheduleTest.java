package com.chatbot.base.schedule;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScheduleTest {
    @Autowired
    private Schedule schedule;

    @Test
    void updateProductStatus() {
        schedule.updateProductStatus();
    }
}