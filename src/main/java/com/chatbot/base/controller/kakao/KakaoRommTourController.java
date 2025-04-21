package com.chatbot.base.controller.kakao;

import com.chatbot.base.common.util.StringFormatterUtil;
import com.chatbot.base.domain.constant.RoomTourReservationStatus;
import com.chatbot.base.domain.reservation.dto.RoomTourReservationDTO;
import com.chatbot.base.domain.reservation.service.RoomTourReservationService;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonParamKey;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.components.ItemCard;
import com.chatbot.base.view.KakaoChatBotView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/roomtour")
public class KakaoRommTourController {
    private final RoomTourReservationService roomTourReservationService;
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();
    @PostMapping(value = "confirm")
    public ChatBotResponse confirmReceiptRoomTour(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            String location = chatBotRequest.getLocation();
            LocalDateTime visitDate = chatBotRequest.getVisitDate();
            LocalDate moveInDate = chatBotRequest.getDate();
            String name = chatBotRequest.getName();
            String gender = chatBotRequest.getGender();
            String age = chatBotRequest.getAge();
            String phone = chatBotRequest.getPhone();
            String period = chatBotRequest.getPeriod();

            RoomTourReservationDTO tourReservationDTO = RoomTourReservationDTO.builder()
                    .location(location)
                    .visitDate(visitDate)
                    .moveInDate(moveInDate)
                    .name(name)
                    .gender(gender)
                    .age(age)
                    .phone(phone)
                    .period(period)
                    .status(RoomTourReservationStatus.RECEIPT.getName())
                    .build();

            ChatBotResponse chatBotResponse = new ChatBotResponse();
            ItemCard itemCard = new ItemCard();
            itemCard.setImageTitle("룸투어 신청정보","룸투어 신청정보를 다시 확인해주세요.");
            itemCard.addItemList("지점명",location);
            itemCard.addItemList("투어 희망일", StringFormatterUtil.formatDate(visitDate.toLocalDate())+" "+visitDate.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            itemCard.addItemList("입주 예정일",StringFormatterUtil.formatDate(moveInDate));
            itemCard.addItemList("거주기간",period);
            itemCard.addItemList("성함",name);
            itemCard.addItemList("성별",gender);
            itemCard.addItemList("연령대",age);
            itemCard.addItemList("연락처",phone);
            itemCard.setTitle("해당 내용으로 룸투어 신청하시겠습니까?");

            chatBotResponse.addItemCard(itemCard);
            chatBotResponse.addQuickButton("다시입력",ButtonAction.블럭이동,"68005cd828fcaa18c05d0449");
            chatBotResponse.addQuickButton("룸투어 신청하기",ButtonAction.블럭이동,"680062d90d2e457e10df68f5", ButtonParamKey.roomTourReservation,tourReservationDTO);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("confirmReceiptRoomTour: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "receipt")
    public ChatBotResponse receiptRoomTour(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            RoomTourReservationDTO roomTourReservation = chatBotRequest.getRoomTourReservation();
            roomTourReservationService.receipt(roomTourReservation);

            ChatBotResponse chatBotResponse = new ChatBotResponse();
            chatBotResponse.addSimpleText("룸투어 신청을 정상적으로 완료하였습니다.");
            return chatBotResponse;
        }catch (Exception e) {
            log.error("receiptRoomTour: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

}
