package com.chatbot.base.domain.reservation.service;

import com.chatbot.base.domain.reservation.RoomTourReservation;
import com.chatbot.base.domain.reservation.dto.RoomTourReservationDTO;

public interface RoomTourReservationService {
    RoomTourReservation receipt(RoomTourReservationDTO roomTourReservationDTO);

    void delete(String id);
//
//    List<RoomTourReservationDTO> getAllByType(ReservationType type, Pageable pageable);
//
//    List<RoomTourReservationDTO> search(String category, String input, ReservationType type);
}
