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

    @Scheduled(cron = "0 0 19 * * *") // 매일 저녁 7시마다 실행
    public void sendBeforeAlarmTalk() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("[{}] 하루전 알람톡 실행",stopWatch.getTotalTimeSeconds());


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayBeforeStart = now.plusDays(1).truncatedTo(ChronoUnit.DAYS); // 25일 00시
        LocalDateTime dayBeforeEnd = dayBeforeStart.plusDays(1).minusNanos(1); // 25일 23:59:59.999
        log.info("{} ~ {}",dayBeforeStart,dayBeforeEnd);

        List<Reservation> reservations = reservationRepository.findAllByTypeAndReservationDateBetween(ReservationType.TRIAL,dayBeforeStart, dayBeforeEnd);
        log.info("총 알림톡 대상자 {}",reservations.size());

        reservations.forEach(reservation -> {
                String studentName = reservation.getStudentName();
                String studentPhone = reservation.getStudentPhone();
                String teacherName = reservation.getTeacherName();
                String teacherPhone = reservation.getTeacherPhone();
                LocalDateTime reservationDate = reservation.getReservationDate();
                log.info("[학생] 알림톡 발송: {} {}",studentName,studentPhone);
                alarmTalkService.sendTrialBefore(studentPhone,studentName,reservationDate);

                log.info("[선생님] 알림톡 발송: {} {}",teacherName,teacherPhone);
                alarmTalkService.sendTrialTeacherBefore(teacherPhone,teacherName,reservationDate);
        });


        stopWatch.stop();
        log.info("[{}] 하루전 알람톡 종료",stopWatch.getTotalTimeSeconds());
    }

    @Scheduled(cron = "0 0 * * * *") // 매 시간 0분 0초마다 실행
    public void sendBeforeInterviewAlarmTalk() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("[{}] 1시간 전 알람톡 실행",stopWatch.getTotalTimeSeconds());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextHourStart = now.plusHours(1).truncatedTo(ChronoUnit.HOURS);
        LocalDateTime nextHourEnd = nextHourStart.plusMinutes(59).plusSeconds(59);
        log.info("{} ~ {}",nextHourStart,nextHourEnd);
        List<Reservation> reservations = reservationRepository.findAllByTypeAndReservationDateBetween(ReservationType.INTERVIEW,nextHourStart, nextHourEnd);
        log.info("총 알림톡 대상자 {}",reservations.size());
        // 실행할 로직
        reservations.forEach(reservation -> {
                String teacherName = reservation.getTeacherName();
                String teacherPhone = reservation.getTeacherPhone();
                String zoomUrl = reservation.getZoomUrl();
                LocalDateTime reservationDate = reservation.getReservationDate();
                log.info("알림톡 발송: {} {}",teacherName,teacherPhone);
                alarmTalkService.sendInterviewBefore(teacherName,teacherPhone,zoomUrl,reservationDate);
        });

        stopWatch.stop();
        log.info("[{}] 1시간 알람톡 종료",stopWatch.getTotalTimeSeconds());
    }

    @Scheduled(cron = "0 */10 * * * *") // 매 10분마다 실행
    public void sendAfterAlarmTalk() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("[{}] 50분 후 알람톡 실행", stopWatch.getTotalTimeSeconds());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTimeStart = now.minusMinutes(50).truncatedTo(ChronoUnit.MINUTES); // 50분 전 (정확한 분 단위)
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
        log.info("[{}] 50분 후 알람톡 종료", stopWatch.getTotalTimeSeconds());
    }
}
