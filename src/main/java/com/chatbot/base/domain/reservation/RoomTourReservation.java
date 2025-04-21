package com.chatbot.base.domain.reservation;

import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.constant.RoomTourReservationStatus;
import com.chatbot.base.domain.reservation.dto.RoomTourReservationDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "roomTourReservation")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomTourReservation extends BaseEntity {
    private String location;
    private LocalDateTime visitDate;
    private LocalDate moveInDate;
    private String name;
    private String gender;
    private String age;
    private String phone;
    private String period;
    @Enumerated(EnumType.STRING)
    private RoomTourReservationStatus status;

    @Builder
    public RoomTourReservation(String location, LocalDateTime visitDate, LocalDate moveInDate, String name, String gender, String age, String phone, String period, RoomTourReservationStatus status) {
        this.location = location;
        this.visitDate = visitDate;
        this.moveInDate = moveInDate;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.period = period;
        this.status = status;
    }

    public RoomTourReservationDTO toDto() {
        return RoomTourReservationDTO.builder()
                .id(getUuid().toString())
                .location(location)
                .visitDate(visitDate)
                .moveInDate(moveInDate)
                .name(name)
                .gender(gender)
                .age(age)
                .phone(phone)
                .period(period)
                .status(status.getName())
                .createDate(getCreateDate())
                .build();
    }
}
