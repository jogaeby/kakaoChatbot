package com.chatbot.base.domain.reservation.service;

import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationType;
import com.chatbot.base.domain.reservation.dto.ReservatonDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReservationService {
    Reservation saveTrialReservation(ReservatonDTO reservatonDTO);
    List<ReservatonDTO> getAllByType(ReservationType type, Pageable pageable);
}
