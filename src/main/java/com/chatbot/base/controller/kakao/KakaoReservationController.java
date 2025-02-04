package com.chatbot.base.controller.kakao;

import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.view.KakaoChatBotView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/kakao/chatbot/reservation")
public class KakaoReservationController {
    private ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();
    private final KakaoChatBotView kakaoChatBotView;

    @PostMapping(value = "test")
    public void test(@RequestBody String chatBotRequest) {
        log.info("{}",chatBotRequest);
    }
}
