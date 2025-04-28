package com.chatbot.base.domain.reservation.service.impl;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
import com.chatbot.base.domain.reservation.repository.ReservationRepository;
import com.chatbot.base.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository roomTourReservationRepository;
    private final AlarmTalkService alarmTalkService;

    @Transactional
    @Override
    public Reservation receipt(ReservationDto reservationDto) {
        Reservation entity = Reservation.builder()
                .id(System.currentTimeMillis())
                .name(reservationDto.getName())
                .phone(reservationDto.getPhone())
                .depart(reservationDto.getDepart())
                .arrive(reservationDto.getArrive())
                .hopePrice(reservationDto.getHopePrice())
                .hopeCompleteDateTime(reservationDto.getHopeCompleteDateTime())
                .reservationDateTime(reservationDto.getReservationDateTime())
                .message(reservationDto.getMessage())
                .comment(reservationDto.getComment())
                .build();


        return roomTourReservationRepository.save(entity);
    }
//
//    @Transactional
//    @Override
//    public void delete(String id) {
//        roomTourReservationRepository.deleteById(UUID.fromString(id));
//    }
//
//    @Transactional
//    @Override
//    public void addRoomNumber(RoomTourReservationDTO roomTourReservationDTO) {
//        String id = roomTourReservationDTO.getId();
//        RoomTourReservation roomTourReservation = roomTourReservationRepository.findById(UUID.fromString(id))
//                .orElseThrow(() -> new NoSuchElementException("찾을 수 없습니다."));
//
//        roomTourReservation.addRoomNumber(roomTourReservationDTO.getAddress(), roomTourReservationDTO.getRoomNumber());
//
//        RoomTourReservationDTO dto = roomTourReservation.toDto();
//        alarmTalkService.sendRoomTourAssignment(dto);
//    }
//
//    @Override
//    public List<RoomTourReservationDTO> getAll(Pageable pageable) {
//        Page<RoomTourReservation> tourReservations = roomTourReservationRepository.findAll(pageable);
//
//
//        return tourReservations.getContent().stream()
//                .map(RoomTourReservation::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<RoomTourReservationDTO> search(String category, String input) {
//        System.out.println("category = " + category);
//        Specification<RoomTourReservation> spec = RoomTourReservationSpecification.withDynamicQuery(category, input);
//
//
//        List<RoomTourReservation> reservations = roomTourReservationRepository.findAll(spec);
//
//        return reservations.stream()
//                .map(RoomTourReservation::toDto)
//                .collect(Collectors.toList());
//    }
}
