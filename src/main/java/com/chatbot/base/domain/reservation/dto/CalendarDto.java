package com.chatbot.base.domain.reservation.dto;

import com.chatbot.base.domain.reservation.constant.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class CalendarDto {
    private String title;
    private String start;
    private String end;
    private String backgroundColor;
    private String borderColor;
    private String textColor;
    private String status;
    private LocalTime time;
    private String id;
}
