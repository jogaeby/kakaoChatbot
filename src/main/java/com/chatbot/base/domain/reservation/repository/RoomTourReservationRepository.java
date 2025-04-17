package com.chatbot.base.domain.reservation.repository;

import com.chatbot.base.domain.reservation.RoomTourReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomTourReservationRepository extends JpaRepository<RoomTourReservation, UUID> {
    Page<RoomTourReservation> findAll(Pageable pageable);
//    List<RoomTourReservation> findAllByTypeAndReservationDateBetween(ReservationType type,LocalDateTime start, LocalDateTime end);
//    List<RoomTourReservation> findAllByReservationDateBetween(LocalDateTime start, LocalDateTime end);

}
