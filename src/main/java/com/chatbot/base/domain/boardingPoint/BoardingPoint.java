package com.chatbot.base.domain.boardingPoint;

import com.chatbot.base.constant.MemberRole;
import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.boardingPoint.dto.BoardingPointDto;
import com.chatbot.base.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "boardingPoint")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardingPoint extends BaseEntity {
    private String busName;
    private String busNumber;
    private String driverName;
    private String driverPhone;
    private String boardPoint;
    private LocalTime startWorkTime;
    private LocalTime departBusTime;
    private LocalTime timeDifference;

    @Builder
    public BoardingPoint(String busName, String busNumber, String driverName, String driverPhone, String boardPoint, LocalTime startWorkTime, LocalTime departBusTime, LocalTime timeDifference) {
        this.busName = busName;
        this.busNumber = busNumber;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.boardPoint = boardPoint;
        this.startWorkTime = startWorkTime;
        this.departBusTime = departBusTime;
        this.timeDifference = timeDifference;
    }

    public static BoardingPoint create(String busName, String busNumber, String driverName, String driverPhone, String boardPoint, LocalTime startWorkTime, LocalTime departBusTime) {
        Duration duration = Duration.between(departBusTime,startWorkTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60; // 남은 분을 계산
        LocalTime timeDifference = LocalTime.of((int) hours, (int) minutes);

        return BoardingPoint.builder()
                .busName(busName)
                .busNumber(busNumber)
                .driverName(driverName)
                .driverPhone(driverPhone)
                .boardPoint(boardPoint)
                .startWorkTime(startWorkTime)
                .departBusTime(departBusTime)
                .timeDifference(timeDifference)
                .build();
    }

    public BoardingPointDto toDto() {
        return BoardingPointDto.builder()
               .id(String.valueOf(getId()))
               .busName(busName)
               .busNumber(busNumber)
               .driverName(driverName)
               .driverPhone(driverPhone)
               .boardPoint(boardPoint)
               .startWorkTime(startWorkTime)
               .departBusTime(departBusTime)
               .timeDifference(timeDifference)
               .build();
    }

    public void update(BoardingPointDto boardingPointDto) {
        Duration duration = Duration.between(boardingPointDto.getDepartBusTime(),boardingPointDto.getStartWorkTime());
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60; // 남은 분을 계산
        LocalTime timeDifference = LocalTime.of((int) hours, (int) minutes);

        this.busName = boardingPointDto.getBusName();
        this.busNumber = boardingPointDto.getBusNumber();
        this.driverName = boardingPointDto.getDriverName();
        this.driverPhone = boardingPointDto.getDriverPhone();
        this.boardPoint = boardingPointDto.getBoardPoint();
        this.startWorkTime = boardingPointDto.getStartWorkTime();
        this.departBusTime = boardingPointDto.getDepartBusTime();
        this.timeDifference = timeDifference;
    }
}