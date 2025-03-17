package com.chatbot.base.domain.reservation.service;

import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.dto.ReservatonDTO;

public interface ReservationService {
    Reservation saveTrialReservation(ReservatonDTO reservatonDTO);
}
