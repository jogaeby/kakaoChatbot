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

        List<Reservation> reservations = reservationRepository.findAllByReservationDateBetween(dayBeforeStart, dayBeforeEnd);
        log.info("총 알림톡 대상자 {}",reservations.size());

        reservations.forEach(reservation -> {
            if (reservation.getType().equals(ReservationType.INTERVIEW)) {
                String teacherName = reservation.getTeacherName();
                String teacherPhone = reservation.getTeacherPhone();
                String zoomUrl = reservation.getZoomUrl();
                LocalDateTime reservationDate = reservation.getReservationDate();

                alarmTalkService.sendInterviewBefore(teacherName,teacherPhone,zoomUrl,reservationDate);
            }


            if (reservation.getType().equals(ReservationType.TRIAL)) {
                String studentName = reservation.getStudentName();
                String studentPhone = reservation.getStudentPhone();
                LocalDateTime reservationDate = reservation.getReservationDate();
                alarmTalkService.sendTrialBefore(studentPhone,studentName,reservationDate);
            }
        });


        stopWatch.stop();
        log.info("[{}] 하루전 알람톡 종료",stopWatch.getTotalTimeSeconds());
    }
    @Scheduled(cron = "0 * * * * *") // 매 분마다 실행
    public void sendAfterAlarmTalk() {

    }
}
