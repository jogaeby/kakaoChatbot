package com.chatbot.base.domain.work.repository;

import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.work.Work;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WorkRepository extends JpaRepository<Work, UUID> {
    List<Work> findAllByWorkDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
