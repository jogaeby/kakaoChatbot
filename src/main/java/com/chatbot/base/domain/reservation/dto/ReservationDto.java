package com.chatbot.base.domain.reservation.dto;

import com.chatbot.base.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class ReservationDto {
    private String id;
    private String name;
    private String phone;
    private String gender;
    private String birthDate;
    private String address;
    private String kakaoUserkey;
    private String isJoin;
    private String memo;
    private String busName;
    private String boardPoint;
    private LocalTime timeDifference;
    private boolean isAlarmSent;
    private String reservationDate;
    private String createDate;
    private String status;
}
