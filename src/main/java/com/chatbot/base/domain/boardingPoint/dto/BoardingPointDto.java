package com.chatbot.base.domain.boardingPoint.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class BoardingPointDto {
    private String id;
    private String busName;
    private String busNumber;
    private String driverName;
    private String driverPhone;
    private String boardPoint;
    private LocalTime startWorkTime;
    private LocalTime departBusTime;
    private LocalTime timeDifference;
}
