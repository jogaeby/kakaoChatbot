package com.chatbot.base.domain.work.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@ToString
@Getter
@Builder
public class WorkDto {
    private String id;
    private LocalDate date;
    private LocalTime time;
    private LocalDateTime dateTime;
}
