package com.chatbot.base.domain.reservation.repository;

import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID>, JpaSpecificationExecutor<Reservation> {
    Page<Reservation> findAllByType(ReservationType type, Pageable pageable);
    List<Reservation> findAllByTypeAndReservationDateBetween(ReservationType type,LocalDateTime start, LocalDateTime end);
    List<Reservation> findAllByReservationDateBetween(LocalDateTime start, LocalDateTime end);

}
