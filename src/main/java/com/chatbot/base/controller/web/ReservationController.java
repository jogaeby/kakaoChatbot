package com.chatbot.base.controller.web;

import com.chatbot.base.annotation.PassAuth;
import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationStatus;
import com.chatbot.base.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("reservation")
public class ReservationController {
    private final ReservationService reservationService;

    @PassAuth
    @GetMapping("ready")
    public String reservationReadyStatus(@RequestParam String id, @RequestParam String status, Model model) {
        try {
            Reservation reservation = reservationService.getReservation(id);

            if (reservation.getStatus().equals(ReservationStatus.COMPLETE)) {
                model.addAttribute("message", "이미 완료된 근무입니다.");
                return "reservationReadyResultNotice";
            }


            if (reservation.getStatus().equals(ReservationStatus.CANCEL)) {
                model.addAttribute("message", "이미 취소된 근무입니다.");
                return "reservationReadyResultNotice";
            }

            ReservationStatus reservationStatus = ReservationStatus.fromString(status);

            if (reservationStatus == ReservationStatus.CANCEL) {
                reservationService.cancelReservationByMember(id);
                model.addAttribute("message", "근무를 취소하였습니다.");
                return "reservationReadyResultNotice";
            }

            model.addAttribute("message", "출근 준비 하였습니다.");
            return "reservationReadyResultNotice";
        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e);
            model.addAttribute("message", "상태 변경을 실패하였습니다.");
            return "reservationReadyResultNotice";
        }
    }

    @PassAuth
    @GetMapping("boarding")
    public String reservationBoardingStatus(@RequestParam String id, @RequestParam String status, Model model) {
        try {
            Reservation reservation = reservationService.getReservation(id);

            if (reservation.getStatus().equals(ReservationStatus.COMPLETE)) {
                model.addAttribute("message", "이미 완료된 근무입니다.");
                return "reservationBoardingResultNotice";
            }


            if (reservation.getStatus().equals(ReservationStatus.CANCEL)) {
                model.addAttribute("message", "이미 취소된 근무입니다.");
                return "reservationBoardingResultNotice";
            }

            ReservationStatus reservationStatus = ReservationStatus.fromString(status);

            if (reservationStatus == ReservationStatus.CANCEL) {
//                reservationService.cancelReservationByMember(id);
                model.addAttribute("message", "탑승하지 못하였습니다.");
                return "reservationBoardingResultNotice";
            }

            model.addAttribute("message", "탑승 하였습니다.");
            return "reservationBoardingResultNotice";
        }catch (Exception e) {
            log.error("{} {}",e.getMessage(),e);
            model.addAttribute("message", "상태 변경을 실패하였습니다.");
            return "reservationBoardingResultNotice";
        }
    }

}
