package com.chatbot.base.domain.reservation;

import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.reservation.constant.ReservationType;
import com.chatbot.base.domain.reservation.dto.ReservatonDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "reservation")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {
    private String studentName;
    private String studentPhone;
    private String teacherName;
    private String teacherPhone;
    private String zoomUrl;
    private LocalDateTime reservationDate;
    private String studentInfo;
    @Enumerated(EnumType.STRING)
    private ReservationType type;

    @Builder
    public Reservation(String studentName, String studentPhone, String teacherName, String teacherPhone, String zoomUrl, LocalDateTime reservationDate, String studentInfo, ReservationType type) {
        this.studentName = studentName;
        this.studentPhone = studentPhone;
        this.teacherName = teacherName;
        this.teacherPhone = teacherPhone;
        this.zoomUrl = zoomUrl;
        this.reservationDate = reservationDate;
        this.studentInfo = studentInfo;
        this.type = type;
    }

    public ReservatonDTO toDto() {
        return ReservatonDTO.builder()
                .id(getUuid().toString())
                .zoomUrl(zoomUrl)
                .studentName(studentName)
                .studentPhone(studentPhone)
                .teacherName(teacherName)
                .teacherPhone(teacherPhone)
                .reservationDate(reservationDate)
                .studentInfo(studentInfo)
                .createDate(getCreateDate().toString())
                .build();
    }
}
