package com.chatbot.base.controller.kakao;

import com.chatbot.base.common.ImageUtil;
import com.chatbot.base.common.util.KakaoApiService;
import com.chatbot.base.domain.event.EventService;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/event")
public class KakaoReservationController {

    private final EventService eventService;
    private final ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    @PostMapping(value = "receipt")
    public ChatBotResponse receiptEvent(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            List<String> images = chatBotRequest.getImages();
            log.info("receiptEvent, images: {}", images);

            String appUserId = chatBotRequest.getAppUserId();

            String id = eventService.onePickEvent(images, appUserId);

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
