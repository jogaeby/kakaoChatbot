package com.chatbot.base.controller.web;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.domain.reservation.constant.ReservationType;
import com.chatbot.base.domain.reservation.dto.ReservatonDTO;
import com.chatbot.base.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("reservation")
public class ReservationController {
    private final ReservationService reservationService;
    private final AlarmTalkService alarmTalkService;

    @GetMapping()
    public String getPage() {
        return "reservation";
    }
    @GetMapping("interview")
    public String getInterviewPage() {
        return "interview";
    }
    @GetMapping("interviewList")
    public String getInterviewListPage() {
        return "interviewList";
    }

    @GetMapping("list")
    public String getListPage() {
        return "reservationList";
    }

    @PostMapping(value = "/trial",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addReservation(@ModelAttribute ReservatonDTO reservatonDTO) {
        try {
            try {
                MultipleDetailMessageSentResponse sendStudent = alarmTalkService.sendTrialReceipt(reservatonDTO.getStudentPhone(), reservatonDTO.getStudentName(), reservatonDTO.getReservationDate());
                MultipleDetailMessageSentResponse sendTeacher = alarmTalkService.sendTrialTeacherReceipt(reservatonDTO.getTeacherPhone(), reservatonDTO.getTeacherName(), reservatonDTO.getReservationDate(), reservatonDTO.getStudentInfo());
                log.info("학생 발송 실패 사유 개수 {} 선생님 발송 실패 사유 개수 {}",sendStudent.getFailedMessageList().size(),sendTeacher.getFailedMessageList().size());
                if (sendStudent.getFailedMessageList().isEmpty() && sendTeacher.getFailedMessageList().isEmpty()) {
                    reservationService.saveTrialReservation(reservatonDTO);
                    return ResponseEntity.ok().build();
                }
                return ResponseEntity.status(400).build();
            }catch (Exception e) {
                return ResponseEntity.status(400).build();
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.status(400).build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable String id) {
        try {

            reservationService.delete(id);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping(value = "/interview",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addInterviewReservation(@ModelAttribute ReservatonDTO reservatonDTO) {
        try {
            try {
                MultipleDetailMessageSentResponse multipleDetailMessageSentResponse = alarmTalkService.sendInterviewReceipt(reservatonDTO.getTeacherPhone(), reservatonDTO.getZoomUrl(), reservatonDTO.getReservationDate());

                if (multipleDetailMessageSentResponse.getFailedMessageList().isEmpty()) {
                    reservationService.saveInterviewReservation(reservatonDTO);
                    return ResponseEntity.ok().build();
                }
                return ResponseEntity.status(400).build();
            }catch (Exception e) {
                return ResponseEntity.status(400).build();
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("interview/list")
    public ResponseEntity<List<ReservatonDTO>> getInterviewReservationList(Pageable pageable) {
        try {
            List<ReservatonDTO> reservationDTOS = reservationService.getAllByType(ReservationType.INTERVIEW, pageable);

            return ResponseEntity.ok(reservationDTOS);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("trial/list")
    public ResponseEntity<List<ReservatonDTO>> getTrialReservationList(Pageable pageable) {
        try {
            List<ReservatonDTO> reservationDTOS = reservationService.getAllByType(ReservationType.TRIAL, pageable);
            return ResponseEntity.ok(reservationDTOS);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("search")
    public ResponseEntity searchReservations(@RequestParam(name = "input") String input, @RequestParam(name = "category") String category, @RequestParam(name = "type") String type) {
        try {
            ReservationType reservationType = ReservationType.fromString(type);

            List<ReservatonDTO> search = reservationService.search(category, input, reservationType);

            return ResponseEntity
                    .ok(search);
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }
}
