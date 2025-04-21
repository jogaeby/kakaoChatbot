package com.chatbot.base.domain.reservation.service.impl;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.domain.reservation.RoomTourReservation;
import com.chatbot.base.domain.reservation.RoomTourReservationSpecification;
import com.chatbot.base.domain.reservation.dto.RoomTourReservationDTO;
import com.chatbot.base.domain.reservation.repository.RoomTourReservationRepository;
import com.chatbot.base.domain.reservation.service.RoomTourReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RoomTourReservationServiceImpl implements RoomTourReservationService {
    private final RoomTourReservationRepository roomTourReservationRepository;
    private final AlarmTalkService alarmTalkService;

    @Transactional
    @Override
    public RoomTourReservation receipt(RoomTourReservationDTO roomTourReservationDTO) {
        RoomTourReservation entity = roomTourReservationDTO.toEntity();
        return roomTourReservationRepository.save(entity);
    }

    @Transactional
    @Override
    public void delete(String id) {
        roomTourReservationRepository.deleteById(UUID.fromString(id));
    }

    @Transactional
    @Override
    public void addRoomNumber(RoomTourReservationDTO roomTourReservationDTO) {
        String id = roomTourReservationDTO.getId();
        RoomTourReservation roomTourReservation = roomTourReservationRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("찾을 수 없습니다."));

        roomTourReservation.addRoomNumber(roomTourReservationDTO.getAddress(), roomTourReservationDTO.getRoomNumber());

        RoomTourReservationDTO dto = roomTourReservation.toDto();
        alarmTalkService.sendRoomTourAssignment(dto);
    }

    @Override
    public List<RoomTourReservationDTO> getAll(Pageable pageable) {
        Page<RoomTourReservation> tourReservations = roomTourReservationRepository.findAll(pageable);


        return tourReservations.getContent().stream()
                .map(RoomTourReservation::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomTourReservationDTO> search(String category, String input) {
        System.out.println("category = " + category);
        Specification<RoomTourReservation> spec = RoomTourReservationSpecification.withDynamicQuery(category, input);


        List<RoomTourReservation> reservations = roomTourReservationRepository.findAll(spec);

        return reservations.stream()
                .map(RoomTourReservation::toDto)
                .collect(Collectors.toList());
    }
}
