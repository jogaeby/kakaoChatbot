package com.chatbot.base.domain.reservation.repository;

import com.chatbot.base.domain.product.Product;
import com.chatbot.base.domain.product.constant.ProductStatus;
import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.constant.ReservationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID>, JpaSpecificationExecutor<Reservation> {
    Page<Reservation> findAllByType(ReservationType type, Pageable pageable);

}
