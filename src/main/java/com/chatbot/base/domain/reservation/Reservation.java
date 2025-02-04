package com.chatbot.base.domain.reservation;

import com.chatbot.base.domain.BaseEntity;
import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.reservation.constant.ReservationStatus;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
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
import java.util.Locale;

@Getter
@Entity
@Table(name = "reservation")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY) // 회원과의 다대일 관계
    @JoinColumn(name = "member_id") // 외래 키 열 이름
    private Member member;
    private LocalDateTime reservationDate;
    private boolean isJoin;
    private String memo;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    private boolean isAlarmSent;

    @Builder
    public Reservation(Member member, LocalDateTime reservationDate, ReservationStatus status) {
        this.member = member;
        this.reservationDate = reservationDate;
        this.isJoin = false;
        this.memo = "";
        this.status = status;
        this.isAlarmSent = false;
    }

    public static Reservation create(Member member,String reservationDateString) {
        LocalDate reservationDate = LocalDate.parse(reservationDateString);
        LocalDateTime reservationDateTime = reservationDate.atTime(10, 0, 0);

        return Reservation.builder()
                .member(member)
                .reservationDate(reservationDateTime)
                .status(ReservationStatus.APPLY)
                .build();
    }

    public ReservationDto toDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String createDateStr = getCreateDate().format(formatter);
        return ReservationDto.builder()
                .id(String.valueOf(getId()))
                .name(member.getName())
                .phone(member.getPhone())
                .gender(member.getGender())
                .birthDate(member.getBirthDate())
                .address(member.getAddress())
                .kakaoUserkey(member.getKakaoUserKey())
                .isJoin(isJoin())
                .timeDifference(member.getBoardingPoint().getTimeDifference())
                .boardPoint(member.getBoardingPoint().getBoardPoint())
                .busName(member.getBoardingPoint().getBusName())
                .memo(memo)
                .isAlarmSent(isAlarmSent)
                .reservationDate(String.valueOf(reservationDate.toLocalDate()))
                .createDate(createDateStr)
                .status(status.getName())
                .build();
    }

    public String isJoin() {
        if (this.isJoin) {
            return "참여";
        }else {
            return "미참여";
        }
    }

    public void updateIsJoin() {
        this.isJoin = !isJoin;
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }

    public void alarmSendSuccess() {
        this.isAlarmSent = true;
    }
}
