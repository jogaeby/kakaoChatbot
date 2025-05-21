package com.chatbot.base.controller.kakao;

import com.chatbot.base.domain.event.EventService;
import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
import com.chatbot.base.view.KakaoChatBotView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
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

    private final KakaoChatBotView kakaoChatBotView;

    @PostMapping(value = "validation")
    public ChatBotResponse authEvent(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();

            String appUserId = chatBotRequest.getAppUserId();

            if (appUserId == null) throw new AuthenticationException("appUserId 없음");

            TextCard textCard = new TextCard();
            textCard.setDescription("아래 버튼을 눌러 이벤트를 참여해주세요.");
            chatBotResponse.addTextCard(textCard);
            chatBotResponse.addQuickButton(new Button("이벤트 참여하기", ButtonAction.블럭이동,"6821606d23dc6c3328144ff7"));
            return chatBotResponse;
        }catch (AuthenticationException e) {
            log.error("[카카오싱크 실패] receiptReservation: {}", e.getMessage(), e);
            return kakaoChatBotView.authView();
        }catch (Exception e) {
            log.error("receiptReservation: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException();
        }
    }

    @PostMapping(value = "receipt")
    public ChatBotResponse receiptEvent(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            List<String> images = chatBotRequest.getImages();

            String appUserId = chatBotRequest.getAppUserId();
            if (appUserId == null) throw new AuthenticationException("appUserId 없음");


            String id = eventService.onePickEvent(images, appUserId);

            ChatBotResponse chatBotResponse = new ChatBotResponse();
            chatBotResponse.addSimpleText("접수번호["+id+"]이(가) 제출 완료되었습니다\n" +
                    "참여해 주셔서 감사합니다");
            return chatBotResponse;
        }catch (AuthenticationException e) {
            log.error("[카카오싱크 실패] receiptReservation: {}", e.getMessage(), e);
            return kakaoChatBotView.authView();
        }catch (Exception e) {
            log.error("receiptReservation: {}", e.getMessage(), e);
            return chatBotExceptionResponse.createException("제출을 실패하였습니다.");
        }
    }

}
