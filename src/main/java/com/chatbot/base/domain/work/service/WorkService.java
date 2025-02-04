package com.chatbot.base.domain.work.service;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
import com.chatbot.base.domain.reservation.service.ReservationService;
import com.chatbot.base.domain.work.Work;
import com.chatbot.base.domain.work.dto.WorkDto;
import com.chatbot.base.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkService {
    private final WorkRepository workRepository;
    private final AlarmTalkService alarmTalkService;
    private final ReservationService reservationService;

    @Transactional
    public void saveWork(WorkDto workDto) {
        LocalDate date = workDto.getDate();
        LocalTime time = workDto.getTime();
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        Work work = Work.create(dateTime);
        workRepository.save(work);


        List<ReservationDto> reservationsByDate = reservationService.getAllReservationsByDate(workDto.getDate());

        alarmTalkService.sendAllFirstAlarm(reservationsByDate,work.toDto());
    }

    @Transactional
    public void updateWork(WorkDto workDto) {
        LocalDateTime localDateTime = LocalDateTime.of(workDto.getDate(), workDto.getTime());

        Work work = workRepository.findById(UUID.fromString(workDto.getId())).
                orElseThrow(() -> new IllegalArgumentException(workDto.getId() + "존재하지 않습니다."));

        work.updateWorkDateTime(localDateTime);

        WorkDto currentWorkDto = work.toDto();
        List<ReservationDto> reservationsByDate = reservationService.getAllReservationsByDate(currentWorkDto.getDate());

        alarmTalkService.sendAllFirstAlarm(reservationsByDate,currentWorkDto);
    }


    public List<WorkDto> getWorkByRange(LocalDateTime start, LocalDateTime end) {
        return workRepository.findAllByWorkDateTimeBetween(start, end)
               .stream()
               .map(Work::toDto)
               .collect(Collectors.toList());
    }

    public WorkDto getWorkDtoByDate(LocalDate date) {
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        return workRepository.findAllByWorkDateTimeBetween(dateTime, dateTime.plusDays(1))
                .stream()
                .map(Work::toDto)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(date + " No such work"));
    }

    public Optional<Work> getWorkByDate(LocalDate date) {
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        return workRepository.findAllByWorkDateTimeBetween(dateTime, dateTime.plusDays(1))
                .stream()
                .findFirst();

    }

    public WorkDto getWorkDtoById(String id) {
        return workRepository.findById(UUID.fromString(id))
                .stream()
                .map(Work::toDto)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(id + " No such work"));
    }
}
