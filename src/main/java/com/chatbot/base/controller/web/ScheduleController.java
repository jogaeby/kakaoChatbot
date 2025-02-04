package com.chatbot.base.controller.web;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationStatus;
import com.chatbot.base.domain.reservation.dto.CalendarDto;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
import com.chatbot.base.domain.reservation.service.ReservationService;
import com.chatbot.base.domain.work.dto.WorkDto;
import com.chatbot.base.domain.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("schedule")
public class ScheduleController {
    private final ReservationService reservationService;
    private final AlarmTalkService alarmService;
    private final WorkService workService;
    @GetMapping("")
    public String getPage() {
        return "schedule";
    }

    @PatchMapping("status/{reservationId}")
    @ResponseBody
    public ResponseEntity updateStatus(@PathVariable String reservationId, @RequestBody Map<String,String> data) {
        try {
            String status = data.get("status");
            ReservationStatus reservationStatus = ReservationStatus.fromString(status);

            if (ReservationStatus.CANCEL.getName().equals(reservationStatus.getName())) {
                reservationService.cancelReservationByAdmin(reservationId);
            }else {
                reservationService.updateStatus(reservationId,reservationStatus);
            }

            return ResponseEntity
                    .ok()
                    .build();
        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @PatchMapping("isJoin/{reservationId}")
    @ResponseBody
    public ResponseEntity updateIsJoin(@PathVariable String reservationId) {
        try {

            reservationService.updateIsJoin(reservationId);

            return ResponseEntity
                    .ok()
                    .build();

        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @GetMapping("calendar")
    @ResponseBody
    public ResponseEntity getScheduleList(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDate reservationStartDate = LocalDate.parse(startDate);
            LocalDateTime reservationStartDateTime = reservationStartDate.atTime(0, 0, 0);

            LocalDate reservationEndDate = LocalDate.parse(endDate);
            LocalDateTime reservationEndDateTime = reservationEndDate.atTime(23, 59, 59);

            List<WorkDto> workByRange = workService.getWorkByRange(reservationStartDateTime, reservationEndDateTime);

            List<CalendarDto> calendarDtoListByReservationStartDateAndReservationEndDate = reservationService
                    .getCalendarDtoListByReservationStartDateAndReservationEndDate(reservationStartDateTime, reservationEndDateTime,workByRange);

            return ResponseEntity
                    .ok()
                    .body(calendarDtoListByReservationStartDateAndReservationEndDate);
        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @GetMapping("list")
    @ResponseBody
    public ResponseEntity getScheduleListByDateAndStatus(@RequestParam String date, @RequestParam String status) {
        try {
            ReservationStatus reservationStatus;
            if (status.equals(null) || status.equals("") || status.equals("null")) {
                reservationStatus = null;
            }else {
                reservationStatus = ReservationStatus.fromString(status);
            }

            List<ReservationDto> reservationByReservationDate = reservationService.getReservationByReservationDate(date,reservationStatus);

            return ResponseEntity
                    .ok()
                    .body(reservationByReservationDate);
        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @PostMapping("works")
    @ResponseBody
    public ResponseEntity saveWorkTime(@RequestBody WorkDto workDto) {
        try {

            workService.saveWork(workDto);

            return ResponseEntity
                    .ok()
                    .build();
        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @PatchMapping("works")
    @ResponseBody
    public ResponseEntity updateWorkTime(@RequestBody WorkDto workDto) {
        try {
            log.info("{}",workDto);

            WorkDto current = workService.getWorkDtoById(workDto.getId());

            if (current.getTime().equals(workDto.getTime()) && current.getDate().equals(workDto.getDate())) {
                return ResponseEntity
                        .ok()
                        .build();
            }

            workService.updateWork(workDto);


            return ResponseEntity
                    .ok()
                    .build();
        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @GetMapping("works")
    @ResponseBody
    public ResponseEntity workTime(@RequestParam LocalDate date) {
        try {

            WorkDto workByDate = workService.getWorkDtoByDate(date);

            return ResponseEntity
                    .ok(workByDate);
        }catch (NoSuchElementException e) {
            return ResponseEntity
                    .ok()
                    .build();
        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e.getStackTrace());
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}
