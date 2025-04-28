package com.chatbot.base.domain.reservation;

import com.chatbot.base.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "reservation")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {
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

    @Builder
    public Reservation(Long id, String name, String phone, String depart, String arrive, String hopePrice, LocalDateTime hopeCompleteDateTime, LocalDateTime reservationDateTime, String message, String comment) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.depart = depart;
        this.arrive = arrive;
        this.hopePrice = hopePrice;
        this.hopeCompleteDateTime = hopeCompleteDateTime;
        this.reservationDateTime = reservationDateTime;
        this.message = message;
        this.comment = comment;
    }
}
