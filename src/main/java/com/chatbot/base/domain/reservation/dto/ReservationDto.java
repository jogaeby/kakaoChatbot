package com.chatbot.base.domain.reservation.dto;

import com.chatbot.base.domain.reservation.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationDto {
    private Long id;
    private String name;
    private String phone;
    private String depart;
    private String arrive;
    private String hopePrice;
    private LocalDateTime hopeCompleteDateTime;
    private LocalDateTime reservationDateTime;
    private String message;
    private String comment;

    public Reservation toEntity() {
        return Reservation.builder()
                .id(id)
                .name(name)
                .phone(phone)
                .depart(depart)
                .arrive(arrive)
                .hopePrice(hopePrice)
                .hopeCompleteDateTime(hopeCompleteDateTime)
                .reservationDateTime(reservationDateTime)
                .message(message)
                .comment(comment)
                .build();
    }
}
