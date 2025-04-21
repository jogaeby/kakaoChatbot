package com.chatbot.base.controller.web;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.domain.reservation.RoomTourReservation;
import com.chatbot.base.domain.reservation.dto.RoomTourReservationDTO;
import com.chatbot.base.domain.reservation.service.RoomTourReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("reservation")
public class ReservationController {
    private final RoomTourReservationService roomTourReservationService;
    private final AlarmTalkService alarmTalkService;

    @GetMapping("roomTour/index")
    public String getRoomTourIndexPage() {
        return "roomTourReservationList";
    }

//
    @PostMapping("roomTour/roomNumber")
    public ResponseEntity<?> addRoomTourNumber(@RequestBody RoomTourReservationDTO roomTourReservationDTO) {
        try {

            roomTourReservationService.addRoomNumber(roomTourReservationDTO);

            return ResponseEntity.ok().build();
        }catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("roomTour/list")
    public ResponseEntity<List<RoomTourReservationDTO>> getInterviewReservationList(Pageable pageable) {
        try {
            List<RoomTourReservationDTO> reservationDTOS = roomTourReservationService.getAll(pageable);

            return ResponseEntity.ok(reservationDTOS);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("roomTour/search")
    public ResponseEntity searchReservations(@RequestParam(name = "input") String input, @RequestParam(name = "category") String category) {
        try {


            List<RoomTourReservationDTO> search = roomTourReservationService.search(category, input);

            return ResponseEntity
                    .ok(search);
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }

    @DeleteMapping("roomTour/{id}")
    public ResponseEntity delete(@PathVariable String id)
    {
        try {
            roomTourReservationService.delete(id);

            return ResponseEntity.ok().build();
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }
}
