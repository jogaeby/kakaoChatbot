package com.chatbot.base.domain.reservation.dto;

import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@Builder
public class ReservatonDTO {
    private String id;
    private String studentName;
    private String studentPhone;
    private String teacherName;
    private String teacherPhone;
    private LocalDateTime reservationDate;
    private String studentInfo;
    private String createDate;

    public Reservation toEntity(ReservationType type) {
        return Reservation.builder()
                .studentName(studentName)
                .studentPhone(studentPhone)
                .teacherName(teacherName)
                .type(type)
                .teacherPhone(teacherPhone)
                .reservationDate(reservationDate)
                .studentInfo(studentInfo)
                .build();
    }
}
