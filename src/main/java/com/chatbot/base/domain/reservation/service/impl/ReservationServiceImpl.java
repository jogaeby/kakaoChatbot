package com.chatbot.base.domain.reservation.service.impl;

import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationType;
import com.chatbot.base.domain.reservation.dto.ReservatonDTO;
import com.chatbot.base.domain.reservation.repository.ReservationRepository;
import com.chatbot.base.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;

    @Transactional
    @Override
    public Reservation saveTrialReservation(ReservatonDTO reservatonDTO) {
        Reservation reservation = reservatonDTO.toEntity(ReservationType.TRIAL);

        return reservationRepository.save(reservation);
    }
}
