package com.chatbot.base.domain.reservation.service;

import com.chatbot.base.domain.reservation.RoomTourReservation;
import com.chatbot.base.domain.reservation.dto.RoomTourReservationDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomTourReservationService {
    RoomTourReservation receipt(RoomTourReservationDTO roomTourReservationDTO);

    void delete(String id);

    List<RoomTourReservationDTO> getAll(Pageable pageable);
//
//    List<RoomTourReservationDTO> search(String category, String input, ReservationType type);
}
