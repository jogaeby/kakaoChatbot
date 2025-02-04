package com.chatbot.base.schedule;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationStatus;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
import com.chatbot.base.domain.reservation.service.ReservationService;
import com.chatbot.base.domain.work.Work;
import com.chatbot.base.domain.work.dto.WorkDto;
import com.chatbot.base.domain.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Schedule {
    private final WorkService workService;
    private final ReservationService reservationService;
    private final AlarmTalkService alarmTalkService;
    private final int beForeHours = 1;

    @Scheduled(cron = "0 * * * * *")
    public void alarmTalk() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDate nowDate = nowDateTime.toLocalDate();

        log.info("[버스탑승알림] {}",nowDateTime);
        List<ReservationDto> reservations = reservationService.getAllReservationsByDate(nowDate);
        Optional<Work> workByDate = workService.getWorkByDate(nowDate);

        if (workByDate.isEmpty()) return;

        WorkDto workDtoByDate = workByDate.get().toDto();
        reservations.stream()
                .filter(reservation -> {

                    if (!reservation.getStatus().equals(ReservationStatus.APPLY.getName()) && !reservation.getStatus().equals(ReservationStatus.ADMISSION.getName())) {
                        return false;
                    }

                    // 이미 알람이 전송되었는지 확인
                    if (reservation.isAlarmSent()) {
                        return false;
                    }

                    // 버스 탑승 시간을 계산
                    LocalTime timeDifference = reservation.getTimeDifference();
                    LocalDateTime busTime = workDtoByDate.getDateTime()
                            .minusHours(timeDifference.getHour())
                            .minusMinutes(timeDifference.getMinute());

                    LocalDateTime min = busTime.minusHours(beForeHours);
                    LocalDateTime max = busTime.minusHours(beForeHours).plusMinutes(1);
                    log.info("{} ~ {}",min,max);
                    // 현재 시간과 버스 탑승 시간의 1시간 전 사이에 있을 경우 알림 전송
                    return nowDateTime.isAfter(min) && nowDateTime.isBefore(max);
                })
                .forEach(reservation -> {
                    alarmTalkService.sendBoardBusAlarm(reservation,workDtoByDate);

                    reservationService.alarmSendSuccess(reservation.getId());
                });
    }

}
