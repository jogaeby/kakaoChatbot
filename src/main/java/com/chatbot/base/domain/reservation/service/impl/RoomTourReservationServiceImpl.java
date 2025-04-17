package com.chatbot.base.domain.reservation.service.impl;

import com.chatbot.base.domain.reservation.RoomTourReservation;
import com.chatbot.base.domain.reservation.dto.RoomTourReservationDTO;
import com.chatbot.base.domain.reservation.repository.RoomTourReservationRepository;
import com.chatbot.base.domain.reservation.service.RoomTourReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RoomTourReservationServiceImpl implements RoomTourReservationService {
    private final RoomTourReservationRepository roomTourReservationRepository;

    @Transactional
    @Override
    public RoomTourReservation receipt(RoomTourReservationDTO roomTourReservationDTO) {
        RoomTourReservation entity = roomTourReservationDTO.toEntity();
        return roomTourReservationRepository.save(entity);
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public List<RoomTourReservationDTO> getAll(Pageable pageable) {
        Page<RoomTourReservation> tourReservations = roomTourReservationRepository.findAll(pageable);


        return tourReservations.getContent().stream()
                .map(RoomTourReservation::toDto)
                .collect(Collectors.toList());
    }
//
//    @Override
//    public List<RoomTourReservationDTO> search(String category, String input, ReservationType type) {
//        return null;
//    }
}
