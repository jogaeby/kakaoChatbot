package com.chatbot.base.schedule;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationType;
import com.chatbot.base.domain.reservation.repository.ReservationRepository;
import com.chatbot.base.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Schedule {
    private final AlarmTalkService alarmTalkService;
    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 * * * * *")
    public void sendBeforeAlarmTalk() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("[{}] 하루전 알람톡 실행", stopWatch.getTotalTimeSeconds());

        // 현재 시간을 초 단위까지 포함하여 절단
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        // 24시간 후의 같은 분 (예: 현재가 14:15면, targetTime은 내일 14:15:00)
        LocalDateTime targetTime = now.plusDays(1).truncatedTo(ChronoUnit.MINUTES);

        // 1분 범위 내의 예약만 찾음 (14:15:00 ~ 14:15:59)
        LocalDateTime targetEndTime = targetTime.plusSeconds(59);

        log.info("{} ~ {}", targetTime, targetEndTime);

        List<Reservation> reservations = reservationRepository.findAllByTypeAndReservationDateBetween(
                ReservationType.TRIAL, targetTime, targetEndTime);
        log.info("총 알림톡 대상자 {}", reservations.size());

        reservations.forEach(reservation -> {
            String studentName = reservation.getStudentName();
            String studentPhone = reservation.getStudentPhone();
            String teacherName = reservation.getTeacherName();
            String teacherPhone = reservation.getTeacherPhone();
            LocalDateTime reservationDate = reservation.getReservationDate();

            log.info("[학생] 알림톡 발송: {} {}", studentName, studentPhone);
            alarmTalkService.sendTrialBefore(studentPhone, studentName, reservationDate);

            log.info("[선생님] 알림톡 발송: {} {}", teacherName, teacherPhone);
            alarmTalkService.sendTrialTeacherBefore(teacherPhone, teacherName, reservationDate);
        });

        stopWatch.stop();
        log.info("[{}] 하루전 알람톡 종료", stopWatch.getTotalTimeSeconds());
    }

    @Scheduled(cron = "0 * * * * *") // 매 분 0초마다 실행
    public void sendBeforeInterviewAlarmTalk() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("[{}] 1시간 전 알람톡 실행", stopWatch.getTotalTimeSeconds());

        // 현재 시간을 초 단위로 절단
        // 현재 시간 (초 단위까지 포함)
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        // 1시간 후의 정각 (예: 현재가 13:15면, nextHourStart는 14:15:00)
        LocalDateTime nextHourStart = now.plusHours(1).truncatedTo(ChronoUnit.MINUTES);

        // 1시간 후의 끝 (예: 14:15:59)
        LocalDateTime nextHourEnd = nextHourStart.plusSeconds(59);
        log.info("{} ~ {}", nextHourStart, nextHourEnd);

        // 예약 조회
        List<Reservation> reservations = reservationRepository.findAllByTypeAndReservationDateBetween(
                ReservationType.INTERVIEW, nextHourStart, nextHourEnd);
        log.info("총 알림톡 대상자: {}", reservations.size());

        // 알림톡 발송
        reservations.forEach(reservation -> {
            LocalDateTime reservationDate = reservation.getReservationDate();
            LocalDateTime alertTime = reservationDate.minusHours(1);  // 예약시간에서 1시간 전으로 설정
            String teacherName = reservation.getTeacherName();
            String teacherPhone = reservation.getTeacherPhone();
            String zoomUrl = reservation.getZoomUrl();

            // 예약 시간이 알림톡 발송 시간에 도달한 경우에만 발송
            // 분 단위만 비교 (초는 무시)
            if (alertTime.getMinute() == now.getMinute() && alertTime.getHour() == now.getHour()) {
                log.info("알림톡 발송: {} {}", teacherName, teacherPhone);
                alarmTalkService.sendInterviewBefore(teacherName, teacherPhone, zoomUrl, reservationDate);
            }
        });

        stopWatch.stop();
        log.info("[{}] 1시간 알람톡 종료", stopWatch.getTotalTimeSeconds());
    }

    @Scheduled(cron = "0 */10 * * * *") // 매 10분마다 실행
    public void sendAfterAlarmTalk() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("[{}] 40분 후 알람톡 실행", stopWatch.getTotalTimeSeconds());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTimeStart = now.minusMinutes(40).truncatedTo(ChronoUnit.MINUTES); // 40분 전 (정확한 분 단위)
        LocalDateTime targetTimeEnd = targetTimeStart.plusSeconds(59).plusNanos(999_999_999); // 59.999초까지 포함
        log.info("{} ~ {}",targetTimeStart,targetTimeEnd);
        List<Reservation> reservations = reservationRepository.findAllByReservationDateBetween(targetTimeStart,targetTimeEnd);
        log.info("총 알림톡 대상자 {}",reservations.size());

        reservations.forEach(reservation -> {
            String teacherName = reservation.getTeacherName();
            String teacherPhone = reservation.getTeacherPhone();
            String studentName = reservation.getStudentName();
            String studentPhone = reservation.getStudentPhone();

            if (reservation.getType().equals(ReservationType.TRIAL)) {
                log.info("[학생] 알림톡 발송: {} {}",studentName,studentPhone);
                alarmTalkService.sendTrialAfter(studentPhone,studentName);

                log.info("[선생님] 알림톡 발송: {} {}",teacherName,teacherPhone);
                alarmTalkService.sendTrialTeacherAfter(teacherPhone,teacherName);
            }

            if (reservation.getType().equals(ReservationType.INTERVIEW)) {
                log.info("알림톡 발송: {} {}",teacherName,teacherPhone);
                alarmTalkService.sendInterviewAfter(teacherName,teacherPhone);
            }
        });

        stopWatch.stop();
        log.info("[{}] 40분 후 알람톡 종료", stopWatch.getTotalTimeSeconds());
    }
}
