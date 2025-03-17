package com.chatbot.base.controller.web;

import com.chatbot.base.annotation.PassAuth;
import com.chatbot.base.common.HttpService;
import com.chatbot.base.domain.member.dto.MemberDTO;
import com.chatbot.base.domain.product.constant.ProductStatus;
import com.chatbot.base.domain.product.dto.ProductDTO;
import com.chatbot.base.domain.product.service.ProductService;
import com.chatbot.base.domain.reservation.dto.ReservatonDTO;
import com.chatbot.base.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("reservation")
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping()
    public String getProductPage() {
        return "reservation";
    }

    @PostMapping(name = "trial",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addReservation(@ModelAttribute ReservatonDTO reservatonDTO) {
        try {

            reservationService.saveTrialReservation(reservatonDTO);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.status(400).build();
        }
    }
}
