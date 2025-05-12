package com.chatbot.base.controller.kakao;

import com.chatbot.base.common.AlarmTalkService;
import com.chatbot.base.common.GoogleSheetUtil;
import com.chatbot.base.common.util.KakaoApiService;
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
import com.chatbot.base.dto.kakao.sync.KakaoProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/event")
public class KakaoReservationController {
    private final GoogleSheetUtil googleSheetUtil;
    private final KakaoApiService kakaoApiService;
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    @PostMapping(value = "receipt")
    public ChatBotResponse receiptEvent(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            List<String> images = chatBotRequest.getImages();
            log.info("receiptEvent, images: {}", images);

//            String appUserId = chatBotRequest.getAppUserId();
//
//            KakaoProfileDto kakaoProfile = kakaoApiService.getKakaoProfile(appUserId);
            long id = System.currentTimeMillis();
//            String name = kakaoProfile.getKakaoAccount().getName();
//            String nickName = kakaoProfile.getProperties().getNickname();
//            String profileImage = kakaoProfile.getProperties().getThumbnailImage();
//            String gender = kakaoProfile.getGender();
//            String birthday = kakaoProfile.getBirthDate();
//            String phone = kakaoProfile.getKakaoAccount().getPhoneNumber();
//            LocalDateTime localDateTime = LocalDateTime.now();
//
//
//            List<Object> rowData = new ArrayList<>();
//            rowData.add(id);
//            rowData.add(name);
//
//
//
//
//            googleSheetUtil.appendToSheet("","",rowData);

            ChatBotResponse chatBotResponse = new ChatBotResponse();
            chatBotResponse.addSimpleText("["+id+"] 제출 완료되었습니다\n" +
                    "참여해 주셔서 감사합니다");
            return chatBotResponse;
        }catch (Exception e) {
            log.error("receiptReservation: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("서비스 신청을 실패하였습니다.");
        }
    }

}
