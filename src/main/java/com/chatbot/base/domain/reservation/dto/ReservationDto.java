package com.chatbot.base.domain.reservation.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationDto {
    private String name;
    private String phone;
    private String depart;
    private String arrive;
    private String hopePrice;
    private LocalDateTime hopeCompleteDateTime;
    private LocalDateTime reservationDateTime;
    private String message;
    private String comment;
}
