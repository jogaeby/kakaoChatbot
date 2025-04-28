package com.chatbot.base.domain.reservation.service;

import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.dto.ReservationDto;

public interface ReservationService {
    Reservation receipt(ReservationDto reservationDto);
//
//    void delete(String id);
//
//    List<RoomTourReservationDTO> getAll(Pageable pageable);
//
//    void addRoomNumber(RoomTourReservationDTO roomTourReservationDTO);
//
//    List<RoomTourReservationDTO> search(String category, String input);
}
