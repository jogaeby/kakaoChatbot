package com.chatbot.base.domain.reservation.service.impl;

import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.ReservationSpecification;
import com.chatbot.base.domain.reservation.constant.ReservationType;
import com.chatbot.base.domain.reservation.dto.ReservatonDTO;
import com.chatbot.base.domain.reservation.repository.ReservationRepository;
import com.chatbot.base.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Transactional
    @Override
    public Reservation saveInterviewReservation(ReservatonDTO reservatonDTO) {
        Reservation reservation = reservatonDTO.toEntity(ReservationType.INTERVIEW);

        return reservationRepository.save(reservation);
    }
    @Transactional
    @Override
    public void delete(String id) {
        reservationRepository.deleteById(UUID.fromString(id));
    }

    @Override
    public List<ReservatonDTO> getAllByType(ReservationType type, Pageable pageable) {
        return reservationRepository.findAllByType(type,pageable).stream()
                .map(Reservation::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservatonDTO> search(String category, String input, ReservationType type) {
        Specification<Reservation> spec = ReservationSpecification.withDynamicQuery(category, input)
                .and((root, query, cb) -> cb.equal(root.get("type"), type));

        List<Reservation> reservations = reservationRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "reservationDate"));


        return reservations.stream()
                .map(Reservation::toDto)
                .collect(Collectors.toList());
    }
}
