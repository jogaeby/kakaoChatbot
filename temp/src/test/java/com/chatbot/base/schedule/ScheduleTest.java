package com.chatbot.base.schedule;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@SpringBootTest
class ScheduleTest {
    @Autowired
    private Schedule schedule;


    @Test
    void sendBeforeAlarmTalk() {
        schedule.sendBeforeAlarmTalk();
    }

    @Test
    void testUpdateProductStatus() {


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayBeforeStart = now.plusDays(1).truncatedTo(ChronoUnit.DAYS); // 25일 00시
        LocalDateTime dayBeforeEnd = dayBeforeStart.plusDays(1).minusNanos(1); // 25일 23:59:59.999

        System.out.println("dayBeforeStart = " + dayBeforeStart);
        System.out.println("dayBeforeEnd = " + dayBeforeEnd);

    }
}