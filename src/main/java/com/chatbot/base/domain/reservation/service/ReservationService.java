package com.chatbot.base.domain.reservation.service;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.domain.member.Member;
import com.chatbot.base.domain.member.service.MemberService;
import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationStatus;
import com.chatbot.base.domain.reservation.dto.CalendarDto;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
import com.chatbot.base.domain.reservation.repository.ReservationRepository;
import com.chatbot.base.domain.work.dto.WorkDto;
import com.chatbot.base.domain.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberService memberService;
    private final AlarmTalkService alarmTalkService;

    @Transactional
    public Reservation saveReservation(String kakaoUserkey, String name, String phone, String reservationDateString) {
        if (isDuplicateReservation(kakaoUserkey,reservationDateString)) {
            throw new DuplicateKeyException("[중복신청]\n이미 "+reservationDateString+" 날짜에 근무를 신청 하였습니다.");
        }
        Member memberByKakaoUserKey = memberService.getMemberByKakaoUserKey(kakaoUserkey);

        Reservation reservation = Reservation.create(memberByKakaoUserKey,reservationDateString);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservationByKakaoUserkey(String kakaoUserkey) {
        // 우선순위를 명시한 정렬 로직
        Map<ReservationStatus, Integer> statusPriority = Map.of(
                ReservationStatus.APPLY, 1,
                ReservationStatus.CANCEL, 2,
                ReservationStatus.COMPLETE, 3
        );

        return reservationRepository.findAllByMemberKakaoUserKeyOrderByReservationDateDesc(kakaoUserkey).stream()
                .filter(reservation ->
                        reservation.getStatus().equals(ReservationStatus.APPLY) ||
                                reservation.getStatus().equals(ReservationStatus.CANCEL) ||
                                reservation.getStatus().equals(ReservationStatus.COMPLETE)
                )
                .sorted(
                        Comparator
                                .comparing(Reservation::getReservationDate, Comparator.reverseOrder()) // 1순위: 날짜 최신순 정렬
                                .thenComparing(reservation -> statusPriority.get(reservation.getStatus())) // 2순위: 상태 우선순위 정렬
                )
                .collect(Collectors.toList());
    }

    public boolean isDuplicateReservation(String kakaoUserkey, String reservationDateString) {
        LocalDate reservationDate = LocalDate.parse(reservationDateString);
        LocalDateTime reservationDateTime = reservationDate.atTime(10, 0, 0);

        List<Reservation> filterReservation = reservationRepository.findByMemberKakaoUserKeyAndReservationDate(kakaoUserkey, reservationDateTime).stream()
                .filter(reservation ->
                        reservation.getStatus().equals(ReservationStatus.ADMISSION) || reservation.getStatus().equals(ReservationStatus.APPLY))
                .collect(Collectors.toList());

        if (!filterReservation.isEmpty()) {
            return true;
        }

        return false;
    }

    public List<ReservationDto> getReservationByReservationDate(String reservationDateString, ReservationStatus status) {
        LocalDate reservationDate = LocalDate.parse(reservationDateString);
        LocalDateTime reservationDateTime = reservationDate.atTime(10, 0, 0);

        if (status == null) {
            return reservationRepository.findAllByReservationDate(reservationDateTime).stream()
                    .map(Reservation::toDto)
                    .toList();
        }

        return reservationRepository.findAllByReservationDateAndStatus(reservationDateTime,status).stream()
                .map(Reservation::toDto)
                .toList();
    }

    public List<CalendarDto> getCalendarDtoListByReservationStartDateAndReservationEndDate(LocalDateTime reservationStartDateTime, LocalDateTime reservationEndDateTime ,List<WorkDto> workByRange) {
        List<Reservation> reservations = reservationRepository.findAllByReservationDateBetween(reservationStartDateTime, reservationEndDateTime);

        // 상태별로 예약을 그룹화하고 날짜별로도 그룹화하여 카운트를 계산
        Map<ReservationStatus, Map<LocalDate, Long>> statusDailyCounts = reservations.stream()
                // "신청"과 "승인" 상태만 필터링
                .filter(reservation ->
                        reservation.getStatus() == ReservationStatus.APPLY || reservation.getStatus() == ReservationStatus.ADMISSION)
                .collect(Collectors.groupingBy(Reservation::getStatus, // 상태별로 그룹화
                        Collectors.groupingBy(reservation -> reservation.getReservationDate().toLocalDate(), // 날짜별로 그룹화
                                Collectors.counting()))); // 각 그룹의 카운트
        List<CalendarDto> calendarEvents = new ArrayList<>();

        // 상태별로 CalendarDto 리스트 생성
        statusDailyCounts.forEach((status, dailyCounts) -> {
            dailyCounts.forEach((date, count) -> {
                String title = status.getName() + ": " + count + "명"; // 상태 + 카운트
                String backgroundColor = getColorByStatus(status); // 상태에 따른 컬러 선택

                calendarEvents.add(
                        CalendarDto.builder()
                                .title(title)
                                .start(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                .backgroundColor(backgroundColor) // 컬러 설정
                                .status(status.getName())
                                .build()
                );
            });
        });



        for (WorkDto workDto : workByRange) {
            calendarEvents.add(
                    CalendarDto.builder()
                            .title("근무시작: "+workDto.getTime())
                            .start(workDto.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .backgroundColor("#007bff") // 컬러 설정
                            .status("근무시작시간")
                            .time(workDto.getTime())
                            .id(workDto.getId())
                            .build()
            );
        }


        return calendarEvents;
    }
    // 상태에 따른 컬러 지정 메서드
    private String getColorByStatus(ReservationStatus status) {
        switch (status.getName()) {
            case "승인":
                return "#28a745"; // 초록색 (확정)
            case "신청":
                return "#ffc107"; // 노란색 (대기 중)
            default:
                return "#007bff"; // 기본 파란색 (기타 상태)
        }
    }

    @Transactional
    public void updateStatus(String reservationId, ReservationStatus status) {
        Reservation reservation = getReservation(reservationId);

        reservation.updateStatus(status);
    }

    @Transactional
    public void cancelReservationByMember(String reservationId) {
        Reservation reservation = getReservation(reservationId);

        updateStatus(reservationId,ReservationStatus.CANCEL);

        alarmTalkService.sendCancelAlarmByMember(reservation.toDto());
    }

    @Transactional
    public void cancelReservationByAdmin(String reservationId) {
        Reservation reservation = getReservation(reservationId);

        updateStatus(reservationId,ReservationStatus.CANCEL);

        alarmTalkService.sendCancelAlarmByAdmin(reservation.toDto());
    }

    @Transactional
    public void deleteReservation(String reservationId) {
        reservationRepository.deleteById(UUID.fromString(reservationId));
    }

    @Transactional
    public void updateIsJoin(String reservationId) {
        Reservation reservation = getReservation(reservationId);

        reservation.updateIsJoin();
    }

    @Transactional
    public void alarmSendSuccess(String reservationId) {
        Reservation reservation = getReservation(reservationId);

        reservation.alarmSendSuccess();
    }

    public Reservation getReservation(String reservationId) {
        return reservationRepository.findById(UUID.fromString(reservationId))
                .orElseThrow(() -> new NoSuchElementException(reservationId + "근무가 존재하지 않습니다"));
    }

    public List<ReservationDto> getAllReservationsByDate(LocalDate date) {
        LocalDateTime reservationStartDateTime = date.atTime(0, 0, 0);
        LocalDateTime reservationEndDateTime = date.atTime(23, 59, 59);

        return reservationRepository.findAllByReservationDateBetween(reservationStartDateTime, reservationEndDateTime).stream()
                .map(Reservation::toDto)
                .collect(Collectors.toList())
                ;
    }
}
