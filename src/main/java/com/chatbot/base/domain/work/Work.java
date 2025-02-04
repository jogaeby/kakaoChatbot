package com.chatbot.base.domain.work;

import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.reservation.constant.ReservationStatus;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
import com.chatbot.base.domain.work.dto.WorkDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Entity
@Table(name = "work")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Work extends BaseEntity {
    private LocalDateTime workDateTime;

    @Builder
    public Work(LocalDateTime workDateTime) {
        this.workDateTime = workDateTime;
    }

    public static Work create(LocalDateTime workDateTime) {
        return Work.builder()
               .workDateTime(workDateTime)
               .build();
    }

    public WorkDto toDto() {
        return WorkDto.builder()
                .id(String.valueOf(getId()))
                .date(workDateTime.toLocalDate())
                .time(workDateTime.toLocalTime())
                .dateTime(workDateTime)
                .build();
    }

    public void updateWorkDateTime(LocalDateTime workDateTime) {
        this.workDateTime = workDateTime;
    }
}
