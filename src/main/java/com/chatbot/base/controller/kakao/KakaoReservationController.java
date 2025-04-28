package com.chatbot.base.controller.kakao;

import com.chatbot.base.common.util.StringFormatterUtil;
import com.chatbot.base.domain.reservation.Reservation;
import com.chatbot.base.domain.reservation.dto.ReservationDto;
import com.chatbot.base.domain.reservation.service.ReservationService;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonParamKey;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.components.Carousel;
import com.chatbot.base.dto.kakao.response.property.components.ItemCard;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/reservation")
public class KakaoReservationController {
    private final ReservationService reservationService;
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();
    @PostMapping(value = "confirm")
    public ChatBotResponse confirmReceipt(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            String name = chatBotRequest.getName();
            String phone = chatBotRequest.getPhone();
            String depart = chatBotRequest.getDepart();
            String arrive = chatBotRequest.getArrive();
            String hopePrice = chatBotRequest.getHopePrice();
            LocalDateTime hopeCompleteDateTime = chatBotRequest.getHopeCompleteDateTime();
            LocalDateTime reservationDateTime = chatBotRequest.getReservationDateTime();
            String message = chatBotRequest.getMessage();
            String comment = chatBotRequest.getComment();

            ReservationDto reservationDto = ReservationDto.builder()
                    .name(name)
                    .phone(phone)
                    .depart(depart)
                    .arrive(arrive)
                    .hopePrice(hopePrice)
                    .hopeCompleteDateTime(hopeCompleteDateTime)
                    .reservationDateTime(reservationDateTime)
                    .message(message)
                    .comment(comment)
                    .build();


            ChatBotResponse chatBotResponse = new ChatBotResponse();
            Carousel carousel = new Carousel();
            ItemCard itemCard = new ItemCard();
            itemCard.setItemListAlignment("right");
            itemCard.setImageTitle("서비스 접수정보","접수정보를 다시 확인해주세요.");
            itemCard.addItemList("이름",name);
            itemCard.addItemList("연락처",phone);
            itemCard.addItemList("예약날짜",StringFormatterUtil.formatDateTime(reservationDateTime));
            itemCard.addItemList("희망완료날짜",StringFormatterUtil.formatDateTime(hopeCompleteDateTime));
            itemCard.addItemList("희망요금",StringFormatterUtil.formatCurrency(hopePrice)+"원");
            itemCard.setTitle("해당 내용으로 서비스를 신청하시겠습니까?");


            TextCard departTextCard = new TextCard();
            departTextCard.setTitle("출발지");
            departTextCard.setDescription(depart);
            carousel.addComponent(departTextCard);

            TextCard arriveTextCard = new TextCard();
            arriveTextCard.setTitle("도착지");
            arriveTextCard.setDescription(arrive);
            carousel.addComponent(arriveTextCard);

            TextCard messageTextCard = new TextCard();
            messageTextCard.setTitle("의뢰내용");
            messageTextCard.setDescription(message);
            carousel.addComponent(messageTextCard);

            TextCard commentTextCard = new TextCard();
            commentTextCard.setTitle("추가요청사항");
            commentTextCard.setDescription(comment);
            carousel.addComponent(commentTextCard);

            chatBotResponse.addItemCard(itemCard);
            chatBotResponse.addCarousel(carousel);
            chatBotResponse.addQuickButton("다시입력",ButtonAction.블럭이동,"68026f3c2a22a85698b1aed6");
            chatBotResponse.addQuickButton("신청하기",ButtonAction.블럭이동,"680062d90d2e457e10df68f5", ButtonParamKey.reservation,reservationDto);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("confirmReceipt: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "receipt")
    public ChatBotResponse receiptReservation(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ReservationDto reservation = chatBotRequest.getReservation();
            Reservation receipt = reservationService.receipt(reservation);
            Long id = receipt.getId();

            ChatBotResponse chatBotResponse = new ChatBotResponse();
            chatBotResponse.addSimpleText("["+id+"] 서비스 신청을 정상적으로 완료하였습니다.");
            return chatBotResponse;
        }catch (Exception e) {
            log.error("receiptReservation: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("서비스 신청을 실패하였습니다.");
        }
    }

}
