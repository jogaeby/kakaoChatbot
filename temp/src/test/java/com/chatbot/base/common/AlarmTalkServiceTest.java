package com.chatbot.base.common;

import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AlarmTalkServiceTest {
    @Autowired
    private AlarmTalkService alarmTalkService;
    @Test
    void sendRoomTourAssignment() {
        ReservationDto build = ReservationDto.builder()
                .id(System.currentTimeMillis())
                .name("테스트")
                .phone("01077131548")
                .depart("출발지")
                .arrive("도착지")
                .hopePrice("10만")
                .hopeCompleteDateTime(LocalDateTime.now())
                .reservationDateTime(LocalDateTime.now())
                .message("요청사항")
                .comment("추가")
                .build();
        Reservation entity = build.toEntity();

        alarmTalkService.sendReservation("01039190126",entity);
    }
}