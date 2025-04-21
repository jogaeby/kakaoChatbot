package com.chatbot.base.domain.reservation.dto;

import com.chatbot.base.domain.constant.RoomTourReservationStatus;
import com.chatbot.base.domain.reservation.RoomTourReservation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RoomTourReservationDTO {
    private String id;
    private String location;
    private LocalDateTime visitDate;
    private LocalDate moveInDate;
    private String name;
    private String gender;
    private String age;
    private String phone;
    private String period;
    private LocalDateTime createDate;
    private String status;

    public RoomTourReservation toEntity() {
        return RoomTourReservation.builder()
                .location(location)
                .visitDate(visitDate)
                .moveInDate(moveInDate)
                .name(name)
                .gender(gender)
                .age(age)
                .phone(phone)
                .period(period)
                .status(RoomTourReservationStatus.fromString(status))
                .build();
    }
}
