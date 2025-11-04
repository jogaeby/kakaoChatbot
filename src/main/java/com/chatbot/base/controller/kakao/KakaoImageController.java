package com.chatbot.base.controller.kakao;

import com.chatbot.base.dto.kakao.constatnt.button.ButtonAction;
import com.chatbot.base.dto.kakao.request.ChatBotRequest;
import com.chatbot.base.dto.kakao.response.ChatBotExceptionResponse;
import com.chatbot.base.dto.kakao.response.ChatBotResponse;
import com.chatbot.base.dto.kakao.response.property.common.Button;
import com.chatbot.base.dto.kakao.response.property.components.BasicCard;
import com.chatbot.base.dto.kakao.response.property.components.Carousel;
import com.chatbot.base.dto.kakao.response.property.components.TextCard;
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
@RequestMapping(value = "/kakao/chatbot/imagae")
public class KakaoImageController {
    private ChatBotExceptionResponse chatBotExceptionResponse = new ChatBotExceptionResponse();

    @PostMapping("medicine/check")
    public ChatBotResponse fallback(@RequestBody ChatBotRequest chatBotRequest) {
        try {
            ChatBotResponse chatBotResponse = new ChatBotResponse();
            List<String> images = chatBotRequest.getImages();

            Carousel carousel = new Carousel();

            images.forEach(url -> {
                log.info("{}",url);
                BasicCard basicCard = new BasicCard();
                basicCard.setThumbnail(url,true);

                carousel.addComponent(basicCard);
            });
            Button button = new Button("약사 연결", ButtonAction.상담원연결,"");


            TextCard textCard = new TextCard();
            textCard.setDescription("하단의 [약사 연결] 버튼을 눌러 약사 연결 후 말씀해주세요.");
            textCard.setButtons(button);

            chatBotResponse.addCarousel(carousel);
            chatBotResponse.addTextCard(textCard);
            return chatBotResponse;
        }catch (Exception e) {
            log.error("ERROR {}",e.getMessage(),e);
            return chatBotExceptionResponse.createException();
        }

    }
}
