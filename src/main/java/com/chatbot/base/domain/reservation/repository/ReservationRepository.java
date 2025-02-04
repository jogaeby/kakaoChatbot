package com.chatbot.base.domain.reservation.repository;

import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByMemberKakaoUserKeyAndReservationDate(String kakaoUserkey, LocalDateTime reservationDate);

    List<Reservation> findAllByMemberKakaoUserKeyOrderByReservationDateDesc(String kakaoUserkey);

    List<Reservation> findAllByReservationDate(LocalDateTime reservationDate);
    List<Reservation> findAllByReservationDateAndStatus(LocalDateTime reservationDate, ReservationStatus status);
    List<Reservation> findAllByReservationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
